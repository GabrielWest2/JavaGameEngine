package engine;

import editor.ConsoleWindow;
import editor.ExplorerWindow;
import engine.audio.AudioManager;
import engine.input.Mouse;
import engine.physics.Physics;
import engine.postprocessing.PostProcessing;
import engine.rendering.DisplayManager;
import engine.rendering.Renderer;
import engine.rendering.model.ModelCreator;
import engine.rendering.texture.TextureLoader;
import engine.scene.SceneManager;
import engine.util.Time;
import scripting.LuaScriptingManager;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;


public class GameEngine {

    public static boolean editorMode = false;

    public static void main(String[] args) {
        GameEngine.run();
    }

    public static void run() {
        DisplayManager.initOpenGL();
        loop();
        glfwFreeCallbacks(DisplayManager.window);
        glfwDestroyWindow(DisplayManager.window);
        ModelCreator.cleanUp();
        TextureLoader.cleanUp();
        Renderer.cleanUp();
        AudioManager.cleanUp();
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private static void loop() {
        Renderer.init();
        SceneManager.init();
        PostProcessing.init();
        if(editorMode) {
            ConsoleWindow.init();
        }
        Physics.init();
        TerrainManager.init();
        WaterManger.init();
        AudioManager.init();
        LuaScriptingManager.loadCode();

        LuaScriptingManager.awake();
        LuaScriptingManager.start();

        while (!glfwWindowShouldClose(DisplayManager.window)) {
            Renderer.beginFrame();
            Time.updateTime();
            Mouse.update();
            SceneManager.loadedScene.camera.update();
            Physics.logic();
            LuaScriptingManager.update();
            Renderer.renderGame();
            Renderer.endScene(SceneManager.loadedScene.camera, ExplorerWindow.selectedEntity);
        }
    }
}
