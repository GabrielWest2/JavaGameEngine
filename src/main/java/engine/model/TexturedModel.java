package engine.model;

import engine.texture.Texture;

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
