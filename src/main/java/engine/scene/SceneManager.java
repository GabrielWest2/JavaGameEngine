package engine.scene;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import editor.ExplorerWindow;
import engine.ecs.Component;
import engine.ecs.Entity;
import engine.ecs.component.Transform;
import engine.serialization.ComponentDeserializer;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class SceneManager {
    public static Scene loadedScene;
    private static Gson gson;


    public static void init(){
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .create();

        loadedScene = new Scene("testScene");
    }

    /**
     * Saves the currently loaded {@code Scene} to a file
     * @param path the path to save the loaded {@code Scene} to
     */
    public static void save(String path) {
        String json = gson.toJson(loadedScene);
        try {
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(path), StandardCharsets.UTF_8))) {
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
    public static void load(String path) {
        File file = new File(path);
        String str = null;
        try {
            str = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene loaded = gson.fromJson(str, Scene.class);
        System.out.println("Loaded " + loaded.getName());
        loadedScene = loaded;
        ExplorerWindow.selectedEntity = null;
        for(Entity e : loaded.getEntities()){
            e.setTransform(e.getComponent(Transform.class));
            for(Component component : e.getComponents()){
                component.onAdded(e);
                component.onVariableChanged();
            }
        }
    }
}
