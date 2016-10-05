package test.coap;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.EndpointManager;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class TestCoapServer extends CoapServer {

	private class DataSampleResource extends CoapResource {

		public DataSampleResource() {
			super(SENSOR_DATA);
		}

		@Override
		public void handlePOST(CoapExchange exchange) {
			byte[] payload = exchange.getRequestPayload();
			System.out.println(payload.length + " bytes received");
			exchange.respond(new String(payload));
		}
	}

	public static String SENSOR_DATA = "sensorData";

	private static final int COAP_PORT = NetworkConfig.getStandard().getInt(NetworkConfig.Keys.COAP_PORT);

	public static void main(String[] args) {
		try {
			TestCoapServer server = new TestCoapServer();
			server.start();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public TestCoapServer() throws SocketException {
		for (InetAddress addr : EndpointManager.getEndpointManager().getNetworkInterfaces()) {
			if (addr instanceof Inet4Address || addr.isLoopbackAddress()) {
				InetSocketAddress bindToAddress = new InetSocketAddress(addr, COAP_PORT);
				addEndpoint(new CoapEndpoint(bindToAddress));
			}
		}
		add(new DataSampleResource());
	}
}