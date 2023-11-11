package engine.shader;

import org.joml.Matrix4f;
import org.joml.Vector4f;

public class MousePickingShader extends ShaderProgram {

    private static final String vertexPath = "src/main/java/engine/shader/src/mousepicking/vertex.shader";

    private static final String fragmentPath = "src/main/java/engine/shader/src/mousepicking/fragment.shader";

    public MousePickingShader() {
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

    public void loadColor(Vector4f color) {
        super.loadUniform(super.getUniformLocation("color"), color);
    }

}
