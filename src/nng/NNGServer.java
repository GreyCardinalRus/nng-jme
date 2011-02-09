package nng;
import com.jme3.system.JmeContext;
import com.jme3.system.JmeContext.Type;
import com.jme3.app.Application;
//import com.jme3.network.connection.Client;
import com.jme3.network.connection.Server;
import com.jme3.network.events.MessageAdapter;
import com.jme3.network.message.Message;
import com.jme3.network.message.StreamDataMessage;
import com.jme3.network.serializing.Serializable;
import com.jme3.network.serializing.Serializer;

import gctest.TestMessagesServer.PingMessage;
import gctest.TestMessagesServer.PongMessage;

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
	            if (message instanceof StreamDataMessage){
				    System.out.println("Received ping message!");
				    System.out.println("Sending pong message..");
				    //message.getClient().send(new PongMessage());
				}else if (message instanceof ServerMessage)
				{
					ServerMessage helloMessage = (ServerMessage) message;
					System.out.println(helloMessage.msg);

				}
				else
				{
				    System.out.println("Wrong format message!");
				}
	        }
	    }
//	server.addMessageListener(this, HelloMessage.class);
	public static void main(String[] args) throws IOException {
	      Serializer.registerClass(StreamDataMessage.class);
	        Serializer.registerClass(PingMessage.class);
	        Serializer.registerClass(PongMessage.class);
	      Application app = new Application();
		  app.start(JmeContext.Type.Headless);
		  Server myServer = new Server(4040, 5050);
		  myServer.start();
		  //myServer.getConnectors()
		  myServer.addMessageListener(new MessageResponder(), StreamDataMessage.class);
		  myServer.addMessageListener(new MessageResponder(), ClientMessage.class);
		  
		}
}
