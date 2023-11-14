package engine.rendering.yaycoolnewmodels;

import engine.rendering.model.Model;
import engine.rendering.model.ModelLoader;

import java.util.ArrayList;
import java.util.List;

public class ComplexModel {
    private List<Model> meshes = new ArrayList<>();


    public ComplexModel(String path) {
        this.meshes = ModelLoader.loadUsingAssimp(path);
    }

    public List<Model> getMeshes() {
        return meshes;
    }

    public void setMeshes(List<Model> meshes) {
        this.meshes = meshes;
    }
}
