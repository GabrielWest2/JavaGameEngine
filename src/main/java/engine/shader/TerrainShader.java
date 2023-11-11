package engine.shader;

import engine.ecs.Light;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class TerrainShader extends ShaderProgram {

    private static final String vertexPath = "src/main/java/engine/shader/src/terrain/vertex.shader";

    private static final String fragmentPath = "src/main/java/engine/shader/src/terrain/fragment.shader";

    public TerrainShader() {
        super(vertexPath, fragmentPath);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
        super.bindAttribute(2, "normal");
    }

    @Override
    public void loadProjectionMatrix(Matrix4f matrix) {
        super.loadUniform(super.getUniformLocation("projectionMatrix"), matrix);
    }

    public void loadViewMatrix(Matrix4f matrix) {
        super.loadUniform(super.getUniformLocation("viewMatrix"), matrix);
    }

    public void loadLight(Light light) {
        super.loadUniform(super.getUniformLocation("lightPosition"), light.getPosition());
        super.loadUniform(super.getUniformLocation("lightColor"), light.getColor());
    }

    public void connectTextures(){
        super.loadUniform(super.getUniformLocation("splatmap"), 0);
        super.loadUniform(super.getUniformLocation("tex1"), 1);
        super.loadUniform(super.getUniformLocation("tex2"), 2);
        super.loadUniform(super.getUniformLocation("tex3"), 3);
        super.loadUniform(super.getUniformLocation("tex4"), 4);
    }

    public void setMaterial(float shineDamper, float reflectivity) {
        super.loadUniform(super.getUniformLocation("shineDamper"), shineDamper);
        super.loadUniform(super.getUniformLocation("reflectivity"), reflectivity);
    }

    public void setClipPlane(Vector4f plane) {
        super.loadUniform(super.getUniformLocation("plane"), plane);
    }

    public void setTextureScales(float scale1, float scale2, float scale3, float scale4){
        super.loadUniform(super.getUniformLocation("textureScale1"), scale1);
        super.loadUniform(super.getUniformLocation("textureScale2"), scale2);
        super.loadUniform(super.getUniformLocation("textureScale3"), scale3);
        super.loadUniform(super.getUniformLocation("textureScale4"), scale4);
    }

}
