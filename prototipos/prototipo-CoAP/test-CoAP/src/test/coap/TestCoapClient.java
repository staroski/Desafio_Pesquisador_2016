package test.coap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

public class TestCoapClient {

	public static void main(String args[]) {
		try {
			String csvDir = "E:\\Desafio_Pesquisador_2016\\prototipos\\data-sample";
			String serverURI = "coap://localhost:5683";
			String clientId = "data-sample";
			TestCoapClient program = new TestCoapClient(csvDir, serverURI, clientId);
			program.execute();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private final String csvDir;
	private final String serverURI;
	private final String clientId;

	public TestCoapClient(String csvDir, String serverURI, String clientId) {
		this.csvDir = csvDir;
		this.serverURI = serverURI;
		this.clientId = clientId;
	}

	private void execute() throws IOException {
		File csvDir = new File(this.csvDir);
		File[] csvFiles = csvDir.listFiles();
		for (File csv : csvFiles) {
			CoapClient client = newClient(serverURI, clientId);
			try (BufferedReader reader = new BufferedReader(new FileReader(csv))) {
				String payload = reader.readLine(); // ignorar header do CSV
				while ((payload = reader.readLine()) != null && !(payload = payload.trim()).isEmpty()) {
					sendData(payload, client);
				}
			}
		}
	}

	private CoapClient newClient(String serverURI, String clientId) {
		return new CoapClient(serverURI + "/" + clientId);
	}

	private void sendData(String payload, CoapClient client) {
		CoapResponse response = client.post(payload, MediaTypeRegistry.TEXT_PLAIN);
		if (response != null) {
			System.out.println(Utils.prettyPrint(response));
		} else {
			System.out.println("No response received.");
		}
	}
}
