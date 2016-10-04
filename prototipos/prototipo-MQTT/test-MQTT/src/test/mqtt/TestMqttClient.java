package test.mqtt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Queue;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class TestMqttClient {

	private class ClientCallback implements MqttCallback {

		@Override
		public void connectionLost(Throwable cause) {}

		@Override
		public void deliveryComplete(IMqttDeliveryToken token) {}

		@Override
		public void messageArrived(String topic, MqttMessage message) throws Exception {
			Timer timer = timers.poll();
			printLog("%d, %d, %d, %d, %d\n", message.getPayload().length, timer.elapsed(), memory.used(), memory.free(), memory.total());
		}
	}

	private static String TOPIC = "sensorData";

	public static void main(String[] args) {
		final Timer timer = new Timer();
		try {
			if (args.length != 5) {
				System.out.println("usage:");
				System.out.println("\t<java> <class-name> <data-dir> <log-file> <uri> <id> <interval>");
				System.out.println("where:");
				System.out.println("\t<java>       the java command to be used for example: java or javaw");
				System.out.println("\t<class-name> " + TestMqttClient.class.getName());
				System.out.println("\t<data-dir>   directory containing csv data samples");
				System.out.println("\t<log-file>   file to write performance info, for example: mqtt-performance.csv");
				System.out.println("\t<uri>        the server URI, for example: tcp://localhost:1883");
				System.out.println("\t<id>         the client identification, for example: test-mqtt-client");
				System.out.println("\t<interval>   the data send interval in miliseconds, for example: 300");
			} else {
				String csvDir = args[0];
				String logFile = args[1];
				String serverURI = args[2];
				String clientId = args[3];
				int interval = Integer.parseInt(args[4]);
				TestMqttClient testMqttClient = new TestMqttClient(csvDir, logFile);
				System.out.println("client running...");
				testMqttClient.execute(serverURI, clientId, interval);
				System.out.println("finished after " + timer.elapsed() + " ms");
			}
		} catch (Throwable t) {
			System.err.println("aborted after " + timer.elapsed() + " ms");
			t.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

	private final Queue<Timer> timers;
	private final Memory memory;
	private final File csvDir;
	private final File logFile;
	private PrintWriter logger;

	public TestMqttClient(String csvDir, String logFile) throws IOException {
		this.csvDir = new File(csvDir);
		this.logFile = new File(logFile);
		this.memory = new Memory();
		this.timers = new LinkedList<>();
	}

	public void execute(String serverURI, String clientId, int interval) throws Exception {
		final MqttClient client = newClient(serverURI, clientId);
		client.subscribe(TOPIC);
		try {
			System.out.printf("creating log file \"%s\"...\n", logFile.getAbsolutePath());
			this.logger = new PrintWriter(logFile);
			printLog("%s, %s, %s, %s, %s\n", "bytes_send", "elapsed_time", "used_memory", "free_memory", "total_memory");
			File[] csvFiles = csvDir.listFiles();
			Timer timer = new Timer();
			for (File csv : csvFiles) {
				try (BufferedReader reader = new BufferedReader(new FileReader(csv))) {
					String data = reader.readLine();
					while ((data = reader.readLine()) != null) {
						if (!(data = data.trim()).isEmpty()) {
							sendData(data, client, logger);
							timer.waitMilis(interval);
						}
					}
				} catch (Exception e) {
					System.err.printf("ERROR!\n");
					e.printStackTrace(System.err);
				}
				break;
			}
		} finally {
			logger.flush();
			logger.close();
			System.out.printf("log file \"%s\" created!\n", logFile.getAbsolutePath());
			client.unsubscribe(TOPIC);
			client.disconnect();
		}
	}

	private MqttClient newClient(String serverURI, String clientId) throws MqttException {
		MemoryPersistence persistence = new MemoryPersistence();
		MqttClient client = new MqttClient(serverURI, clientId, persistence);
		MqttConnectOptions connOpts = new MqttConnectOptions();
		connOpts.setCleanSession(false);
		client.connect(connOpts);
		client.setCallback(new ClientCallback());
		return client;
	}

	private void printLog(String format, Object... args) {
		String log = String.format(format, args);
		logger.print(log);
		System.out.print(log);
	}

	private void sendData(String data, MqttClient client, PrintWriter logOutput) throws Exception {
		timers.add(new Timer());
		client.publish(TOPIC, data.getBytes(), 2, false);
	}
}