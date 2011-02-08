package nng;

import com.jme3.network.connection.Client;
//import com.jme3.network.connection.Server;
import com.jme3.network.events.MessageAdapter;
import com.jme3.network.message.Message;
import com.jme3.network.message.StreamDataMessage;
import com.jme3.network.serializing.Serializer;
import com.jme3.network.serializing.Serializable;
//import com.jme3.network.serializing.Serializer;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

//import nng.NNGServer.MessageResponder;
public class NNGClient {
	@Serializable()
	public class HelloMessage extends Message {
		public String hello = "Hello!";
	}

	private static class MessageResponder extends MessageAdapter {
		@Override
		public void messageReceived(Message message) {
			// This message is of type HelloMessage, so we don't have to check.
			HelloMessage helloMessage = (HelloMessage) message;
			System.out.println(helloMessage.hello);
			// helloMessage.hello = "Hi!";
			// message.getClient().send(helloMessage);
		}
	}

	public static void main(String[] args) throws IOException {
		Serializer.registerClass(StreamDataMessage.class);
		Serializer.registerClass(HelloMessage.class);
		// Client client = new Client("localhost", 4040, 5050);
		Client client = new Client();

		List<InetAddress> foundHosts = client.discoverHosts(5050, 5000);
		for (InetAddress host : foundHosts) {
			client.connect(host.getCanonicalHostName(), 4040, 5050);
		}
		client.start();
		client.addMessageListener(new MessageResponder(), HelloMessage.class);
		StreamDataMessage m = new StreamDataMessage();
		byte[] b = new byte[5];
		b[0] = 1;
		b[1] = 13;
		m.setData(b);
		// client.send(m);
		client.send(new HelloMessage());
		System.out.println(client.getPlayerID() + " CID="
				+ client.getClientID());
	}
}
