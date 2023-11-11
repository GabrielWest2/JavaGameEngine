package engine.util;

import engine.Camera;
import engine.GameEngine;
import engine.display.DisplayManager;
import engine.input.Mouse;
import org.joml.*;

import java.lang.Math;

public class MatrixBuilder {

    private static final float FOV = 90;

    private static final float NEAR_PLANE = 0.1f;

    private static final float FAR_PLANE = 1500f;

    public static final Matrix4f defaultTransformation = new Matrix4f().identity();

    public static Matrix4f createTransformationMatrix(Vector3f offset, Vector3f rotation, Vector3f scale) {
        Matrix4f worldMatrix = new Matrix4f();
        worldMatrix.identity().translate(offset).
                rotateX((float) Math.toRadians(rotation.x)).
                rotateY((float) Math.toRadians(rotation.y)).
                rotateZ((float) Math.toRadians(rotation.z)).
                scale(scale.x, scale.y, scale.z);
        return worldMatrix;
    }

    public static Matrix4f createTransformationMatrix(Vector3f offset, Quaternionf rotation, Vector3f scale) {
        Matrix4f worldMatrix = new Matrix4f();
        try {
            worldMatrix.identity()
                    .translate(offset)
                    .rotate(((Quaternionf)rotation.clone()).normalize())
                    .scale(scale.x, scale.y, scale.z);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return worldMatrix;
    }

    public static Matrix4f createProjectionMatrix(int width, int height) {
        float aspectRatio = (float) width / (float) height;
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

    public static Vector3f calculateMouseRay(int width, int height){
        float mouseX = Mouse.getX();
        float mouseY = Mouse.getY();
        Vector2f normalizedCoords = getNormalizedDeviceCoords(mouseX, mouseY);
        Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1f, 1f);
        Vector4f eyeCoords = toEyeCoords(clipCoords, width, height);
        Vector3f worldRay = toWorldCoords(eyeCoords);
        return worldRay;
    }

    public static Vector4f toEyeCoords(Vector4f clipCoords, int width, int height){
        Matrix4f invertedProjection = createProjectionMatrix(width, height).invert();
        Vector4f eyeCoords = invertedProjection.transform(clipCoords);
        return new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);
    }

    public static Vector3f toWorldCoords(Vector4f eyeCoords){
        Matrix4f invertedView = createViewMatrix(GameEngine.getInstance().camera).invert();
        Vector4f rayWorld = invertedView.transform(eyeCoords);
        Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
        mouseRay.normalize();

        return mouseRay;
    }

    public static Vector2f getNormalizedDeviceCoords(float mouseX, float mouseY){
        float x = (2f * mouseX) / DisplayManager.getWidth() - 1f;
        float y = (2f * mouseY) / DisplayManager.getHeight() - 1f;
        return new Vector2f(x, y);
    }

    public static Matrix4f createViewMatrix(Camera camera) {
        Matrix4f matrix = new Matrix4f();
        Vector3f forward = new Vector3f();
        Matrix3f normal = new Matrix3f();
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
