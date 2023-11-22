package engine.ecs;

import engine.GameEngine;
import engine.ecs.component.ModelRenderer;
import engine.ecs.component.Transform;
import engine.rendering.model.Model;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Entity {

    private String name = "New Entity";

    private transient Transform transform;

    private final List<Component> components;

    private boolean isLocked = false;


    public Entity(Transform transform, Model model) {
        this.transform = transform;
        components = new ArrayList<>();
        addComponent(transform);
        addComponent(new ModelRenderer(model));
    }

    public Entity(String name, Transform transform) {
        this.name = name;
        this.transform = transform;
        components = new ArrayList<>();
        addComponent(transform);

    }

    public Entity() {
        this.transform = new Transform(new Vector3f(0, 0, 0));
        components = new ArrayList<>();
        addComponent(transform);

    }

    public Entity(String name) {
        this.name = name;
        this.transform = new Transform(new Vector3f(0, 0, 0));
        components = new ArrayList<>();
        addComponent(transform);

    }

    public List<Component> getComponents() {
        return components;
    }

    public Entity addComponent(Component component) {
        component.entity = this;
        components.add(component);
        component.onAdded(this);
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

    public void setTransform(Transform component) {
        this.transform = component;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }
}
