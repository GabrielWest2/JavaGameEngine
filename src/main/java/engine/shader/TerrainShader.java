package engine.shader;

import org.joml.Matrix4f;

public class TerrainShader extends ShaderProgram {

    private static final String vertexPath = "src/main/java/engine/shader/src/terrain/vertex.shader";
    private static final String fragmentPath = "src/main/java/engine/shader/src/terrain/fragment.shader";

    public TerrainShader() {
        super(vertexPath, fragmentPath);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "triangleColor");
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

}
