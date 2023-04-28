package engine.texture;

import org.lwjgl.opengl.GL13;

public record Texture(int textureID) {

    public void delete() {
        GL13.glDeleteTextures(textureID);
    }
}
