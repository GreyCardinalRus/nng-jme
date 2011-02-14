package gctest;

import com.jme3.network.connection.Client;
import com.jme3.network.connection.Server;
import com.jme3.network.events.MessageAdapter;
import com.jme3.network.message.Message;
import com.jme3.network.serializing.Serializable;
import com.jme3.network.serializing.Serializer;
import java.io.IOException;

public class gcserver {

    @Serializable
    public static class ClientMessage extends Message {
    	public String i;
    	public ClientMessage(String _i){i=_i;}
       	public ClientMessage(){}
    	public String get_i(){return(i);}
    }
    @Serializable
    public static class ServerMessage extends Message {
    	public String i;
    	public ServerMessage(String _i){i=_i;}
       	public ServerMessage(){}
    	public String get_i(){return(i);}
    }
    private static class MessageResponder extends MessageAdapter {
        @Override
        public void messageReceived(Message message) {
            try {
                if (message instanceof ClientMessage){
                    System.out.println("Received ping message! "+((ClientMessage) message).i);
                    System.out.println("Sending pong message..");
                    message.getClient().send(new ServerMessage(((ClientMessage) message).i));
                }else if (message instanceof ServerMessage){
                    System.out.println("Received pong message!"+((ServerMessage) message).i);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        public static void main(String[] args) throws IOException, InterruptedException{
            Serializer.registerClass(ClientMessage.class);
            Serializer.registerClass(ServerMessage.class);

            Server server = new Server(5110, 5110);
            server.start();

            Client client = new Client("localhost", 5110, 5110);
            client.start();

            server.addMessageListener(new MessageResponder(), ClientMessage.class);
            client.addMessageListener(new MessageResponder(), ServerMessage.class);

            Thread.sleep(100);

//            System.out.println("Sending ping message..");
//            client.send(new PingMessage());
            client.send(new ClientMessage("33"));
        }
    }
}


