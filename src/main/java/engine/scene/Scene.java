package engine.scene;

import engine.ecs.Entity;
import engine.rendering.Camera;
import engine.rendering.lighting.SceneLights;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gabed
 * @Date 7/22/2022
 */
public class Scene {

    private final List<Entity> entities = new ArrayList<>();

    private String name;

    private SceneLights lights;

    public Camera camera;

    public Scene(String name) {
        this.name = name;
        lights = new SceneLights();
        camera = new Camera(new Vector3f(30, 10, 30), new Vector3f(0, 25, 0));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    public void removeEntity(int i) {
        entities.remove(i);
    }

    public List<Entity> getEntitiesByName(String name) {
        List<Entity> list = new ArrayList<>();
        for (Entity entity : entities) {
            if (entity.getName().equals(name))
                list.add(entity);
        }
        return list;
    }

    public Entity getEntityByName(String name) {
        for (Entity entity : entities) {
            if (entity.getName().equals(name))
                return entity;
        }
        return null;
    }

    public SceneLights getLights() {
        return lights;
    }

    public void setLights(SceneLights lights) {
        this.lights = lights;
    }

    /**
     * @return all entities in this scene
     */
    public List<Entity> getEntities() {
        return entities;
    }


}
