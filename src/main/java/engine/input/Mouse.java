package engine.input;

import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;

public class Mouse {

    private static float x = 0;

    private static float y = 0;

    private static float dx = 0;

    private static float dy = 0;


    public static void update() {
        dx = x - ImGui.getMousePosX();
        dy = y - ImGui.getMousePosY();
        x = ImGui.getMousePosX();
        y = ImGui.getMousePosY();
    }

    public static boolean isMousePressed(int i) {
        return ImGui.isMouseDown(i);
    }

    public static boolean isMouseClicked(int i) {
        return ImGui.isMouseClicked(i);
    }

    private static ImVec2 getMousePos() {
        return ImGui.getMousePos();
    }

    public static float getMouseDx() {
        return dx;
    }

    public static float getMouseDy() {
        return dy;
    }

    public static float getX() {
        return x;
    }

    public static float getY() {
        return y;
    }

    public static Vector2f getPos() {
        return new Vector2f(x, y);
    }
}
