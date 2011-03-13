package nng;

import gctest.NinjaAnimation.ClientMessage;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
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

public class Exenika extends SimpleApplication implements
AnimEventListener, ActionListener {
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
			if (message instanceof ServerMessage) {
				System.out.println("Received server message!" + ((ServerMessage)message).msg);
//				createbox();
//				System.out.println("Sending pong message..");
//				Box b = new Box(Vector3f.ZERO, 1, 1, 1);
//				Geometry geom = new Geometry("Box", b);
//				Material mat = new Material(assetManager,
//						"Common/MatDefs/Misc/Unshaded.j3md");
//				mat.setColor("Color", ColorRGBA.Blue);
//				geom.setMaterial(mat);
//				rootNode.attachChild(geom);
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
			initKeys();
		}
//		Box b = new Box(Vector3f.ZERO, 1, 1, 1);
//		Geometry geom = new Geometry("Box", b);
//		Material mat = new Material(assetManager,
//				"Common/MatDefs/Misc/Unshaded.j3md");
//		mat.setColor("Color", ColorRGBA.Blue);
//		geom.setMaterial(mat);
//		rootNode.attachChild(geom);
	}
   void createbox()
   {
		Box b = new Box(Vector3f.ZERO, 1, 1, 1);
		Geometry geom = new Geometry("Box", b);
		Material mat = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		geom.setMaterial(mat);
		rootNode.attachChild(geom);
	   
   }
	/** Custom Keybinding: Map named actions to inputs. */
	private void initKeys() {
		inputManager.addMapping("Attack1", new KeyTrigger(KeyInput.KEY_0));
		inputManager.addMapping("Attack2", new KeyTrigger(KeyInput.KEY_1));
		inputManager.addMapping("Attack3", new KeyTrigger(KeyInput.KEY_2));
		inputManager.addMapping("Backflip", new KeyTrigger(KeyInput.KEY_3));
		inputManager.addMapping("Block", new KeyTrigger(KeyInput.KEY_4));
		inputManager.addMapping("Climb", new KeyTrigger(KeyInput.KEY_5));
		inputManager.addMapping("Crouch", new KeyTrigger(KeyInput.KEY_6));
		inputManager.addMapping("Death1", new KeyTrigger(KeyInput.KEY_7));
		inputManager.addMapping("Death2", new KeyTrigger(KeyInput.KEY_8));
		inputManager.addMapping("HighJump", new KeyTrigger(KeyInput.KEY_9));
		inputManager.addMapping("Idle1", new KeyTrigger(KeyInput.KEY_P));
		inputManager.addMapping("Idle2", new KeyTrigger(KeyInput.KEY_O));
		inputManager.addMapping("Idle3", new KeyTrigger(KeyInput.KEY_I));
		inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_J));
		inputManager.addMapping("JumpNoHeight", new KeyTrigger(KeyInput.KEY_K));
		inputManager.addMapping("Kick", new KeyTrigger(KeyInput.KEY_L));
		inputManager.addMapping("SideKick", new KeyTrigger(KeyInput.KEY_M));
		inputManager.addMapping("Spin", new KeyTrigger(KeyInput.KEY_N));
		inputManager.addMapping("Stealth", new KeyTrigger(KeyInput.KEY_B));
		inputManager.addMapping("Walk", new KeyTrigger(KeyInput.KEY_V));
		inputManager.addMapping("Random", new KeyTrigger(KeyInput.KEY_SPACE));
		inputManager.addMapping("Auto", new KeyTrigger(KeyInput.KEY_RETURN));
		inputManager.addListener(this, "Attack1", "Attack2", "Attack3", "Backflip",
						"Block", "Climb", "Crouch", "Death1", "Death2",
						"HighJump", "Idle1", "Idle2", "Idle3", "Jump",
						"JumpNoHeight", "Kick", "SideKick", "Spin", "Stealth",
						"Walk", "Random", "Auto");
	}
	public void onAction(String name, boolean keyPressed, float tpf) {
		// System.out.print(name);
		if (name.equals("Random") && keyPressed) {
			ClientMessage cm = new ClientMessage();
			cm.msg = name;
			System.out.println(client.getPlayerID() + " CID="
					+ client.getClientID());
			try {

				client.send(cm);
				System.out.println("message send..." + name);
				// client.send(new PingMessage());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			int p = 0, nm = 0;
//			for (nm = 0; nm < numChanels; nm++) {
//				p = (int) (Math.random() * animNumbers);
//				channels[nm].setAnim(animNames[p], 0.50f);
//				channels[nm].setLoopMode(LoopMode.Loop);
//				// p = (int) (Math.random() * animNumbers);
//				// channel2.setAnim(animNames[p], 0.50f);
//				// channel2.setLoopMode(LoopMode.Loop);
//			}
//		} else if (name.equals("Auto") && keyPressed) {
//			System.out.print(isRunAuto);
//			isRunAuto = !isRunAuto;
//			System.out.println(" ->" + isRunAuto);
//			if (isRunAuto) {
//				int p = 0, nm = 0;
//				for (nm = 0; nm < numChanels; nm++) {
//					p = (int) (Math.random() * animNumbers);
//					channels[nm].setAnim(animNames[p], 0.50f);
//					channels[nm].setLoopMode(LoopMode.Loop);
//					// p = (int) (Math.random() * animNumbers);
//					// channel2.setAnim(animNames[p], 0.50f);
//					// channel2.setLoopMode(LoopMode.Loop);
//				}
//
//			}
//		} else if ( keyPressed) {
//			// if (!channel1.getAnimationName().equals("Walk")) {
//			// channel1.setAnim("Walk", 0.50f);
//			channels[0].setAnim(name, 0.50f);
//			channels[0].setLoopMode(LoopMode.Loop);
//			// }
		}
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
