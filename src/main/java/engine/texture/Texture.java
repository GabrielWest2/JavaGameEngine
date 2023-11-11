package engine.texture;

import org.lwjgl.opengl.GL13;

public record Texture(int textureID, String filepath) {

    public void delete() {
        GL13.glDeleteTextures(textureID);
    }

    public String getFilepath() {
        return filepath;
    }
}
