package test.mqtt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class TestMqttClient {

	public static void main(String[] args) {
		try {
			String csvDir = "E:\\Desafio_Pesquisador_2016\\prototipos\\data-sample";
			String serverURI = "tcp://localhost:1883";
			String clientId = "data-sample";
			TestMqttClient program = new TestMqttClient(csvDir, serverURI, clientId);
			program.execute();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private final String csvDir;
	private final String serverURI;
	private final String clientId;

	public TestMqttClient(String csvDir, String serverURI, String clientId) {
		this.csvDir = csvDir;
		this.serverURI = serverURI;
		this.clientId = clientId;
	}

	private void execute() throws Exception {
		File csvDir = new File(this.csvDir);
		File[] csvFiles = csvDir.listFiles();
		for (File csv : csvFiles) {
			MqttClient client = newClient(serverURI, clientId);
			try (BufferedReader reader = new BufferedReader(new FileReader(csv))) {
				String payload = reader.readLine(); // ignorar header do CSV
				while ((payload = reader.readLine()) != null && !(payload = payload.trim()).isEmpty()) {
					sendData(payload, client);
				}
			}
			client.disconnect();
		}
	}

	private MqttClient newClient(String serverURI, String clientId) throws MqttException {
		MemoryPersistence persistence = new MemoryPersistence();
		MqttClient mqttClient = new MqttClient(serverURI, clientId, persistence);
		MqttConnectOptions connOpts = new MqttConnectOptions();
		connOpts.setCleanSession(true);
		mqttClient.connect(connOpts);
		return mqttClient;
	}

	private void sendData(String payload, MqttClient client) {
		String topic = "sample-data";
		int qos = 2;
		try {
			MqttMessage message = new MqttMessage(payload.getBytes());
			message.setQos(qos);
			client.publish(topic, message);
		} catch (MqttException me) {
			System.out.println("reason " + me.getReasonCode());
			System.out.println("msg " + me.getMessage());
			System.out.println("loc " + me.getLocalizedMessage());
			System.out.println("cause " + me.getCause());
			System.out.println("excep " + me);
			me.printStackTrace();
		}

		// CoapResponse response = client.post(payload,
		// MediaTypeRegistry.TEXT_PLAIN);
		// if (response != null) {
		// System.out.println(Utils.prettyPrint(response));
		// } else {
		// System.out.println("No response received.");
		// }
	}
}