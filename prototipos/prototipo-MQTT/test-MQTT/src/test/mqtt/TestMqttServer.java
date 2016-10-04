package test.mqtt;

import java.util.Properties;

import io.moquette.interception.InterceptHandler;
import io.moquette.interception.messages.InterceptAcknowledgedMessage;
import io.moquette.interception.messages.InterceptConnectMessage;
import io.moquette.interception.messages.InterceptDisconnectMessage;
import io.moquette.interception.messages.InterceptPublishMessage;
import io.moquette.interception.messages.InterceptSubscribeMessage;
import io.moquette.interception.messages.InterceptUnsubscribeMessage;
import io.moquette.server.Server;

public class TestMqttServer {

	private class ServerHandler implements InterceptHandler {

		@Override
		public void onConnect(InterceptConnectMessage msg) {}

		@Override
		public void onDisconnect(InterceptDisconnectMessage msg) {}

		@Override
		public void onMessageAcknowledged(InterceptAcknowledgedMessage msg) {}

		@Override
		public void onPublish(InterceptPublishMessage msg) {
			System.out.println(msg.getPayload().array().length + " bytes received");
		}

		@Override
		public void onSubscribe(InterceptSubscribeMessage msg) {}

		@Override
		public void onUnsubscribe(InterceptUnsubscribeMessage msg) {}
	}

	public static void main(String[] args) {
		try {
			TestMqttServer server = new TestMqttServer();
			server.start();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public void start() throws Exception {
		Server server = new Server();
		Properties properties = new Properties();
		properties.setProperty("host", "localhost");
		properties.setProperty("port", "1883");
		properties.setProperty("autosave_interval", "600");
		server.startServer(properties);
		server.addInterceptHandler(new ServerHandler());
		System.out.println("MQTT server running");
	}
}
