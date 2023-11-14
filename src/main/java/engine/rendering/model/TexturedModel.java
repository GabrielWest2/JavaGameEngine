package engine.rendering.model;

import engine.rendering.texture.Texture;

public class TexturedModel extends Model {
    private final Texture texture;

    public TexturedModel(int vaoID, int vertexCount, Texture texture) {
        super(vaoID, vertexCount);
        this.texture = texture;
    }

    public Texture getTexture() {
        return texture;
    }
}
