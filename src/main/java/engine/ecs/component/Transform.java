package engine.ecs.component;

import editor.CustomHudName;
import engine.ecs.Component;
import org.joml.Vector3f;

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
}
