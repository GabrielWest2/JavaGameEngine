package engine.physics;
/*
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.*;
import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import engine.GameEngine;
import engine.ecs.Entity;
import engine.input.Keyboard;
import engine.input.Mouse;
import engine.model.OBJLoader;
import engine.shader.StaticShader;
import engine.terrain.Terrain;
import engine.texture.TextureLoader;
import engine.util.QuatHelper;
import engine.util.Time;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glLoadIdentity;

public class Physics {

    private static final Transform DEFAULT_BALL_TRANSFORM = new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(0, 30, 0), 1.0f));
    private static DynamicsWorld dynamicsWorld;
    private static final Set<RigidBody> balls = new HashSet<RigidBody>();
    private static RigidBody controlBall;
    private static boolean applyForce = false;
    private static boolean createNewShape = false;
    private static boolean resetControlBall = false;
    private static boolean createNewCube = false;
    private static boolean createNewCapsule = false;


    public static void setUpPhysics() {
        BroadphaseInterface broadphase = new DbvtBroadphase();
        CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
        ConstraintSolver solver = new SequentialImpulseConstraintSolver();
        dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
        dynamicsWorld.setGravity(new Vector3f(0, -10 , 0));


        CollisionShape groundShape = Terrain.generateCollider();//new StaticPlaneShape(new Vector3f(0, 1, 0), 0.25f );
        CollisionShape ballShape = new SphereShape(3.0f);

        MotionState groundMotionState = new DefaultMotionState(new Transform(new Matrix4f(
                new Quat4f(0, 0, 0, 1),
                new Vector3f(0, 0, 0), 1.0f)));
        RigidBodyConstructionInfo groundBodyConstructionInfo = new RigidBodyConstructionInfo(0, groundMotionState, groundShape, new Vector3f(0, 0, 0));
        groundBodyConstructionInfo.restitution = 0.25f;
        RigidBody groundRigidBody = new RigidBody(groundBodyConstructionInfo);
        dynamicsWorld.addRigidBody(groundRigidBody);



        MotionState ballMotionState = new DefaultMotionState(DEFAULT_BALL_TRANSFORM);
        Vector3f ballInertia = new Vector3f(0, 0, 0);
        ballShape.calculateLocalInertia(2.5f, ballInertia);
        RigidBodyConstructionInfo ballConstructionInfo = new RigidBodyConstructionInfo(2.5f, ballMotionState, ballShape, ballInertia);
        ballConstructionInfo.restitution = 0.5f;
        ballConstructionInfo.angularDamping = 0.95f;
        controlBall = new RigidBody(ballConstructionInfo);
        controlBall.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
        balls.add(controlBall);
        dynamicsWorld.addRigidBody(controlBall);

        //Terrain

        CollisionShape shape = Terrain.generateCollider();
        DefaultMotionState motionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(0f, 0f, 0f), 1)));
        Vector3f inertia = new Vector3f();
        shape.calculateLocalInertia(1.0f, inertia);
        RigidBodyConstructionInfo constructionInfo = new RigidBodyConstructionInfo(1.0f, motionState, shape, inertia);
        constructionInfo.restitution = 0.25f;
        RigidBody body = new RigidBody(constructionInfo);
        dynamicsWorld.addRigidBody(body);
        balls.add(body);



        capsuleTransform = new engine.ecs.component.Transform(new org.joml.Vector3f(0,0,0), new org.joml.Vector3f(0,0,0), new org.joml.Vector3f(0.5f, 1f, 0.5f));
        cubeTransform = new engine.ecs.component.Transform(new org.joml.Vector3f(0,0,0), new org.joml.Vector3f(0,0,0), new org.joml.Vector3f(3f, 3f, 3f));
        sphereTransform = new engine.ecs.component.Transform(new org.joml.Vector3f(0,0,0), new org.joml.Vector3f(0,0,0), new org.joml.Vector3f(3f/2f, 3f/2f, 3f/2f));
        sphereEntity = new Entity(sphereTransform, OBJLoader.loadTexturedOBJ("sphere.obj", TextureLoader.getTexture("jungle_planks.png")));
        capsuleEntity = new Entity(capsuleTransform, OBJLoader.loadTexturedOBJ("sphere.obj", TextureLoader.getTexture("crystal.jpg")));
        cubeEntity = new Entity(cubeTransform, OBJLoader.loadTexturedOBJ("cube.obj", TextureLoader.getTexture("crystal.jpg")));

    }

    private static engine.ecs.component.Transform cubeTransform;
    private static engine.ecs.component.Transform sphereTransform;
    private static engine.ecs.component.Transform capsuleTransform;
    private static Entity sphereEntity;
    private static Entity capsuleEntity;
    private static Entity cubeEntity;


    public static void render(StaticShader shader){

        for (RigidBody body : balls) {
            Vector3f position = body.getWorldTransform(new Transform()).origin;
            Vector3f rotation = QuatHelper.convert(body.getWorldTransform(new Transform()).getRotation(new Quat4f()));
            if(body.getCollisionShape() instanceof SphereShape) {
                sphereEntity.getTransform().setPosition(new org.joml.Vector3f(position.x, position.y, position.z));
                GameEngine.renderer.Render(sphereEntity, shader);
            }else if(body.getCollisionShape() instanceof BoxShape){
                cubeEntity.getTransform().setPosition(new org.joml.Vector3f(position.x, position.y, position.z));
                GameEngine.renderer.Render(cubeEntity, shader);
            }else if(body.getCollisionShape() instanceof CapsuleShape){
                capsuleEntity.getTransform().setPosition(new org.joml.Vector3f(position.x, position.y, position.z));
                capsuleEntity.getTransform().setRotation(new org.joml.Vector3f(rotation.x, rotation.y, rotation.z));
                GameEngine.renderer.Render(capsuleEntity, shader);
            }
        }
    }

    public static void logic() {
        glLoadIdentity();
        dynamicsWorld.stepSimulation(Time.getDeltaTime());
        Set<RigidBody> ballsToBeRemoved = new HashSet<RigidBody>();
        for (RigidBody body : balls) {
            Vector3f position = body.getMotionState().getWorldTransform(new Transform()).origin;
            if (!body.equals(controlBall) && (position.y < -100f)) {
                ballsToBeRemoved.add(body);
            }
        }
        for (RigidBody body : ballsToBeRemoved) {
            balls.remove(body);
            dynamicsWorld.removeRigidBody(body);
        }
        if (applyForce) {
            Transform controlBallTransform = new Transform();
            controlBall.getMotionState().getWorldTransform(controlBallTransform);
            Vector3f controlBallLocation = controlBallTransform.origin;
            Vector3f cameraPosition = new Vector3f(GameEngine.camera.getPosition().x(), GameEngine.camera.getPosition().y(), GameEngine.camera.getPosition().z());
            Vector3f force = new Vector3f();
            force.sub(cameraPosition, controlBallLocation);
            controlBall.activate(true);
            controlBall.applyCentralForce(force);
        }
        if (createNewShape) {
            CollisionShape shape = new SphereShape(3.0f);
            DefaultMotionState motionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(GameEngine.camera.getPosition().x(), GameEngine.camera.getPosition().y(), GameEngine.camera.getPosition().z()), 1)));
            Vector3f inertia = new Vector3f();
            shape.calculateLocalInertia(1.0f, inertia);
            RigidBodyConstructionInfo constructionInfo = new RigidBodyConstructionInfo(1.0f, motionState, shape, inertia);
            constructionInfo.restitution = 0.25f;
            RigidBody body = new RigidBody(constructionInfo);
            dynamicsWorld.addRigidBody(body);
            balls.add(body);
            createNewShape = false;
        }
        if (createNewCube) {
            CollisionShape shape = new BoxShape(new Vector3f(3f, 3f, 3f));
            DefaultMotionState motionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(GameEngine.camera.getPosition().x(), GameEngine.camera.getPosition().y(), GameEngine.camera.getPosition().z()), 1)));
            Vector3f inertia = new Vector3f();
            shape.calculateLocalInertia(1.0f, inertia);
            RigidBodyConstructionInfo constructionInfo = new RigidBodyConstructionInfo(1.0f, motionState, shape, inertia);
            constructionInfo.restitution = 0.25f;
            RigidBody body = new RigidBody(constructionInfo);
            dynamicsWorld.addRigidBody(body);
            balls.add(body);
            createNewCube = false;
        }
        if (createNewCapsule) {
            CollisionShape shape = new CapsuleShape(0.5f, 2f);
            DefaultMotionState motionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(GameEngine.camera.getPosition().x(), GameEngine.camera.getPosition().y(), GameEngine.camera.getPosition().z()), 1)));
            Vector3f inertia = new Vector3f();
            shape.calculateLocalInertia(1.0f, inertia);
            RigidBodyConstructionInfo constructionInfo = new RigidBodyConstructionInfo(1.0f, motionState, shape, inertia);
            constructionInfo.restitution = 0.25f;
            RigidBody body = new RigidBody(constructionInfo);
            dynamicsWorld.addRigidBody(body);
            balls.add(body);
            createNewCube = false;
        }
        if (resetControlBall) {
            controlBall.setCenterOfMassTransform(DEFAULT_BALL_TRANSFORM);
            controlBall.setAngularVelocity(new Vector3f(0, 0, 0));
            controlBall.setLinearVelocity(new Vector3f(0, 0, 0));
            resetControlBall = false;
        }
    }

    public static void input() {
        applyForce = Mouse.isMousePressed(0);
        createNewShape = Keyboard.isKeyPressedThisFrame(GLFW_KEY_G);
        resetControlBall = Keyboard.isKeyPressedThisFrame(GLFW_KEY_F);
        createNewCube = Keyboard.isKeyPressedThisFrame(GLFW_KEY_C);
        createNewCapsule = Keyboard.isKeyPressedThisFrame(GLFW_KEY_X);
    }


   /* public static String getHitString() {
        final String[] s = new String[1];
        CollisionWorld.RayResultCallback callback = new CollisionWorld.RayResultCallback() {
            @Override
            public float addSingleResult(CollisionWorld.LocalRayResult localRayResult, boolean b) {
                s[0] = localRayResult.collisionObject.getCollisionShape().getName();
                return 0;
            }
        };
        Vector3f unitLookRay = new Vector3f( Math.cos(Math.toRadians(GameEngine.camera.getRotation().z)), Math.sin(Math.toRadians(GameEngine.camera.getRotation().z)), Math.sin(Math.toRadians(GameEngine.camera.getRotation().y)));
        Vector3f startRay = new Vector3f(GameEngine.camera.getPosition().x, GameEngine.camera.getPosition().y, GameEngine.camera.getPosition().z);
        Vector3f endRay = (Vector3f) startRay.clone();
        endRay.add(new Vector3f(0.0f, 0.0f, 0.0f));

        dynamicsWorld.rayTest(startRay, unitLookRay, callback);
        return s[0];
    }
}
*/
