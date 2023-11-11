package engine.ecs.component;

import editor.CustomHudName;
import engine.ecs.Component;
import engine.ecs.Entity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.vecmath.Quat4f;

public class Transform extends Component {

    @CustomHudName(displayName = "Position")
    private Vector3f position = new Vector3f(0, 0, 0);

    @CustomHudName(displayName = "Rotation")
    private Quaternionf rotation = new Quaternionf();

    @CustomHudName(displayName = "Scale")
    private Vector3f scale = new Vector3f(1, 1, 1);

    public Transform(Vector3f position, Quaternionf rotation, Vector3f scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public Transform(Vector3f position, Quaternionf rotation) {
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


    public void scaleBy(Vector3f vec) {
        this.scale.add(vec);
    }

    public Vector3f getPosition() {
        return position;
    }

    public Entity setPosition(Vector3f position) {
        this.position = position;
        return this.entity;
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public void setRotation(Quaternionf rotation) {
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
            rb.setPosition(new javax.vecmath.Vector3f(position.x, position.y, position.z), new Quat4f(rotation.x, rotation.y, rotation.z, rotation.w));
        }
    }
}
