package engine.ecs.component;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CapsuleShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.QuaternionUtil;
import com.bulletphysics.linearmath.Transform;
import engine.GameEngine;
import engine.ecs.Component;
import engine.ecs.Entity;
import engine.model.Model;
import engine.model.ModelCreator;
import engine.model.OBJLoader;
import engine.physics.Physics;
import org.joml.QuaternionfInterpolator;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class Rigidbody3D extends Component {
    public float mass = 2.5f;
    public float restitution = 0.5f;
    public float angularDamping = 0.95f;

    public transient RigidBody rb;

    private static Model cube;
    private static Model sphere;

    static {
        cube = OBJLoader.loadOBJWithoutMTL("engine/primitive/cube.obj");
        sphere = OBJLoader.loadOBJWithoutMTL("engine/primitive/sphere.obj");
    }

    public Rigidbody3D(){
        org.joml.Vector3f pos = new org.joml.Vector3f();
        org.joml.Vector3f rot = new org.joml.Vector3f();
        Quat4f quatRot = new Quat4f();
        QuaternionUtil.setEuler(quatRot, rot.x, rot.y, rot.z);
        MotionState state = new DefaultMotionState(new Transform(new Matrix4f(quatRot, new Vector3f(pos.x, pos.y, pos.z), 1.0f)));
        CollisionShape shape = new BoxShape(new javax.vecmath.Vector3f(1, 1, 1));
        shape.calculateLocalInertia(mass, new javax.vecmath.Vector3f(0, 0, 0));

        RigidBodyConstructionInfo rigidBodyConstructionInfo = new RigidBodyConstructionInfo(mass, state, shape, new Vector3f(0, 0, 0));
        rigidBodyConstructionInfo.restitution = restitution;
        rigidBodyConstructionInfo.angularDamping = angularDamping;
        rb = new RigidBody(rigidBodyConstructionInfo);

        state = new DefaultMotionState(new Transform(new Matrix4f(quatRot, new Vector3f(0, -2, 0), 1.0f)));
        shape = new BoxShape(new javax.vecmath.Vector3f(1, 1, 1));
        shape.calculateLocalInertia(mass, new javax.vecmath.Vector3f(0, 0, 0));
        rigidBodyConstructionInfo = new RigidBodyConstructionInfo(mass, state, shape, new Vector3f(0, 0, 0));
        rigidBodyConstructionInfo.restitution = restitution;
        rigidBodyConstructionInfo.angularDamping = angularDamping;




    }

    public void render(){
        Vector3f position = rb.getWorldTransform(new Transform()).origin;
       // Vector3f rotation = .convert(rb.getWorldTransform(new Transform()).getRotation(new Quat4f()));
        //GameEngine.getInstance().renderer.renderDebug(cube, new org.joml.Vector3f(position.x, position.y, position.z), new org.joml.Vector3f(), new org.joml.Vector3f(1, 1, 1));
        /*if(rb.getCollisionShape() instanceof SphereShape) {
            GameEngine.getInstance().renderer.render(sphereEntity);
        }else if(rb.getCollisionShape() instanceof BoxShape){
            GameEngine.getInstance().render(cubeEntity);
        }else if(rb.getCollisionShape() instanceof CapsuleShape){

            GameEngine.getInstance().render(capsuleEntity);
        }*/
    }

    @Override
    public void onAdded(){
        Physics.addBody(this);
    }

    @Override
    public void onRemoved(){
        Physics.removeBody(this);
    }

    @Override
    public boolean canBeRemoved() {
        return true;
    }
}
