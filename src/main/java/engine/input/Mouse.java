package engine.input;

import editor.GameViewportWindow;
import engine.display.DisplayManager;
import imgui.ImGui;
import imgui.ImVec2;

import static org.lwjgl.glfw.GLFW.*;

public class Mouse {
    private static float x = 0, y = 0, dx = 0, dy = 0;
    private static boolean mouseHidden = false;

    public static boolean isMouseHidden() {
        return mouseHidden;
    }

    public static void setMouseHidden(boolean hidden) {
        mouseHidden = hidden;
        //glfwSetInputMode(DisplayManager.window, GLFW_CURSOR, hidden ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);
    }

    public static void update() {
        dx = x - ImGui.getMousePosX();
        dy = y - ImGui.getMousePosY();
        x = ImGui.getMousePosX();
        y = ImGui.getMousePosY();

        if (!mouseHidden) {
            dx = 0;
            dy = 0;
            if (ImGui.isMouseClicked(0) && GameViewportWindow.focused) {
                setMouseHidden(true);
            }
        } else {
            if (Keyboard.isKeyPressed(GLFW_KEY_ESCAPE)) {
                setMouseHidden(false);
            }
        }
    }

    public static boolean isMousePressed(int i) {
        return ImGui.isMouseDown(i);
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
}
