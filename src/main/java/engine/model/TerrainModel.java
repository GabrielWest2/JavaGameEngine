package engine.model;

import engine.texture.Texture;

public class TerrainModel extends Model {
    private Texture splat, t1, t2, t3, t4;

    public TerrainModel(int vaoID, int vertexCount, Texture splat, Texture t1, Texture t2, Texture t3, Texture t4) {
        super(vaoID, vertexCount);
        this.splat = splat;
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
        this.t4 = t4;
    }

    public Texture getSplat() {
        return splat;
    }

    public Texture getT1() {
        return t1;
    }

    public Texture getT2() {
        return t2;
    }

    public Texture getT3() {
        return t3;
    }

    public Texture getT4() {
        return t4;
    }
}