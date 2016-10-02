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
			String serverURI = "coap://localhost:5683/data-sample";
			TestCoapClient program = new TestCoapClient(csvDir, serverURI);
			program.execute();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private final String csvDir;
	private final String serverURI;

	public TestCoapClient(String csvDir, String serverURI) {
		this.csvDir = csvDir;
		this.serverURI = serverURI;
	}

	private void execute() throws IOException {
		final File csvDir = new File(this.csvDir);
		File[] csvFiles = csvDir.listFiles();
		for (File csv : csvFiles) {
			final CoapClient client = new CoapClient(serverURI);
			try (BufferedReader reader = new BufferedReader(new FileReader(csv))) {
				String payload = reader.readLine(); // ignorar header do CSV
				while ((payload = reader.readLine()) != null && !(payload = payload.trim()).isEmpty()) {
					CoapResponse response = client.post(payload, MediaTypeRegistry.TEXT_PLAIN);
					showResponse(response);
				}
			}
		}
	}

	private void showResponse(CoapResponse response) {
		if (response != null) {
			System.out.println(Utils.prettyPrint(response));
		} else {
			System.out.println("No response received.");
		}
	}
}
