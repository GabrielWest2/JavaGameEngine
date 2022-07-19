package engine.texture;

import engine.model.Model;

public class NormalMappedModel extends Model {
    private final Texture texture;
    private final Texture normalMapTexture;

    public NormalMappedModel(int vaoID, int vertexCount, Texture texture, Texture normalMapTexture) {
        super(vaoID, vertexCount);
        this.texture = texture;
        this.normalMapTexture = normalMapTexture;
    }

    public Texture getTexture() {
        return texture;
    }

    public Texture getNormalMap() {
        return normalMapTexture;
    }
}

