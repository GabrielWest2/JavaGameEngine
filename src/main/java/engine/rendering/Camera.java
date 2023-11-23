package engine.rendering;

import editor.GameViewportWindow;
import engine.input.Keyboard;
import engine.input.Mouse;
import engine.util.MatrixBuilder;
import engine.util.Time;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {

    private static final float MOVEMENT_SPEED = 10f;

    private static final float FAST_MOVEMENT_SPEED = 90f;

    private Vector3f position;

    private Vector3f rotation;

    private transient Matrix4f viewMatrix;

    public Camera(Vector3f position, Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;
        viewMatrix = MatrixBuilder.createViewMatrix(this);
    }


    public void update() {
        if (GameViewportWindow.focused) {
            float vx = 0;
            float vy = 0;
            float vz = 0;
            boolean ctrl = Keyboard.isKeyPressed(GLFW_KEY_LEFT_CONTROL);
            float speed = (ctrl ? FAST_MOVEMENT_SPEED : MOVEMENT_SPEED);
            if (Keyboard.isKeyPressed(GLFW_KEY_W)) {
                vx += -Math.sin(Math.toRadians(rotation.y));
                vz += -Math.cos(Math.toRadians(rotation.y));
            }
            if (Keyboard.isKeyPressed(GLFW_KEY_S)) {
                vx += Math.sin(Math.toRadians(rotation.y));
                vz += Math.cos(Math.toRadians(rotation.y));
            }
            if (Keyboard.isKeyPressed(GLFW_KEY_A)) {
                vx += -Math.sin(Math.toRadians(rotation.y + 90));
                vz += -Math.cos(Math.toRadians(rotation.y + 90));
            }
            if (Keyboard.isKeyPressed(GLFW_KEY_D)) {
                vx += -Math.sin(Math.toRadians(rotation.y - 90));
                vz += -Math.cos(Math.toRadians(rotation.y - 90));
            }
            if (Keyboard.isKeyPressed(GLFW_KEY_SPACE)) {
                vy += 1;
            }
            if (Keyboard.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
                vy -= 1;
            }

            Vector3f move = new Vector3f(Time.getDeltaTime() * vx * speed, Time.getDeltaTime() * vy * speed, Time.getDeltaTime() * vz * speed);
            this.moveBy(move);
            if(Mouse.isMousePressed(1)) {
                this.rotateBy(new Vector3f(Mouse.getMouseDy() / 10f, Mouse.getMouseDx() / 10f, 0));
            }
        }
        this.viewMatrix = MatrixBuilder.createViewMatrix(this);
    }

    public void waterInvert(float level){
        position.y = level - (position.y - level);
        rotation.x = -rotation.x;
        this.viewMatrix = MatrixBuilder.createViewMatrix(this);
    }

    public void moveBy(Vector3f vec) {
        this.position.add(vec);
    }

    public void rotateBy(Vector3f vec) {
        this.rotation = new Vector3f(rotation.x + vec.x, rotation.y + vec.y, 0);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }
}
