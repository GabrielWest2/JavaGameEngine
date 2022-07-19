package engine.util;

import engine.Camera;
import engine.shader.display.DisplayManager;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class MatrixBuilder {

    private static final float FOV = 90;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1500f;

    public static Matrix4f createTransformationMatrix(Vector3f offset, Vector3f rotation, Vector3f scale) {
        Matrix4f worldMatrix = new Matrix4f();
        worldMatrix.identity().translate(offset).
                rotateX((float) Math.toRadians(rotation.x)).
                rotateY((float) Math.toRadians(rotation.y)).
                rotateZ((float) Math.toRadians(rotation.z)).
                scale(scale.x, scale.y, scale.z);
        return worldMatrix;
    }

    public static Matrix4f createProjectionMatrix() {
        float aspectRatio = (float) DisplayManager.getWidth() / (float) DisplayManager.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        Matrix4f matrix = new Matrix4f();
        matrix.m00(x_scale);
        matrix.m11(y_scale);
        matrix.m22(-((FAR_PLANE + NEAR_PLANE) / frustum_length));
        matrix.m23(-1);
        matrix.m32(-((2 * NEAR_PLANE * FAR_PLANE) / frustum_length));
        matrix.m33(0);
        return matrix;
    }

    public static Matrix4f createViewMatrix(Camera camera) {
        Matrix4f matrix = new Matrix4f();
        Vector3f forward = new Vector3f();
        Matrix3f normal = new Matrix3f();
        //matrix.identity().translate(camera.getPosition()).setRotationXYZ((float)Math.toRadians(camera.getRotation().x), (float)Math.toRadians(camera.getRotation().y), (float)Math.toRadians(camera.getRotation().z));
        matrix.identity()
                .rotateX((float) Math.toRadians(-camera.getRotation().x))
                .rotateY((float) Math.toRadians(-camera.getRotation().y))
                .rotateZ((float) Math.toRadians(-camera.getRotation().z))
                .translate(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z)
                .normal(normal) // <- returns the 'normal' matrix
                .invert()
                .transform(forward.set(0, 0, -1)); // <- what is forward?
        return matrix;
    }

    public static Matrix4f createStationaryViewMatrix(Camera camera) {
        Matrix4f matrix = new Matrix4f();
        Vector3f forward = new Vector3f();
        Matrix3f normal = new Matrix3f();
        matrix.identity()
                .rotateX((float) Math.toRadians(-camera.getRotation().x))
                .rotateY((float) Math.toRadians(-camera.getRotation().y))
                .rotateZ((float) Math.toRadians(-camera.getRotation().z))
                .normal(normal) // <- returns the 'normal' matrix
                .invert()
                .transform(forward.set(0, 0, -1)); // <- what is forward?
        return matrix;
    }
}
