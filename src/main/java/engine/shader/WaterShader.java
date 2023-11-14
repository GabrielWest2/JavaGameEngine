package engine.shader;

import engine.Light;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class WaterShader extends ShaderProgram{
    private static final String vertexPath = "src/main/java/engine/shader/src/water/vertex.shader";

    private static final String fragmentPath = "src/main/java/engine/shader/src/water/fragment.shader";

    public WaterShader() {
        super(vertexPath, fragmentPath);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
    }

    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadUniform(super.getUniformLocation("transformationMatrix"), matrix);
    }

    public void connectTextures(){
        super.loadUniform(super.getUniformLocation("reflectionTexture"), 0);
        super.loadUniform(super.getUniformLocation("refractionTexture"), 1);
        super.loadUniform(super.getUniformLocation("dudv"), 2);
        super.loadUniform(super.getUniformLocation("normalMap"), 3);
        super.loadUniform(super.getUniformLocation("refractionDepthTexture"), 4);
    }

    public void loadLight(Light light){
        super.loadUniform(super.getUniformLocation("lightPosition"), light.getPosition());
        super.loadUniform(super.getUniformLocation("lightColor"), light.getColor());
    }

    public void setMaterial(float shineDamper, float reflectivity) {
        super.loadUniform(super.getUniformLocation("shineDamper"), shineDamper);
        super.loadUniform(super.getUniformLocation("reflectivity"), reflectivity);
    }

    public void loadWaterMovement(float moveFactor){
        super.loadUniform(super.getUniformLocation("moveFactor"), moveFactor);
    }

    public void loadCameraPosition(Vector3f pos){
        super.loadUniform(super.getUniformLocation("cameraPos"), pos);
    }

    @Override
    public void loadProjectionMatrix(Matrix4f matrix) {
        super.loadUniform(super.getUniformLocation("projectionMatrix"), matrix);
    }

    public void loadViewMatrix(Matrix4f matrix) {
        super.loadUniform(super.getUniformLocation("viewMatrix"), matrix);
    }
}