package engine.ecs.component;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.QuaternionUtil;
import com.bulletphysics.linearmath.Transform;
import editor.util.Range;
import engine.ecs.Component;
import engine.ecs.Entity;
import engine.physics.Physics;
import imgui.ImGui;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class Rigidbody3D extends Component {

    public float mass = 2.5f;

    public float restitution = 0.5f;

    @Range(min = 0, max = 1)
    public float angularDamping = 0.95f;

    public transient RigidBody rb;

    public Rigidbody3D(){
        org.joml.Vector3f pos = new org.joml.Vector3f();
        org.joml.Vector3f rot = new org.joml.Vector3f();
        Quat4f quatRot = new Quat4f();
        QuaternionUtil.setEuler(quatRot, rot.x, rot.y, rot.z);
        MotionState state = new DefaultMotionState(new Transform(new Matrix4f(quatRot, new Vector3f(pos.x, pos.y, pos.z), 1.0f)));
        CollisionShape shape = new BoxShape(new javax.vecmath.Vector3f(1, 1, 1));
        shape.calculateLocalInertia(mass, new javax.vecmath.Vector3f(0, 0, 0));

        RigidBodyConstructionInfo rigidBodyConstructionInfo = new RigidBodyConstructionInfo(mass, state, shape, new Vector3f(1, 1, 1));
        rigidBodyConstructionInfo.restitution = restitution;
        rigidBodyConstructionInfo.angularDamping = angularDamping;

        rb = new RigidBody(rigidBodyConstructionInfo);

        /*
        state = new DefaultMotionState(new Transform(new Matrix4f(quatRot, new Vector3f(0, -2, 0), 1.0f)));
        shape = new BoxShape(new javax.vecmath.Vector3f(1, 1, 1));
        shape.calculateLocalInertia(mass, new javax.vecmath.Vector3f(0, 0, 0));
        rigidBodyConstructionInfo = new RigidBodyConstructionInfo(mass, state, shape, new Vector3f(0, 0, 0));
        rigidBodyConstructionInfo.restitution = restitution;
        rigidBodyConstructionInfo.angularDamping = angularDamping;*/
    }

    @Override
    public void GUI() {
        super.GUI();
        if(ImGui.button("test")){
            rb.setLinearVelocity(new Vector3f(5, 5, 5));
            rb.setAngularVelocity(new Vector3f(0, 10, 2));
        }

        ImGui.text("Is active: " + (rb.isActive() ? "true" : "false"));
        if(!rb.isActive() && ImGui.button("Activate")){
            rb.activate();
        }
        ImGui.text("Is in world: " + (rb.isInWorld() ? "true" : "false"));
    }

    @Override
    public void onVariableChanged() {
        rb.setDamping(0.0f, angularDamping);
        rb.setMassProps(mass, new Vector3f(0,0,0));
        rb.setRestitution(restitution);
    }

    public void setPosition(Vector3f position, Quat4f rotation){
        Transform transform = new Transform(new Matrix4f(rotation, position, 1.0f));

        rb.setWorldTransform(transform);
        rb.getMotionState().setWorldTransform(transform);
        rb.setLinearVelocity(new Vector3f(0.0f, 0.0f, 0.0f));
        rb.setAngularVelocity(new Vector3f(0.0f, 0.0f, 0.0f));
        rb.clearForces();
    }


    @Override
    public void onAdded(Entity parent){
        super.onAdded(parent);
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
