package engine.rendering.model;

import engine.rendering.texture.Texture;
import org.joml.Matrix4f;

public class TerrainModel extends Model {

    private final Texture splat;

    private final Texture t1;

    private final Texture t2;

    private final Texture t3;

    private final Texture t4;

    private final Matrix4f transformationMatrix;

    public TerrainModel(int vaoID, int vertexCount, Texture splat, Texture t1, Texture t2, Texture t3, Texture t4, Matrix4f transformation) {
        super(vaoID, vertexCount);
        this.splat = splat;
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
        this.t4 = t4;
        this.transformationMatrix = transformation;
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

    public Matrix4f getTransformationMatrix() {
        return transformationMatrix;
    }
}
