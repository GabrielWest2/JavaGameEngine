package engine;

import editor.ConsoleWindow;
import engine.ecs.Entity;
import engine.ecs.Light;
import engine.ecs.component.Transform;
import engine.model.ModelCreator;
import engine.postprocessing.PostProcessing;
import engine.shader.Framebuffer;
import engine.shader.StaticShader;
import engine.shader.TerrainShader;
import engine.shader.display.DisplayManager;
import engine.terrain.Terrain;
import engine.texture.TextureLoader;
import engine.util.MatrixBuilder;
import org.joml.Vector3f;
import org.lwjgl.Version;
import scripting.GameEngineAPI;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;


public class GameEngine {
    public static ModelCreator modelCreator;
    public static TextureLoader textureLoader;
    public static Renderer renderer;
    public static SkyboxRenderer skyboxRenderer;
    public static StaticShader shader;
    public static TerrainShader terrainShader;
    public static Camera camera;
    public static Framebuffer fb;

    public static ModelCreator getModelCreator() {
        return modelCreator;
    }

    public static TextureLoader getTextureLoader() {
        return textureLoader;
    }

    public static Renderer getRenderer() {
        return renderer;
    }

    public static StaticShader getShader() {
        return shader;
    }

    public static void main(String[] args) {
        new GameEngine().run();
    }

    public void run() {

        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        DisplayManager.initOpenGL(this);


        loop();


        glfwFreeCallbacks(DisplayManager.window);
        glfwDestroyWindow(DisplayManager.window);
        modelCreator.cleanUp();
        TextureLoader.cleanUp();
        shader.cleanUp();
        terrainShader.cleanUp();
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void loop() {
        modelCreator = new ModelCreator();

        renderer = new Renderer();
        renderer.Prepare();
        skyboxRenderer = new SkyboxRenderer(MatrixBuilder.createProjectionMatrix());

        shader = new StaticShader();
        terrainShader = new TerrainShader();

        renderer.setShader(shader);
        renderer.setShader(terrainShader);
        PostProcessing.init();
        ConsoleWindow.init();

        camera = new Camera(new Vector3f(30, 10, 30), new Vector3f(0, 25, 0));
        fb = new Framebuffer(DisplayManager.getWidth(), DisplayManager.getHeight(), Framebuffer.DEPTH_TEXTURE);
        DisplayManager.setCallbacks();
        //Physics.setUpPhysics();


        Terrain terrain = new Terrain();
        Entity entity = new Entity(new Transform(), terrain.getModel());

        Light sun = new Light(new Vector3f(10, 30, 10), new Vector3f(1, 1f, 1));

        while (!glfwWindowShouldClose(DisplayManager.window)) {
            renderer.beginFrame(fb);
            camera.update();
            if (GameEngineAPI.getInstance() != null)
                GameEngineAPI.getInstance().Update();


            skyboxRenderer.render(MatrixBuilder.createStationaryViewMatrix(camera));

            //shader.start();
            //shader.loadViewMatrix(MatrixBuilder.createViewMatrix(camera));
            //shader.loadLight(light);
            //shader.setMaterial(20, 0.5f);
            //renderer.Render(link, shader);
            //Physics.render(shader);
            //shader.stop();
            //Physics.logic();
            //Physics.input();


            terrainShader.start();
            terrainShader.loadViewMatrix(MatrixBuilder.createViewMatrix(camera));
            renderer.Render(entity, terrainShader);
            terrainShader.stop();


            renderer.endScene(fb);
        }
    }
}
