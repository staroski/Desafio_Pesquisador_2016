package test.coap;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.EndpointManager;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class SeniorCoapServer extends CoapServer {

	private class DataSampleResource extends CoapResource {

		public DataSampleResource() {
			super("senior-data-sample");
		}

		@Override
		public void handlePOST(CoapExchange exchange) {
			exchange.accept();
			String payload = exchange.getRequestText();
			exchange.respond(payload == null || payload.isEmpty() //
					? ResponseCode.UNSUPPORTED_CONTENT_FORMAT //
					: ResponseCode.VALID);
		}
	}

	private static final int COAP_PORT = NetworkConfig.getStandard().getInt(NetworkConfig.Keys.COAP_PORT);

	public static void main(String[] args) {
		try {
			SeniorCoapServer server = new SeniorCoapServer();
			server.addEndpoints();
			server.start();
		} catch (SocketException e) {
			System.err.println("Failed to initialize server: " + e.getMessage());
		}
	}

	public SeniorCoapServer() throws SocketException {
		add(new DataSampleResource());
	}

	private void addEndpoints() {
		for (InetAddress addr : EndpointManager.getEndpointManager().getNetworkInterfaces()) {
			if (addr instanceof Inet4Address || addr.isLoopbackAddress()) {
				InetSocketAddress bindToAddress = new InetSocketAddress(addr, COAP_PORT);
				addEndpoint(new CoapEndpoint(bindToAddress));
			}
		}
	}
}