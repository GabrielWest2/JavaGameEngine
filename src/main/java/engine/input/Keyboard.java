package engine.input;

import engine.GameEngine;
import engine.rendering.DisplayManager;
import imgui.ImGui;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwGetKey;

public class Keyboard {

    public static boolean isKeyPressed(int glfwKey) {
        return GameEngine.editorMode ? ImGui.isKeyDown(glfwKey) : glfwGetKey(DisplayManager.window, glfwKey) == GLFW_PRESS;
    }

    public static boolean isKeyPressedThisFrame(int glfwKey) {
        return GameEngine.editorMode ? ImGui.isKeyPressed(glfwKey) : glfwGetKey(DisplayManager.window, glfwKey) == GLFW_PRESS;
    }

}
