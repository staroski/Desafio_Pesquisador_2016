package test.mqtt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class TestMqttClient {

	public static void main(String[] args) {
		String csvDir = "E:\\Desafio_Pesquisador_2016\\prototipos\\data-sample";
		String logFile = "E:\\Desafio_Pesquisador_2016\\prototipos\\log-mqtt.csv";
		String serverURI = "tcp://localhost:1883";
		String clientId = "data-sample";
		int interval = 300;
		Timer timer = new Timer();
		try {
			TestMqttClient testMqttClient = new TestMqttClient(csvDir, logFile);
			System.out.println("client running...");
			testMqttClient.execute(serverURI, clientId, interval);
			System.out.println("finished after " + timer.elapsed() + " ms");
		} catch (Throwable t) {
			System.err.println("aborted after " + timer.elapsed() + " ms");
			t.printStackTrace();
		}
	}

	private final Timer timer;
	private final Memory memory;

	private final File csvDir;
	private final File logFile;

	private PrintWriter logger;

	public TestMqttClient(String csvDir, String logFile) throws IOException {
		this.csvDir = new File(csvDir);
		this.logFile = new File(logFile);
		this.memory = new Memory();
		this.timer = new Timer();
	}

	public void execute(String serverURI, String clientId, int interval) throws Exception {
		final MqttClient client = newClient(serverURI, clientId);
		try {
			System.out.printf("creating log file \"%s\"...\n", logFile.getAbsolutePath());
			this.logger = new PrintWriter(logFile);
			printLog("%s, %s, %s, %s, %s\n", "bytes_send", "elapsed_time", "used_memory", "free_memory", "total_memory");
			File[] csvFiles = csvDir.listFiles();
			Timer counter = new Timer();
			StringBuilder buffer = new StringBuilder();
			int totalFiles = 0;
			for (File csv : csvFiles) { // lê cada arquivo do diretório
				totalFiles++;
				System.out.printf("reading file %d \"%s\"...    ", totalFiles, csv.getAbsolutePath());
				try (BufferedReader reader = new BufferedReader(new FileReader(csv))) {
					String line = reader.readLine(); // ignora a primeira linha, que é o cabeçalho do CSV
					while ((line = reader.readLine()) != null) {
						if (!(line = line.trim()).isEmpty()) {
							buffer.append(line).append('\n'); // vai enchendo o buffer até chegar a hora de enviar os dados
						}
						if (counter.hasElapsed(interval)) { // tá na hora de enviar?
							sendData(buffer, client, logger); // sim, então envia
							buffer = new StringBuilder(); // reinicializa o buffer
							counter.reset(); // reseta o timer
						}
					}
					// chegou ao fim do arquivo
					if (buffer.length() > 0) { // ainda tem dados pendentes de envio?
						long timeToWait = interval - counter.elapsed();
						if (timeToWait > 0) { // já pode enviar?
							Thread.sleep(timeToWait); // não, então aguarda um pouquinho
						}
						sendData(buffer, client, logger); // agora envia
						buffer = new StringBuilder(); // reinicializa o buffer
						counter.reset(); // reseta o timer
					}
					System.out.printf("OK!\n");
				} catch (Exception e) {
					System.err.printf("ERROR!\n");
					e.printStackTrace(System.err);
				}
			}
		} finally {
			logger.flush();
			logger.close();
			System.out.printf("log file \"%s\" created!\n", logFile.getAbsolutePath());
			client.disconnect();
		}
	}

	private void printLog(String format, Object... args) {
		logger.print(String.format(format, args));
	}

	private MqttClient newClient(String serverURI, String clientId) throws MqttException {
		MemoryPersistence persistence = new MemoryPersistence();
		MqttClient client = new MqttClient(serverURI, clientId, persistence);
		MqttConnectOptions connOpts = new MqttConnectOptions();
		connOpts.setCleanSession(false);
		client.connect(connOpts);
		return client;
	}

	private void sendData(StringBuilder buffer, MqttClient client, PrintWriter logOutput) throws Exception {
		String topic = "sample-data";
		int qos = 2;
		boolean retained = true;
		byte[] payload = buffer.toString().getBytes();
		timer.reset();
		client.publish(topic, payload, qos, retained);
		printLog("%d, %d, %d, %d, %d\n", payload.length, timer.elapsed(), memory.used(), memory.free(), memory.total());
	}
}