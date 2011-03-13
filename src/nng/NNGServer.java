package nng;

import com.jme3.network.connection.Server;
import com.jme3.network.events.MessageAdapter;
import com.jme3.network.message.Message;
import com.jme3.network.message.StreamDataMessage;
import com.jme3.network.serializing.Serializable;
import com.jme3.network.serializing.Serializer;

//import gctest.TestMessagesServer.PingMessage;
//import gctest.TestMessagesServer.PongMessage;

import java.io.IOException;

//import nng.NNGClient.HelloMessage;

//@Serializable
//public static class PingMessage extends Message {
//}
//
//
//@Serializable
//public static class PongMessage extends Message {
//}
public class NNGServer {
	   @Serializable
	    public static class ServerMessage extends Message {
	    	public String msg;
	    }
	    @Serializable
	    public static class ClientMessage extends Message {
	    	public String msg;
	    }

	private static class MessageResponder extends MessageAdapter {
		@Override
		public void messageReceived(Message message) {
			if (message instanceof StreamDataMessage) {
				System.out.println("Received ping message!" + message);
				System.out.println("Sending pong message..");
				// message.getClient().send(new PongMessage());
			} else if (message instanceof ClientMessage) {
				try {
					ServerMessage SrvMessage = new ServerMessage();
					
					SrvMessage.msg = "Server ansver ..." +message.getClient().getClientID();
					System.out.println("Received message: "	+ ((ClientMessage) message).msg);
					System.out.println("Sending message.. " + SrvMessage.msg);
					message.getClient().send(SrvMessage);
					// System.out.println(helloMessage.msg);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			} else {
				System.out.println("Wrong format message!");
			}
		}
	}

	// server.addMessageListener(this, HelloMessage.class);
	public static void main(String[] args) throws IOException , InterruptedException{
		Serializer.registerClass(StreamDataMessage.class);
		Serializer.registerClass(ServerMessage.class);
		Serializer.registerClass(ClientMessage.class);
		// Application app = new Application();
		// app.start(JmeContext.Type.Headless);
		Server myServer = new Server(5110, 5110);
		myServer.start();
		// myServer.getConnectors()
		//myServer.addMessageListener(new MessageResponder(),	StreamDataMessage.class);
		myServer.addMessageListener(new MessageResponder(), ClientMessage.class);
//	       Client client = new Client("localhost", 5110, 5110);
//	        client.start();



//			client.addMessageListener(new MessageResponder(), ServerMessage.class);
//		       Thread.sleep(100);
//
//		       ClientMessage ms = new ClientMessage();
//		       ms.msg="test" ;
//			client.send(ms);
//			System.out.println(client.getPlayerID() + " CID="
//					+ client.getClientID());
//
//	      System.out.println("Sending ping message..");
	//       client.send(new PingMessage());

	}
}
