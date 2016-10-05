package test.coap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Queue;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

public class TestCoapClient {

	private class ClientCallback implements CoapHandler {

		@Override
		public void onError() {
			System.err.println("ERROR");
		}

		@Override
		public void onLoad(CoapResponse response) {
			Timer timer = timers.poll();
			printLog("%d, %d, %d, %d, %d\n", response.getPayload().length, timer.elapsed(), memory.used(), memory.free(), memory.total());
		}
	}

	public static void main(String args[]) {
		final Timer timer = new Timer();
		try {
			if (args.length != 4) {
				System.out.println("usage:");
				System.out.println("\t<java> <class-name> <data-dir> <log-file> <uri> <interval>");
				System.out.println("where:");
				System.out.println("\t<java>       the java command to be used for example: java or javaw");
				System.out.println("\t<class-name> " + TestCoapClient.class.getName());
				System.out.println("\t<data-dir>   directory containing csv data samples");
				System.out.println("\t<log-file>   file to write performance info, for example: coap-performance.csv");
				System.out.println("\t<uri>        the server URI, for example: coap://localhost:5683");
				System.out.println("\t<interval>   the data send interval in miliseconds, for example: 300");
			} else {
				String csvDir = args[0];
				String logFile = args[1];
				String serverURI = args[2];
				int interval = Integer.parseInt(args[3]);
				TestCoapClient testMqttClient = new TestCoapClient(csvDir, logFile);
				System.out.println("client running...");
				testMqttClient.execute(serverURI, interval);
				System.out.println("finished after " + timer.elapsed() + " ms");
			}
		} catch (Throwable t) {
			System.err.println("aborted after " + timer.elapsed() + " ms");
			t.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

	private final ClientCallback CLIENT_CALLBACK = new ClientCallback();

	private final Queue<Timer> timers;
	private final Memory memory;
	private final File csvDir;
	private final File logFile;
	private PrintWriter logger;

	public TestCoapClient(String csvDir, String logFile) throws IOException {
		this.csvDir = new File(csvDir);
		this.logFile = new File(logFile);
		this.memory = new Memory();
		this.timers = new LinkedList<>();
	}

	public void execute(String serverURI, int interval) throws Exception {
		final CoapClient client = newClient(serverURI);
		try {
			System.out.printf("creating log file \"%s\"...\n", logFile.getAbsolutePath());
			this.logger = new PrintWriter(logFile);
			printLog("%s, %s, %s, %s, %s\n", "bytes_send", "elapsed_time", "used_memory", "free_memory", "total_memory");
			File[] csvFiles = csvDir.listFiles();
			Timer timer = new Timer();
			int dataLimit = 1000;
			FILES: for (File csv : csvFiles) {
				try (BufferedReader reader = new BufferedReader(new FileReader(csv))) {
					String data = reader.readLine();
					while ((data = reader.readLine()) != null) {
						if (!(data = data.trim()).isEmpty()) {
							dataLimit--;
							sendData(data, client, logger);
							if (dataLimit == 0) {
								break FILES;
							}
							timer.waitMilis(interval);
						}
					}
				} catch (Exception e) {
					System.err.printf("ERROR!\n");
					e.printStackTrace(System.err);
				}
			}
		} finally {
			logger.flush();
			logger.close();
			System.out.printf("log file \"%s\" created!\n", logFile.getAbsolutePath());
			client.shutdown();
		}
	}

	private CoapClient newClient(String serverURI) {
		CoapClient client = new CoapClient(serverURI + "/" + TestCoapServer.SENSOR_DATA);
		return client;
	}

	private void printLog(String format, Object... args) {
		String log = String.format(format, args);
		logger.print(log);
		System.out.print(log);
	}

	private void sendData(String data, CoapClient client, PrintWriter logOutput) throws Exception {
		timers.add(new Timer());
		client.post(CLIENT_CALLBACK, data.getBytes(), MediaTypeRegistry.TEXT_CSV);
	}
}
