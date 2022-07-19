package engine;

import editor.GameViewportWindow;
import engine.input.Keyboard;
import engine.input.Mouse;
import engine.util.Time;
import org.joml.Math;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {
    private final float MOVEMENT_SPEED = 10f;
    private final float FAST_MOVEMENT_SPEED = 30f;
    private Vector3f position = new Vector3f(0, 0, 0);
    private Vector3f rotation = new Vector3f(0, 0, 0);


    public Camera(Vector3f position, Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public Camera() {
    }

    public void update() {
        if (GameViewportWindow.focused) {
            float vx = 0, vy = 0, vz = 0;
            boolean ctrl = Keyboard.isKeyPressed(GLFW_KEY_LEFT_CONTROL);
            if (Keyboard.isKeyPressed(GLFW_KEY_W)) {
                vx = -Math.sin(Math.toRadians(rotation.y)) * (ctrl ? FAST_MOVEMENT_SPEED : MOVEMENT_SPEED);
                vz = -Math.cos(Math.toRadians(rotation.y)) * (ctrl ? FAST_MOVEMENT_SPEED : MOVEMENT_SPEED);
            }
            if (Keyboard.isKeyPressed(GLFW_KEY_S)) {
                vx = Math.sin(Math.toRadians(rotation.y)) * (ctrl ? FAST_MOVEMENT_SPEED : MOVEMENT_SPEED);
                vz = Math.cos(Math.toRadians(rotation.y)) * (ctrl ? FAST_MOVEMENT_SPEED : MOVEMENT_SPEED);
            }
            if (Keyboard.isKeyPressed(GLFW_KEY_A)) {
                vx = -Math.sin(Math.toRadians(rotation.y + 90)) * (ctrl ? FAST_MOVEMENT_SPEED : MOVEMENT_SPEED);
                vz = -Math.cos(Math.toRadians(rotation.y + 90)) * (ctrl ? FAST_MOVEMENT_SPEED : MOVEMENT_SPEED);
            }
            if (Keyboard.isKeyPressed(GLFW_KEY_D)) {
                vx = -Math.sin(Math.toRadians(rotation.y - 90)) * (ctrl ? FAST_MOVEMENT_SPEED : MOVEMENT_SPEED);
                vz = -Math.cos(Math.toRadians(rotation.y - 90)) * (ctrl ? FAST_MOVEMENT_SPEED : MOVEMENT_SPEED);
            }
            if (Keyboard.isKeyPressed(GLFW_KEY_SPACE)) {
                vy += (ctrl ? FAST_MOVEMENT_SPEED : MOVEMENT_SPEED);
            }
            if (Keyboard.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
                vy -= (ctrl ? FAST_MOVEMENT_SPEED : MOVEMENT_SPEED);
            }

            Vector3f move = new Vector3f(Time.getDeltaTime() * vx, Time.getDeltaTime() * vy, Time.getDeltaTime() * vz);
            this.moveBy(move);
            this.rotateBy(new Vector3f(Mouse.getMouseDy() / 20f, Mouse.getMouseDx() / 20f, 0));
        }
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
}
