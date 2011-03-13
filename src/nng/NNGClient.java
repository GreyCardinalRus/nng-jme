package nng;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
//import com.jme3.math.ColorRGBA;
import com.jme3.network.connection.Client;
//import com.jme3.network.connection.Server;
import com.jme3.network.events.MessageAdapter;
import com.jme3.network.message.Message;
import com.jme3.network.serializing.Serializer;
import com.jme3.network.serializing.Serializable;
//import com.jme3.network.serializing.Serializer;
//import gctest.TestMessagesServer.PingMessage;
//import gctest.TestMessagesServer.PongMessage;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

//import nng.NNGServer.ClientMessage;
//import nng.NNGServer.ServerMessage;

//import nng.NNGServer.MessageResponder;
public class NNGClient extends SimpleApplication implements
AnimEventListener, ActionListener {
    @Serializable
    public static class ServerMessage extends Message {
    	public String msg;
    }
    @Serializable
    public static class ClientMessage extends Message {
    	public String msg;
    }
    private static Client client;
	@Override
	public void simpleInitApp() {
		inputManager.addMapping("Auto", new KeyTrigger(KeyInput.KEY_RETURN));
		inputManager.addListener(this,"Auto");

		
	}
	private static class MessageResponder extends MessageAdapter {
		@Override
		public void messageReceived(Message message) {
		if (message instanceof ServerMessage) {
			System.out.println("Received message: "	+ ((ServerMessage) message).msg);
			// System.out.println(helloMessage.msg);
		} else {
			System.out.println("Wrong format message!");
		}
		}
	}
	public void onAction(String name, boolean keyPressed, float tpf) {
		// System.out.print(name);
		if (name.equals("Auto")) {
			
//			System.out.println("sending..." +
//					"");
//			StreamDataMessage m = new StreamDataMessage();
////			byte[] b = new byte[5];
////			b[0] = 1;
////			b[1] = 13;
////			m.setData(b);
//			try {
//				client.send(m);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			ClientMessage cm = new ClientMessage();
			cm.msg="ttt";
			try {
				client.send(cm);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		}
	public static void main(String[] args) throws IOException, InterruptedException {
	      //Serializer.registerClass(StreamDataMessage.class);
	        Serializer.registerClass(ServerMessage.class);
	        Serializer.registerClass(ClientMessage.class);
		// Client client = new Client("localhost", 4040, 5050);
		client = new Client();

		List<InetAddress> foundHosts = client.discoverHosts(5110, 5110);
		for (InetAddress host : foundHosts) {
			client.connect(host.getCanonicalHostName(), 5110, 5110);
		}
		client.start();
		client.addMessageListener(new MessageResponder(), ServerMessage.class);
	       Thread.sleep(100);

	       ClientMessage ms = new ClientMessage();
	       ms.msg="testClient" ;
		client.send(ms);
		System.out.println(client.getPlayerID() + " CID="
				+ client.getClientID());
	}
	@Override
	public void onAnimCycleDone(AnimControl control, AnimChannel channel,
			String animName) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onAnimChange(AnimControl control, AnimChannel channel,
			String animName) {
		// TODO Auto-generated method stub
		
	}
}
