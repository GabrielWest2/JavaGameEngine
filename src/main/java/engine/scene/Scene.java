package engine.scene;

import engine.ecs.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gabed
 * @Date 7/22/2022
 */
public class Scene {
    private final List<Entity> entities = new ArrayList<>();
    private String name;

    public Scene(String name) {
        this.name = name;
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

    public List<Entity> getEntities() {
        return entities;
    }
}
