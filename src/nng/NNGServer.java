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
import java.io.IOException;

//@Serializable
//public static class PingMessage extends Message {
//}
//
//
//@Serializable
//public static class PongMessage extends Message {
//}
public class NNGServer {
	@Serializable()
	
public class HelloMessage extends Message {
   public String hello = "Hello!";
}
	   private static class MessageResponder extends MessageAdapter {
	        @Override
	        public void messageReceived(Message message) {
	            if (message instanceof StreamDataMessage){
				    System.out.println("Received ping message!");
				    System.out.println("Sending pong message..");
				    //message.getClient().send(new PongMessage());
				}else //if (message instanceof PongMessage)
				{
				    System.out.println("Wrong format message!");
				}
	        }
	    }
//	server.addMessageListener(this, HelloMessage.class);
	public static void main(String[] args) throws IOException {
	      Serializer.registerClass(StreamDataMessage.class);
	      Serializer.registerClass(HelloMessage.class);
	      Application app = new Application();
		  app.start(JmeContext.Type.Headless);
		  Server myServer = new Server(4040, 5050);
		  myServer.start();
		  
		  myServer.addMessageListener(new MessageResponder(), StreamDataMessage.class);
		  myServer.addMessageListener(new MessageResponder(), HelloMessage.class);
		}
}
