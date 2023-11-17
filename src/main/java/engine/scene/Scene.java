package engine.scene;

import editor.ExplorerWindow;
import engine.GameEngine;
import engine.ecs.Component;
import engine.ecs.Entity;
import engine.ecs.component.Transform;
import engine.rendering.lighting.SceneLights;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
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

    public Scene(String name) {
        this.name = name;
        lights = new SceneLights();
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

    /**
     * Saves the currently loaded {@code Scene} to a file
     * @param path the path to save the loaded {@code Scene} to
     */
    public void save(String path) {
        String json = GameEngine.gson.toJson(this);
        try {
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(path), "utf-8"))) {
                writer.write(json);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deserializes and loads a {@code Scene} from a file
     * @param path the path of the {@code Scene} file
     */
    public void load(String path) {
        File file = new File(path);
        String str = null;
        try {
            str = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
           e.printStackTrace();
        }

        Scene loaded = GameEngine.gson.fromJson(str, Scene.class);
        System.out.println("Loaded " + loaded.getName());
        GameEngine.loadedScene = loaded;
        ExplorerWindow.selectedEntity = null;
        for(Entity e : loaded.entities){
            e.setTransform(e.getComponent(Transform.class));
            for(Component component : e.getComponents()){
                component.onAdded(e);
                component.onVariableChanged();
            }
        }
    }
}
