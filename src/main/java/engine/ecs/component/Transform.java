package engine.ecs.component;

import editor.CustomHudName;
import engine.ecs.Component;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.vecmath.Quat4f;

public class Transform extends Component {
    @CustomHudName(displayName = "Position")
    private Vector3f position = new Vector3f(0, 0, 0);
    @CustomHudName(displayName = "Rotation")
    private Vector3f rotation = new Vector3f(0, 0, 0);
    @CustomHudName(displayName = "Scale")
    private Vector3f scale = new Vector3f(1, 1, 1);

    public Transform(Vector3f position, Vector3f rotation, Vector3f scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public Transform(Vector3f position, Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public Transform() {

    }

    public Transform(Vector3f position) {
        this.position = position;
    }

    public void moveBy(Vector3f vec) {
        this.position.add(vec);
    }

    public void rotateBy(Vector3f vec) {
        this.rotation.add(vec);
    }

    public void scaleBy(Vector3f vec) {
        this.scale.add(vec);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public Vector3f getScale() {
        return scale;
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }

    @Override
    public boolean canBeRemoved() {
        return false;
    }

    @Override
    public void onVariableChanged(){
        Rigidbody3D rb = entity.getComponent(Rigidbody3D.class);
        if(rb != null){
            Quaternionf f = new Quaternionf();
            f.rotateXYZ(rotation.x, rotation.y, rotation.z);
            rb.setPosition(new javax.vecmath.Vector3f(position.x, position.y, position.z), new Quat4f(f.x, f.y, f.z, f.w));
        }
    }
}
