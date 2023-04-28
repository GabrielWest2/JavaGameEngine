package engine.input;

import imgui.ImGui;

public class Keyboard {

    public static boolean isKeyPressed(int glfwKey) {
        return ImGui.isKeyDown(glfwKey);
    }

    public static boolean isKeyPressedThisFrame(int glfwKey) {

        return ImGui.isKeyPressed(glfwKey);
    }

}
