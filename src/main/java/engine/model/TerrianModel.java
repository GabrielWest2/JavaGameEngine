package engine.model;

import engine.texture.Texture;

public class TerrianModel extends Model {
    private Texture texture;

    public TerrianModel(int vaoID, int vertexCount) {
        super(vaoID, vertexCount);
    }

    public TerrianModel(int vaoID, int vertexCount, Texture texture) {
        super(vaoID, vertexCount);
        this.texture = texture;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }
}
