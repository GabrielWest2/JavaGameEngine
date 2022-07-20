package engine.shader;

import org.joml.Matrix4f;

public class GridShader extends ShaderProgram {

    private static final String vertexPath = "src/main/java/engine/shader/src/grid/vertex.shader";
    private static final String fragmentPath = "src/main/java/engine/shader/src/grid/fragment.shader";

    public GridShader() {
        super(vertexPath, fragmentPath);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

    @Override
    public void loadProjectionMatrix(Matrix4f matrix) {
        super.loadUniform(super.getUniformLocation("projectionMatrix"), matrix);
    }

    public void loadViewMatrix(Matrix4f matrix) {
        super.loadUniform(super.getUniformLocation("viewMatrix"), matrix);
    }

}
