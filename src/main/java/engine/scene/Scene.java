package engine.scene;

import java.util.ArrayList;
import java.util.List;

public class Scene {
    private final List<GameObject> objects = new ArrayList<>();

    public void addGameObject(GameObject object) {
        objects.add(object);
    }

}
