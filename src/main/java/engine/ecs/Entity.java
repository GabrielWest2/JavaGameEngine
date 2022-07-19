package engine.ecs;

import engine.ecs.component.Transform;
import engine.model.Model;
import org.joml.Vector3f;

public class Entity {
    private final Transform transform;
    private Model model;

    public Entity(Transform transform, Model model) {
        this.transform = transform;
        this.model = model;
    }

    public Entity(Model model) {
        this.transform = new Transform(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));
        this.model = model;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public Transform getTransform() {
        return transform;
    }
}
