package engine.rendering.model;

/**
 * @author gabed
 * @Date 7/20/2022
 */
public class SkyboxModel extends Mesh {
    private final int cubemapTexture;

    public SkyboxModel(int vaoID, int vertexCount, int cubemapTexture) {
        super(vaoID, vertexCount);
        this.cubemapTexture = cubemapTexture;
    }

    public int getCubemapTexture() {
        return cubemapTexture;
    }
}
