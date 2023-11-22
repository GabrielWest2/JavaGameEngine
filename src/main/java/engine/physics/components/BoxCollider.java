package engine.physics.components;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import editor.util.CustomHudName;
import editor.util.NoHudRender;
import engine.ecs.Component;
import engine.ecs.Entity;
import imgui.ImGui;
import imgui.type.ImBoolean;
import org.joml.Vector3f;

import static com.bulletphysics.collision.dispatch.CollisionObject.*;

public class BoxCollider extends Component {

    private Vector3f halfExtents = new Vector3f(0.5f);

    @CustomHudName(displayName = "Inertia")
    private Vector3f inertia = new Vector3f(0.5f);

    private boolean canDeactivate = false;

    @NoHudRender
    private boolean rotX;

    @NoHudRender
    private boolean rotY;

    @NoHudRender
    private boolean rotZ;


    private void updateCollider(){
        Rigidbody3D rb = entity.getComponent(Rigidbody3D.class);
        if(rb == null) {
            rb = new Rigidbody3D();
            entity.addComponent(rb);
        }
        CollisionShape shape = new BoxShape(new javax.vecmath.Vector3f(halfExtents.x, halfExtents.y, halfExtents.z));
        shape.calculateLocalInertia(rb.mass, new javax.vecmath.Vector3f(rotX ? inertia.x : 0, rotY ? inertia.y : 0, rotZ ? inertia.z : 0));
        rb.rb.setMassProps(rb.mass, new javax.vecmath.Vector3f(rotX ? inertia.x : 0, rotY ? inertia.y : 0, rotZ ? inertia.z : 0));
        rb.rb.updateInertiaTensor();
        rb.rb.setCollisionShape(shape);
        rb.rb.forceActivationState(canDeactivate ? WANTS_DEACTIVATION :  DISABLE_DEACTIVATION);
    }

    @Override
    public void onAdded(Entity parent){
        super.onAdded(parent);
        updateCollider();
    }

    @Override
    public void onVariableChanged(){
        updateCollider();
    }

    @Override
    public void GUI() {
        super.GUI();
        boolean edited = false;
        if(ImGui.checkbox("X", new ImBoolean(rotX))){
            rotX = !rotX;
            edited = true;
        }
        ImGui.sameLine();
        if(ImGui.checkbox("Y", new ImBoolean(rotY))){
            rotY = !rotY;
            edited = true;
        }
        ImGui.sameLine();
        if(ImGui.checkbox("Z", new ImBoolean(rotZ))){
            rotZ = !rotZ;
            edited = true;
        }
        ImGui.sameLine();
        ImGui.text("  Allow Rotation");

        if(edited){
            updateCollider();
        }
    }
}
