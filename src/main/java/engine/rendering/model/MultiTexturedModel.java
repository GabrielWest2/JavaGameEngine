package engine.rendering.model;

import engine.rendering.texture.Texture;

import java.util.HashMap;

public class MultiTexturedModel extends Model {

    private final HashMap<String, TexturedModel> materialModels;

    private HashMap<String, Texture> materialTextures;

    public MultiTexturedModel(HashMap<String, TexturedModel> models, HashMap<String, Texture> textures) {
        super(0, 0);
        this.materialModels = models;
        this.materialTextures = textures;
    }

    public HashMap<String, TexturedModel> getMaterialModels() {
        return materialModels;
    }

    public HashMap<String, Texture> getMaterialTextures() {
        return materialTextures;
    }

    public void setTexture(String materialName, Texture texture){
        materialTextures.put(materialName, texture);
    }
}
