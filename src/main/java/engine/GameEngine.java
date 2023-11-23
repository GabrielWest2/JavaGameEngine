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

import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;


public class GameEngine {

    public static GameEngine instance;


    public static void main(String[] args) {
        instance = new GameEngine();
        instance.run();
    }

    public static GameEngine getInstance() {
        return instance;
    }

    public void run() {
        DisplayManager.initOpenGL(this);
        loop();
        glfwFreeCallbacks(DisplayManager.window);
        glfwDestroyWindow(DisplayManager.window);
        ModelCreator.cleanUp();
        TextureLoader.cleanUp();
        Renderer.cleanUp();
        AudioManager.cleanUp();
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }


    private void loop() {
        Renderer.init();
        SceneManager.init();
        PostProcessing.init();
        ConsoleWindow.init();
        Physics.init();
        TerrainManager.init();
        WaterManger.init();
        AudioManager.init();

        while (!glfwWindowShouldClose(DisplayManager.window)) {
            Renderer.beginFrame();
            Time.updateTime();
            Mouse.update();
            SceneManager.loadedScene.camera.update();
            Physics.logic();
            Physics.render();
            LuaScriptingManager.Update();




            if(Mouse.isMousePressed(0))
                LuaScriptingManager.LeftClick();

            Renderer.renderGame();

            Renderer.endScene(SceneManager.loadedScene.camera, ExplorerWindow.selectedEntity);
        }
    }
}
