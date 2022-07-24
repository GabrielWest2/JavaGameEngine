package engine;

import editor.ConsoleWindow;
import engine.display.DisplayManager;
import engine.ecs.Entity;
import engine.ecs.Light;
import engine.ecs.component.Terrain;
import engine.model.Model;
import engine.model.ModelCreator;
import engine.model.OBJLoader;
import engine.model.SkyboxModel;
import engine.postprocessing.PostProcessing;
import engine.scene.Scene;
import engine.shader.Framebuffer;
import engine.texture.Texture;
import engine.texture.TextureLoader;
import org.joml.Vector3f;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;


public class GameEngine {
    public static GameEngine instance;
    public static Texture grass;
    public static Model cube;
    public ModelCreator modelCreator;
    public Renderer renderer;
    public Camera camera;
    public Framebuffer fb;
    public Light light;
    public Terrain terrain;
    public Entity terrainEntity;
    public Scene loadedScene;
    public SkyboxModel skybox;

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
        modelCreator.cleanUp();
        TextureLoader.cleanUp();

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void loop() {
        grass = TextureLoader.getTexture("grass_color.png");
        modelCreator = new ModelCreator();
        cube = OBJLoader.loadTexturedOBJ("cube.obj", grass);
        loadedScene = new Scene("testScene");
        skybox = modelCreator.createSkyboxModel(new String[]{"right", "left", "top", "bottom", "back", "front"});
        renderer = new Renderer();
        renderer.Prepare();

        PostProcessing.init();
        ConsoleWindow.init();


        camera = new Camera(new Vector3f(30, 10, 30), new Vector3f(0, 25, 0));
        fb = new Framebuffer(DisplayManager.getWidth(), DisplayManager.getHeight(), Framebuffer.DEPTH_TEXTURE);
        DisplayManager.setCallbacks();

        loadedScene.addEntity(new Entity("Terrain").addComponent(new Terrain()));


        light = new Light(new Vector3f(10, 30, 10), new Vector3f(1, 1f, 1));

        while (!glfwWindowShouldClose(DisplayManager.window)) {
            renderer.beginFrame(fb);

            renderer.Render(skybox);

            for (Entity entity : loadedScene.getEntities())
                renderer.Render(entity);


            renderer.endScene(fb);
        }
    }
}
