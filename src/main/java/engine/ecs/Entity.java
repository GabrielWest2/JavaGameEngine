package engine.ecs;

import engine.GameEngine;
import engine.ecs.component.ModelRenderer;
import engine.ecs.component.Transform;
import engine.model.Model;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Entity {
    private final Transform transform;
    private final List<Component> components;
    private String name = "New Entity";

    public Entity(Transform transform, Model model) {
        this.transform = transform;
        components = new ArrayList<>();
        components.add(transform);
        components.add(new ModelRenderer(model));
    }

    public Entity(String name, Transform transform) {
        this.name = name;
        this.transform = transform;
        components = new ArrayList<>();
        components.add(transform);

    }

    public Entity() {
        this.transform = new Transform(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));
        components = new ArrayList<>();
        components.add(transform);

    }

    public Entity(String name) {
        this.name = name;
        this.transform = new Transform(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));
        components = new ArrayList<>();
        components.add(transform);

    }

    public List<Component> getComponents() {
        return components;
    }

    public Entity addComponent(Component component) {
        component.entity = this;
        components.add(component);
        component.onAdded();
        return this;
    }

    public Entity removeComponent(Component component) {
        component.onRemoved();
        component.entity = null;
        components.remove(component);
        return this;
    }

    public <T extends Component> T getComponent(Class<T> componentClass) {
        for (Component c : components) {
            if (componentClass.isAssignableFrom(c.getClass())) {
                try {
                    return componentClass.cast(c);
                } catch (ClassCastException cce) {
                    System.out.println("Failed to cast class.");
                }
            }
        }
        return null;
    }

    public void destroy() {
        GameEngine.getInstance().loadedScene.removeEntity(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Transform getTransform() {
        return transform;
    }
}
