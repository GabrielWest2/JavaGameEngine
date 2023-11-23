package engine.rendering.model;

import java.util.List;

public class Model {
    private List<Mesh> meshes;


    public Model(String path) {
        this.meshes = ModelLoader.loadUsingAssimp(path);
    }

    public List<Mesh> getMeshes() {
        return meshes;
    }

    public void setMeshes(List<Mesh> meshes) {
        this.meshes = meshes;
    }
}
