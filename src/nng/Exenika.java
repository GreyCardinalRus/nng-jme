package nng;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.math.ColorRGBA;
import com.jme3.network.connection.Client;
import com.jme3.network.connection.Server;
import com.jme3.network.events.MessageAdapter;
import com.jme3.network.message.Message;
import com.jme3.network.message.StreamDataMessage;
import com.jme3.network.serializing.Serializable;
import com.jme3.network.serializing.Serializer;
import com.jme3.system.JmeContext;
import com.jme3.system.JmeContext.Type;

public class Exenika extends SimpleApplication {
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

					SrvMessage.msg = "Server ansver ..."
							+ message.getClient().getClientID();
					System.out.println("Received message: "
							+ ((ClientMessage) message).msg);
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

	static boolean isServer = false, isClient = false;

	static void PrintUse() {
		System.out.println("Use ... -server ...-client");
	}

	Server server;
	Client client;
	static int port = 5110;

	public static void main(String[] args) {
		Serializer.registerClass(ServerMessage.class);
		Serializer.registerClass(ClientMessage.class);

		Exenika app = new Exenika();
		// boolean isServer = false,isClient;
		PrintUse();
		for (int i = 0; i < args.length; i++) {
			if (0 == args[i].compareTo("-server"))
				isServer = true;
			if (0 == args[i].compareTo("-client"))
				isClient = true;
		}
		if (!isServer && !isClient) {
			isServer = true;
			isClient = true;
			System.out.println("Standalone mode...");
			app.start();
		} else if (isServer && !isClient) {
			System.out.println("Server mode...");
			app.start(JmeContext.Type.Headless);
		} else if (isClient)
			app.start();
		else {
			PrintUse();

		}
		;
	}

	@Override
	public void simpleInitApp() {
		if (isServer) {
			try {
				server = new Server(5110, 5110);
				server.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// myServer.getConnectors()
			// myServer.addMessageListener(new MessageResponder(),
			// StreamDataMessage.class);
			server.addMessageListener(new MessageResponder(),
					ClientMessage.class);

		}
		if (isClient) {
			// Client client = new Client("localhost", 4040, 5050);
			client = new Client();
			if (isServer) {
				try {
					client.connect("localhost", port, port);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				List<InetAddress> foundHosts;
				try {
					foundHosts = client.discoverHosts(port, port);
					for (InetAddress host : foundHosts) {
						client.connect(host.getCanonicalHostName(), port, port);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			client.start();
		}
		Box b = new Box(Vector3f.ZERO, 1, 1, 1);
		Geometry geom = new Geometry("Box", b);
		Material mat = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		geom.setMaterial(mat);
		rootNode.attachChild(geom);
	}

}
