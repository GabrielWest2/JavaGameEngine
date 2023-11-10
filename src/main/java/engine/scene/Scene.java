package engine.scene;

import editor.ExplorerWindow;
import engine.GameEngine;
import engine.ecs.Component;
import engine.ecs.Entity;
import engine.ecs.component.Transform;
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

    public void save() {

        String json = GameEngine.gson.toJson(this);
        try {
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("C:/Users/gabed/OneDrive/Documents/GitHub/JavaGameEngine/scenes/scene1.scn"), "utf-8"))) {
                writer.write(json);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //User user = gson.fromJson(json, User.class);
    }

    public void load() {
        File file = new File("C:/Users/gabed/OneDrive/Documents/GitHub/JavaGameEngine/scenes/scene1.scn");
        String str = null;
        try {
            str = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Scene loaded = GameEngine.gson.fromJson(str, Scene.class);
        System.out.println("Loaded " + loaded.getName());
        GameEngine.getInstance().loadedScene = loaded;
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
