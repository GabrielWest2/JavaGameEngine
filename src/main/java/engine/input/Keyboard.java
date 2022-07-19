package engine.input;

import imgui.ImGui;

public class Keyboard {
    /*private static HashMap<Integer, Boolean> keysPressed = new HashMap<>();


    public static void OnKeyEvent(long window, int key, int scancode, int action, int mods){
        if(action == GLFW_PRESS){
            keysPressed.put(key, true);
        }else if(action == GLFW_RELEASE){
            keysPressed.put(key, false);
        }
    }*/
    public static boolean isKeyPressed(int glfwKey) {
        /*if(!keysPressed.containsKey(glfwKey)){
            keysPressed.put(glfwKey, false);
        }*/
        //return keysPressed.get(glfwKey);
        return ImGui.isKeyDown(glfwKey);
    }

    public static boolean isKeyPressedThisFrame(int glfwKey) {
        /*if(!keysPressed.containsKey(glfwKey)){
            keysPressed.put(glfwKey, false);
        }*/
        //return keysPressed.get(glfwKey);
        return ImGui.isKeyPressed(glfwKey);
    }

}
