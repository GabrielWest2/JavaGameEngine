package engine.rendering.model;

import engine.rendering.Material;

public class Mesh {

    private final int vaoID;
    
    private final int vertexCount;

    private Material material;

    public Mesh(int vaoID, int vertexCount) {
        this.vaoID = vaoID;
        this.vertexCount = vertexCount;
    }

    public int getVaoID() {
        return vaoID;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}
