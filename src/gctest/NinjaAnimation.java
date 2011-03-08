package gctest;

import gctest.TestMessagesClient.PingMessage;
//import gctest.TestMessagesClient.PingResponder;
import gctest.TestMessagesClient.PongMessage;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import nng.NNGClient.ClientMessage;
import nng.NNGClient.ServerMessage;

//import nng.NNGClient.ClientMessage;
//import nng.NNGClient.MessageResponder;
//import nng.NNGClient.ServerMessage;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
//import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.network.connection.Client;
import com.jme3.network.events.MessageAdapter;
import com.jme3.network.message.Message;
import com.jme3.network.serializing.Serializable;
import com.jme3.network.serializing.Serializer;
import com.jme3.scene.Node;

//import com.jme3.scene.debug.SkeletonDebugger;

/**
 * Sample 7 - how to load an OgreXML model and play an animation, using
 * channels, a controller, and an AnimEventListener.
 */
public class NinjaAnimation extends SimpleApplication implements
		AnimEventListener, ActionListener {

	private AnimChannel[] channels;
	private int numChanels;
	private String[] animNames;
	private int animNumbers;
	private boolean isRunAuto;
	private AnimChannel channel;
	private AnimControl control;
	Node player;

	@Serializable
	public static class ServerMessage extends Message {
		public String msg;
	}

	@Serializable
	public static class ClientMessage extends Message {
		public String msg;
	}

	private static class PingResponder extends MessageAdapter {
		@Override
		public void messageReceived(Message message) {
			try {
				if (message instanceof PingMessage) {
					System.out.println("Received ping message!");
					System.out.println("Sending pong message..");
					message.getClient().send(new PongMessage());
				} else if (message instanceof PongMessage) {
					System.out.println("Received pong message!");
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private static Client client;

	public static void main(String[] args) {
        Serializer.registerClass(ServerMessage.class);
        Serializer.registerClass(ClientMessage.class);
		NinjaAnimation app = new NinjaAnimation();
		app.start();
	}

	private static class MessageResponder extends MessageAdapter {
		@Override
		public void messageReceived(Message message) {
			if (message instanceof ServerMessage) {
				System.out.println("Received message: "
						+ ((ServerMessage) message).msg);
				// System.out.println(helloMessage.msg);
			} else {
				System.out.println("Wrong format message!");
			}
		}
	}

	@Override
	public void simpleInitApp() {
		isRunAuto = false;
		viewPort.setBackgroundColor(ColorRGBA.LightGray);
		initKeys();
		flyCam.setMoveSpeed(1000f);
		cam.setLocation(new Vector3f(0f, 150f, -425f));
		cam.lookAt(new Vector3f(0f, 100f, 0f), Vector3f.UNIT_Y);
		numChanels = 20;
		channels = new AnimChannel[numChanels];
		DirectionalLight dl = new DirectionalLight();
		dl.setDirection(new Vector3f(-0.1f, -1f, 1).normalizeLocal());
		rootNode.addLight(dl);
		// add ninja...
		Node model1;
		for (int nm = 0; nm < numChanels; nm++) {
			model1 = (Node) assetManager
					.loadModel("Models/Ninja/Ninja.mesh.xml");
			// Node model2 = (Node) assetManager
			// .loadModel("Models/Ninja/Ninja.mesh.xml");
			// Node model2 = model1.clone();

			model1.setLocalTranslation((nm % 4) * 100, 0, nm * 100);
			// model2.setLocalTranslation(100, 0, 0);

			// rotate
			model1.rotate(0, (nm % 2 == 0) ? -1.7f : 1.7f, 0);
			// model2.rotate(0, 1.7f, 0);
			// \
			AnimControl control1 = model1.getControl(AnimControl.class);
			// System.out.print(control1.getAnimationNames().size());
			// animNames = (String[]) control1.getAnimationNames().toArray();
			animNumbers = 0;
			animNames = new String[control1.getAnimationNames().size() + 1];
			for (String anim : control1.getAnimationNames()) {
				animNames[animNumbers++] = anim;
			}

			// animNames = control1.getAnimationNames().toArray(new String[0]);
			channels[nm] = control1.createChannel();

			// AnimControl control2 = model2.getControl(AnimControl.class);
			// channel2 = control2.createChannel();

			// SkeletonDebugger skeletonDebug = new
			// SkeletonDebugger("skeleton1",
			// control1.getSkeleton());
			// Material mat = new Material(assetManager,
			// "Common/MatDefs/Misc/Unshaded.j3md");
			// mat.getAdditionalRenderState().setWireframe(true);
			// mat.setColor("Color", ColorRGBA.Green);
			// mat.getAdditionalRenderState().setDepthTest(false);
			// skeletonDebug.setMaterial(mat);
			// model1.attachChild(skeletonDebug);
			//
			// skeletonDebug = new SkeletonDebugger("skeleton2",
			// control2.getSkeleton());
			// skeletonDebug.setMaterial(mat);
			// model2.attachChild(skeletonDebug);

			rootNode.attachChild(model1);
			// rootNode.attachChild(model2);
		}
		// player = (Node) assetManager.loadModel("Models/Oto/Oto.mesh.xml");
		// player.setLocalScale(0.5f);
		// rootNode.attachChild(player);
		//
		// control = player.getControl(AnimControl.class);
		// control.addListener(this);
		// channel = control.createChannel();
		// channel.setAnim("stand");
		client = new Client();

		client = new Client();

		List<InetAddress> foundHosts;
		try {
			foundHosts = client.discoverHosts(5110, 5110);
			for (InetAddress host : foundHosts) {
				client.connect(host.getCanonicalHostName(), 5110, 5110);
				Thread.sleep(100);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// client.start();
		// client.addMessageListener(new MessageResponder(),
		// ServerMessage.class);
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// try {
		// client = new Client("localhost", 5110, 5110);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		client.start();
		client.addMessageListener(new MessageResponder(), ServerMessage.class);
		// client.addMessageListener(new PingResponder(), PongMessage.class);
		// client.send(new ServerMessage());
		System.out.println(client.getPlayerID() + " CID="
				+ client.getClientID());
		ClientMessage ms = new ClientMessage();
		ms.msg = "testClient";
		try {
			client.send(ms);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(client.getPlayerID() + " CID="
				+ client.getClientID());

	}

	public void onAnimCycleDone(AnimControl control, AnimChannel channel,
			String animName) {
		// if (animName.equals("Walk")) {
		// channel.setAnim("stand", 0.50f);
		// channel.setLoopMode(LoopMode.DontLoop);
		// channel.setSpeed(1f);
		// }
		System.out.println(animName + " done...");
		if (isRunAuto) {
			int p = 0;
			// p = (int) (Math.random() * animNumbers);
			// channel1.setAnim(animNames[p], 0.50f);
			// channel1.setLoopMode(LoopMode.DontLoop);
			// p = (int) (Math.random() * animNumbers);
			// channel2.setAnim(animNames[p], 0.50f);
			// channel2.setLoopMode(LoopMode.DontLoop);

		}

	}

	public void onAnimChange(AnimControl control, AnimChannel channel,
			String animName) {
		// unused
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
		inputManager
				.addListener(this, "Attack1", "Attack2", "Attack3", "Backflip",
						"Block", "Climb", "Crouch", "Death1", "Death2",
						"HighJump", "Idle1", "Idle2", "Idle3", "Jump",
						"JumpNoHeight", "Kick", "SideKick", "Spin", "Stealth",
						"Walk", "Random", "Auto");
	}

	// private ActionListener actionListener = new ActionListener() {

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
			int p = 0, nm = 0;
			for (nm = 0; nm < numChanels; nm++) {
				p = (int) (Math.random() * animNumbers);
				channels[nm].setAnim(animNames[p], 0.50f);
				channels[nm].setLoopMode(LoopMode.Loop);
				// p = (int) (Math.random() * animNumbers);
				// channel2.setAnim(animNames[p], 0.50f);
				// channel2.setLoopMode(LoopMode.Loop);
			}
		} else if (name.equals("Auto") && keyPressed) {
			System.out.print(isRunAuto);
			isRunAuto = !isRunAuto;
			System.out.println(" ->" + isRunAuto);
			if (isRunAuto) {
				int p = 0, nm = 0;
				for (nm = 0; nm < numChanels; nm++) {
					p = (int) (Math.random() * animNumbers);
					channels[nm].setAnim(animNames[p], 0.50f);
					channels[nm].setLoopMode(LoopMode.Loop);
					// p = (int) (Math.random() * animNumbers);
					// channel2.setAnim(animNames[p], 0.50f);
					// channel2.setLoopMode(LoopMode.Loop);
				}

			}
		} else if ( keyPressed) {
			// if (!channel1.getAnimationName().equals("Walk")) {
			// channel1.setAnim("Walk", 0.50f);
			channels[0].setAnim(name, 0.50f);
			channels[0].setLoopMode(LoopMode.Loop);
			// }
		}
	}
	// };

}
