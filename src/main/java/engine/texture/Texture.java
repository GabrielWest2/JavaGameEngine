package engine.texture;

import org.lwjgl.opengl.GL13;

public class Texture {
    private final int textureID;

    public Texture(int textureID) {
        this.textureID = textureID;
    }

    public int getTextureID() {
        return textureID;
    }

    public void delete() {
        GL13.glDeleteTextures(textureID);
    }
}
