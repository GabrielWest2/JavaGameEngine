package engine.rendering.yaycoolnewmodels;

import engine.rendering.texture.Texture;
import engine.rendering.texture.TextureLoader;
import org.joml.Vector4f;

public class Material {

    public static final Vector4f DEFAULT_COLOR = new Vector4f(1, 1, 1, 1);
    private Vector4f diffuseColor = DEFAULT_COLOR;

    private Vector4f ambientColor = DEFAULT_COLOR;

    private Vector4f specularColor = DEFAULT_COLOR.div(4);

    private float reflectance = 0.0f;

    private String texturePath;

    private transient Texture loadedTexture;

    public Material() {

    }

    public void setDiffuseColor(Vector4f diffuse) {
        this.diffuseColor = diffuse;
    }

    public void setTexturePath(String texturePath) {
        this.texturePath = texturePath;
        this.loadedTexture = TextureLoader.loadTexture(texturePath);
    }

    public Vector4f getDiffuseColor() {
        return diffuseColor;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public Texture getLoadedTexture() {
        return loadedTexture;
    }

    public void setAmbientColor(Vector4f ambientColor) {
        this.ambientColor = ambientColor;
    }

    public Vector4f getAmbientColor() {
        return ambientColor;
    }

    public void setSpecularColor(Vector4f specularColor) {
        this.specularColor = specularColor;
    }

    public Vector4f getSpecularColor() {
        return specularColor;
    }

    public void setReflectance(float reflectance) {
        this.reflectance = reflectance;
    }

    public float getReflectance() {
        return reflectance;
    }
}