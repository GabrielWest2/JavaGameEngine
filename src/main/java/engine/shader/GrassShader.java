package engine.shader;

import engine.ecs.Light;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class GrassShader extends ShaderProgram {

    private static final String vertexPath = "src/main/java/engine/shader/src/grass/vertex.shader";

    private static final String fragmentPath = "src/main/java/engine/shader/src/grass/fragment.shader";

    public GrassShader() {
        super(vertexPath, fragmentPath);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
        super.bindAttribute(2, "normal");
    }

    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadUniform(super.getUniformLocation("transformationMatrix"), matrix);

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

    public void setMaterial(float shineDamper, float reflectivity) {
        super.loadUniform(super.getUniformLocation("shineDamper"), shineDamper);
        super.loadUniform(super.getUniformLocation("reflectivity"), reflectivity);
    }

    public void loadMovement(float moveFactor){
        super.loadUniform(super.getUniformLocation("moveFactor"), moveFactor);
    }

    public void setClipPlane(Vector4f plane) {
        super.loadUniform(super.getUniformLocation("plane"), plane);
    }
}
