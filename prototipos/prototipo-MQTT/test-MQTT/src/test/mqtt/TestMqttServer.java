package test.mqtt;

import java.io.InputStream;
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
		public void onConnect(InterceptConnectMessage msg) {
			System.out.println("onConnect");
		}

		@Override
		public void onDisconnect(InterceptDisconnectMessage msg) {
			System.out.println("onDisconnect");
		}

		@Override
		public void onMessageAcknowledged(InterceptAcknowledgedMessage msg) {
			System.out.println("onMessageAcknowledged");
		}

		@Override
		public void onPublish(InterceptPublishMessage msg) {
			System.out.println("onPublish");
		}

		@Override
		public void onSubscribe(InterceptSubscribeMessage msg) {
			System.out.println("onSubscribe");
		}

		@Override
		public void onUnsubscribe(InterceptUnsubscribeMessage msg) {
			System.out.println("onUnsubscribe");
		}

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
		InputStream config = getClass().getResourceAsStream("/moquette.conf");
		Properties properties = new Properties();
		properties.load(config);
		server.startServer(properties);
		server.addInterceptHandler(new ServerHandler());
	}
}
