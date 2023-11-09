package engine.physics;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import engine.ecs.component.Rigidbody3D;
import engine.util.Time;
import org.joml.Quaternionf;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.util.HashMap;

public class Physics {

    private static DynamicsWorld dynamicsWorld;
    private static HashMap<RigidBody, Rigidbody3D> bodies = new HashMap<>();

    public static void init() {
        BroadphaseInterface broadphase = new DbvtBroadphase();
        CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
        ConstraintSolver solver = new SequentialImpulseConstraintSolver();
        dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
        dynamicsWorld.setGravity(new Vector3f(0, -10, 0));


        Transform groundTransform = new Transform();
        groundTransform.setIdentity();
        CollisionShape groundShape;

        // x / z plane at y = -1.
        groundShape = new BoxShape(new Vector3f(110, 0.1f, 110));
        DefaultMotionState myMotionState = new DefaultMotionState(groundTransform);
        RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(0, myMotionState, groundShape, new Vector3f(0, 0, 0));
        RigidBody body = new RigidBody(rbInfo);
        body.setRestitution(0.9f);
        dynamicsWorld.addRigidBody(body);
    }

    public static void addBody(Rigidbody3D rigidbody3D) {
        bodies.put(rigidbody3D.rb, rigidbody3D);
        dynamicsWorld.addRigidBody(rigidbody3D.rb);
    }

    public static void removeBody(Rigidbody3D rigidbody3D){
        dynamicsWorld.removeRigidBody(rigidbody3D.rb);
        bodies.remove(rigidbody3D.rb);
    }

    public static void render() {
        for (RigidBody rb : bodies.keySet()){
            CollisionShape sh = rb.getCollisionShape();
            //if(sh != null){
            //    System.out.println(sh.getName());
            //}
            //TODO Render wireframe collider gizmo
        }
    }

    public static void logic() {
        dynamicsWorld.stepSimulation(Time.getDeltaTime());
        for (RigidBody rb : bodies.keySet()){
            Rigidbody3D comp = bodies.get(rb);
            Vector3f origin = rb.getMotionState().getWorldTransform(new Transform()).origin;
            Quat4f rotation = rb.getMotionState().getWorldTransform(new Transform()).getRotation(new Quat4f());
            //rotation.
            comp.entity.getTransform().setPosition(new org.joml.Vector3f(origin.x, origin.y, origin.z));
            comp.entity.getTransform().setRotation(new org.joml.Quaternionf(rotation.x, rotation.y, rotation.z, rotation.w));
        }
    }

    public static DynamicsWorld getDynamicsWorld() {
        return dynamicsWorld;
    }
}