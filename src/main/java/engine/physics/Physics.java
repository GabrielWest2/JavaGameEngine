package engine.physics;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.Transform;
import engine.ecs.component.Rigidbody3D;
import engine.util.Time;

import javax.vecmath.Vector3f;
import java.util.HashMap;

public class Physics {

    private static DynamicsWorld dynamicsWorld;
    private static HashMap<RigidBody, Rigidbody3D> bodies = new HashMap<>();

    public static void setUpPhysics() {
        BroadphaseInterface broadphase = new DbvtBroadphase();
        CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
        ConstraintSolver solver = new SequentialImpulseConstraintSolver();
        dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
        dynamicsWorld.setGravity(new Vector3f(0, -10, 0));
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
            bodies.get(rb).render();
        }
    }

    public static void logic() {
        dynamicsWorld.stepSimulation(Time.getDeltaTime());
        for (RigidBody rb : bodies.keySet()){
            Rigidbody3D comp = bodies.get(rb);
            Vector3f origin = rb.getMotionState().getWorldTransform(new Transform()).origin;
            System.out.println(origin);
            comp.entity.getTransform().setPosition(new org.joml.Vector3f(origin.x, origin.y, origin.z));
        }
    }

    public static DynamicsWorld getDynamicsWorld() {
        return dynamicsWorld;
    }
}