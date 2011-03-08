/*
 * Copyright (c) 2009-2010 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package gctest;

import java.util.ArrayList;
import java.util.List;

import jme3tools.converters.ImageToAwt;

import com.jme3.bullet.BulletAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.nodes.PhysicsNode;
import com.jme3.bullet.objects.VehicleWheel;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
//import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.shadow.BasicShadowRenderer;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.util.SkyFactory;

public class TestFancyCar extends SimpleApplication implements ActionListener {

    private BulletAppState bulletAppState;
    private VehicleControl player;
    private int num_players;
    private VehicleControl players[];
//    private VehicleWheel fr, fl, br, bl;
//    private Node node_fr, node_fl, node_br, node_bl;
    private float wheelRadius;
    private float steeringValue = 0;
    private float accelerationValue = 0;
    private Node carNode;
    //Materials
    Material matRock;
    Material matWire;
    TerrainQuad terrain;
    RigidBodyControl terrainPhysicsNode;
    
    private Vector3f jumpForce = new Vector3f(0, 3000, 0);
    public static void main(String[] args) {
        TestFancyCar app = new TestFancyCar();
        app.start();
    }

    private void setupKeys() {
        inputManager.addMapping("Lefts1", new KeyTrigger(KeyInput.KEY_H));
        inputManager.addMapping("Rights1", new KeyTrigger(KeyInput.KEY_K));
        inputManager.addMapping("Ups1", new KeyTrigger(KeyInput.KEY_U));
        inputManager.addMapping("Downs1", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("Space", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Reset", new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addListener(this, "Lefts1");
        inputManager.addListener(this, "Rights1");
        inputManager.addListener(this, "Ups1");
        inputManager.addListener(this, "Downs1");
        inputManager.addListener(this, "Space");
        inputManager.addListener(this, "Reset");
        inputManager.addMapping("Lefts2", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("Rights2", new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("Ups2", new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("Downs2", new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addListener(this, "Lefts2");
        inputManager.addListener(this, "Rights2");
        inputManager.addListener(this, "Ups2");
        inputManager.addListener(this, "Downs2");

    }

    @Override
    public void simpleInitApp() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        if (settings.getRenderer().startsWith("LWJGL")) {
            BasicShadowRenderer bsr = new BasicShadowRenderer(assetManager, 512);
            bsr.setDirection(new Vector3f(-0.5f, -0.3f, -0.3f).normalizeLocal());
            viewPort.addProcessor(bsr);
        }
        cam.setFrustumFar(150f);
        flyCam.setMoveSpeed(10);

        setupKeys();
        createTerrain();
        createSky();
//        setupFloor();
        num_players=2;
        players = new VehicleControl[50];
        for(int i=0;i<num_players;i++)   	
        	{
        	players[i]=buildPlayer(i);
        	//wait(100);
        	}
        //playes[1]=buildPlayer(1);

        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.5f, -1f, -0.3f).normalizeLocal());
        rootNode.addLight(dl);

        dl = new DirectionalLight();
        dl.setDirection(new Vector3f(0.5f, -0.1f, 0.3f).normalizeLocal());
        rootNode.addLight(dl);
    }

    private void createTerrain() {
        matRock = new Material(assetManager, "Common/MatDefs/Terrain/Terrain.j3md");
        matRock.setTexture("Alpha", assetManager.loadTexture("Textures/Terrain/splat/alphamap.png"));
        Texture heightMapImage = assetManager.loadTexture("Textures/Terrain/splat/mountains512.png");
        Texture grass = assetManager.loadTexture("Textures/Terrain/splat/grass.jpg");
        grass.setWrap(WrapMode.Repeat);
        matRock.setTexture("Tex1", grass);
        matRock.setFloat("Tex1Scale", 64f);
        Texture dirt = assetManager.loadTexture("Textures/Terrain/splat/dirt.jpg");
        dirt.setWrap(WrapMode.Repeat);
        matRock.setTexture("Tex2", dirt);
        matRock.setFloat("Tex2Scale", 32f);
        Texture rock = assetManager.loadTexture("Textures/Terrain/splat/road.jpg");
        rock.setWrap(WrapMode.Repeat);
        matRock.setTexture("Tex3", rock);
        matRock.setFloat("Tex3Scale", 128f);
        matWire = new Material(assetManager, "Common/MatDefs/Misc/WireColor.j3md");
        matWire.setColor("Color", ColorRGBA.Green);

        AbstractHeightMap heightmap = null;
        try {
            heightmap = new ImageBasedHeightMap(ImageToAwt.convert(heightMapImage.getImage(), false, true, 0), 0.25f);
            heightmap.load();

        } catch (Exception e) {
            e.printStackTrace();
        }

        terrain = new TerrainQuad("terrain", 65, 513, heightmap.getHeightMap());
        List<Camera> cameras = new ArrayList<Camera>();
        cameras.add(getCamera());
        TerrainLodControl control = new TerrainLodControl(terrain, cameras);
        terrain.addControl(control);
        terrain.setMaterial(matRock);
        terrain.setModelBound(new BoundingBox());
        terrain.updateModelBound();
        terrain.setLocalScale(new Vector3f(2, 2, 2));

        terrainPhysicsNode = new RigidBodyControl(CollisionShapeFactory.createMeshShape(terrain), 0);
        terrain.addControl(terrainPhysicsNode);
        rootNode.attachChild(terrain);
        getPhysicsSpace().add(terrainPhysicsNode);
    }

    private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }

  
    private Geometry findGeom(Spatial spatial, String name) {
        if (spatial instanceof Node) {
            Node node = (Node) spatial;
            for (int i = 0; i < node.getQuantity(); i++) {
                Spatial child = node.getChild(i);
                Geometry result = findGeom(child, name);
                if (result != null) {
                    return result;
                }
            }
        } else if (spatial instanceof Geometry) {
            if (spatial.getName().startsWith(name)) {
                return (Geometry) spatial;
            }
        }
        return null;
    }
    private void createSky() {
        rootNode.attachChild(SkyFactory.createSky(assetManager, "Textures/Sky/Bright/BrightSky.dds", false));
    }

    private VehicleControl buildPlayer(int num_player) 
    {
        float stiffness = 120.0f;//200=f1 car
        float compValue = 0.2f; //(lower than damp!)
        float dampValue = 0.3f;
        final float mass = 400;

        //Load model and get chassis Geometry
        carNode = (Node)assetManager.loadModel("Models/Ferrari/Car.scene");
        carNode.setShadowMode(ShadowMode.Cast);
        Geometry chasis = findGeom(carNode, "Car");
        BoundingBox box = (BoundingBox) chasis.getModelBound();

        //Create a hull collision shape for the chassis
        CollisionShape carHull = CollisionShapeFactory.createDynamicMeshShape(chasis);

        //Create a vehicle control
        player = new VehicleControl(carHull, mass);
        carNode.addControl(player);

        //Setting default values for wheels
        player.setSuspensionCompression(compValue * 2.0f * FastMath.sqrt(stiffness));
        player.setSuspensionDamping(dampValue * 2.0f * FastMath.sqrt(stiffness));
        player.setSuspensionStiffness(stiffness);
        player.setMaxSuspensionForce(10000);

        //Create four wheels and add them at their locations
        //note that our fancy car actually goes backwards..
        Vector3f wheelDirection = new Vector3f(0, -1, 0);
        Vector3f wheelAxle = new Vector3f(-1, 0, 0);

        Geometry wheel_fr = findGeom(carNode, "WheelFrontRight");
        wheel_fr.center();
        //wheel_fr.scale(3);
        box = (BoundingBox) wheel_fr.getModelBound();
        wheelRadius = box.getYExtent();
        float back_wheel_h = (wheelRadius * 1.7f) - 1f;
        float front_wheel_h = (wheelRadius * 1.9f) - 1f;
        player.addWheel(wheel_fr.getParent(), box.getCenter().add(0, -front_wheel_h, 0),
                wheelDirection, wheelAxle, 0.2f, wheelRadius, true);

        Geometry wheel_fl = findGeom(carNode, "WheelFrontLeft");
        wheel_fl.center();
        box = (BoundingBox) wheel_fl.getModelBound();
        player.addWheel(wheel_fl.getParent(), box.getCenter().add(0, -front_wheel_h, 0),
                wheelDirection, wheelAxle, 0.2f, wheelRadius, true);

        Geometry wheel_br = findGeom(carNode, "WheelBackRight");
        wheel_br.center();
        box = (BoundingBox) wheel_br.getModelBound();
        player.addWheel(wheel_br.getParent(), box.getCenter().add(0, -back_wheel_h, 0),
                wheelDirection, wheelAxle, 0.2f, wheelRadius, false);

        Geometry wheel_bl = findGeom(carNode, "WheelBackLeft");
        wheel_bl.center();
        box = (BoundingBox) wheel_bl.getModelBound();
        player.addWheel(wheel_bl.getParent(), box.getCenter().add(0, -back_wheel_h, 0),
                wheelDirection, wheelAxle, 0.2f, wheelRadius, false);

//        player.attachDebugShape(assetManager);
        player.getWheel(2).setFrictionSlip(4);
        player.getWheel(3).setFrictionSlip(4);

        rootNode.attachChild(carNode);
        getPhysicsSpace().add(player);
        return(player);
    }

    public void onAction(String binding, boolean value, float tpf) {
        int i;
    	for( i=0;i<num_players;i++)
        {
    	if (binding.equals("Lefts"+i)) {
            if (value) {
                steeringValue += .5f;
            } else {
                steeringValue += -.5f;
            }
            players[i].steer(steeringValue);
        } else if (binding.equals("Rights"+i)) {
            if (value) {
                steeringValue += -.5f;
            } else {
                steeringValue += .5f;
            }
            players[i].steer(steeringValue);
        } //note that our fancy car actually goes backwards..
        else if (binding.equals("Ups"+i)) {
            if (value) {
                accelerationValue -= 800;
            } else {
                accelerationValue += 800;
            }
            players[i].accelerate(accelerationValue);
        } else if (binding.equals("Downs"+i)) {
            if (value) {
                players[i].brake(40f);
            } else {
                players[i].brake(0f);
            }
        } else if (binding.equals("Space")) {
            if (value) {
            	players[i].applyImpulse(jumpForce, Vector3f.ZERO);
            }
            } else if (binding.equals("Reset")) {
            if (value) {
                System.out.println("Reset");
                players[i].setPhysicsLocation(new Vector3f(-140, 10, -10));
                players[i].setPhysicsRotation(new Matrix3f());
                players[i].setLinearVelocity(Vector3f.ZERO);
                players[i].setAngularVelocity(Vector3f.ZERO);
                players[i].resetSuspension();
            } else {
            }
        }
    }
    }

    @Override
    public void simpleUpdate(float tpf) {
        //	cam.lookAt(carNode.getWorldTranslation(), Vector3f.UNIT_Y);
    	Vector3f playerR = carNode.getWorldRotation().getRotationColumn(0);
    	cam.setLocation(carNode.getWorldTranslation().add(0, 2, 0));
    	//cam.lookAt(player.getPhysicsLocation().add(0, 0, 0), new Vector3f(playerR.x,playerR.y,playerR.z));
    	//
    	//Quaternion i= carNode.getWorldRotation().getRotationColumn(0) 
//    	cam.setLocation(carNode.getWorldTranslation().add(0, 5, 0));
    }
    public void updateCamera() {
        rootNode.updateGeometricState();

        Vector3f pos = carNode.getWorldTranslation().clone();
        Quaternion rot = carNode.getWorldRotation();
        Vector3f dir = rot.getRotationColumn(2);

        // make it XZ only
        Vector3f camPos = new Vector3f(dir);
        camPos.setY(0);
        camPos.normalizeLocal();

        // negate and multiply by distance from object
        camPos.negateLocal();
        camPos.multLocal(15);

        // add Y distance
        camPos.setY(2);
        camPos.addLocal(pos);
        cam.setLocation(camPos);

        Vector3f lookAt = new Vector3f(dir);
        lookAt.multLocal(7); // look at dist
        lookAt.addLocal(pos);
        cam.lookAt(lookAt, Vector3f.UNIT_Y);
    }
}
