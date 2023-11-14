package engine.rendering.model;

import engine.rendering.texture.Texture;

public class VegetationModel extends Model {
    private final Texture texture;

    public VegetationModel(int vaoID, int vertexCount, Texture texture) {
        super(vaoID, vertexCount);
        this.texture = texture;
    }

    public Texture getTexture() {
        return texture;
    }
}
