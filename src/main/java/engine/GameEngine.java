package engine;

import editor.ConsoleWindow;
import engine.display.DisplayManager;
import engine.ecs.Entity;
import engine.ecs.Light;
import engine.ecs.component.Transform;
import engine.model.ModelCreator;
import engine.model.OBJLoader;
import engine.model.SkyboxModel;
import engine.postprocessing.PostProcessing;
import engine.shader.Framebuffer;
import engine.terrain.Terrain;
import engine.texture.TextureLoader;
import org.joml.Vector3f;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;


public class GameEngine {
    public static ModelCreator modelCreator;
    public static Renderer renderer;
    public static Camera camera;
    public static Framebuffer fb;
    public static Light light;
    public static Terrain terrain;


    public static ModelCreator getModelCreator() {
        return modelCreator;
    }


    public static void main(String[] args) {
        new GameEngine().run();
    }

    public void run() {
        DisplayManager.initOpenGL(this);
        loop();
        glfwFreeCallbacks(DisplayManager.window);
        glfwDestroyWindow(DisplayManager.window);
        modelCreator.cleanUp();
        TextureLoader.cleanUp();

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void loop() {
        modelCreator = new ModelCreator();
        renderer = new Renderer();
        renderer.Prepare();
        renderer.Prepare();


        PostProcessing.init();
        ConsoleWindow.init();

        camera = new Camera(new Vector3f(30, 10, 30), new Vector3f(0, 25, 0));
        fb = new Framebuffer(DisplayManager.getWidth(), DisplayManager.getHeight(), Framebuffer.DEPTH_TEXTURE);
        DisplayManager.setCallbacks();

        Entity link = new Entity(new Transform(), OBJLoader.loadTexturedOBJ("cube.obj", TextureLoader.getTexture("grass_color.png")));

        terrain = new Terrain();
        light = new Light(new Vector3f(10, 30, 10), new Vector3f(1, 1f, 1));
        SkyboxModel skyboxModel = modelCreator.createSkyboxModel(new String[]{"right", "left", "top", "bottom", "back", "front"});

        while (!glfwWindowShouldClose(DisplayManager.window)) {
            renderer.beginFrame(fb);
            camera.update();
            //GameEngineAPI.engineUpdate();

            renderer.Render(skyboxModel);
            renderer.Render(link);
            renderer.Render(terrain.getModel());


            renderer.endScene(fb);
        }
    }
}
