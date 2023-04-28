package engine;

import editor.ConsoleWindow;
import engine.display.DisplayManager;
import engine.ecs.Entity;
import engine.ecs.Light;
import engine.ecs.component.ModelRenderer;
import engine.ecs.component.Terrain;
import engine.ecs.component.Water;
import engine.input.Keyboard;
import engine.input.Mouse;
import engine.model.*;
import engine.postprocessing.PostProcessing;
import engine.scene.Scene;
import engine.shader.Framebuffer;
import engine.texture.Texture;
import engine.texture.TextureLoader;
import engine.util.Time;
import org.joml.Vector3f;
import scripting.LuaScriptingManager;

import java.security.Key;
import java.util.Random;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;


public class GameEngine {
    public static GameEngine instance;
    public static Texture grass;
    public static Texture waterDUDV;
    public static Texture waterNormalMap;
    public static Model cube;
    public static Framebuffer reflectionBuffer;
    public static Framebuffer refractionBuffer;
    public static Framebuffer frameBuffer;
    public static float waterMovement = 0f;
    public ModelCreator modelCreator;
    public Renderer renderer;
    public Camera camera;
    public Light light;
    public Scene loadedScene;
    public SkyboxModel skybox;
    public final int waterHeight = 0;
    public int clipHeight = 0;
    public float clipDirection = 1;

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
        renderer.cleanUp();
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void loop() {
        grass = TextureLoader.getTexture("grass_color.png");
        waterDUDV = TextureLoader.getTexture("waterDUDV.png");
        waterNormalMap = TextureLoader.getTexture("waterNormalMap.png");
        modelCreator = new ModelCreator();
        cube = OBJLoader.loadTexturedOBJ("stall.obj", TextureLoader.getTexture("stallTexture.png"));
        loadedScene = new Scene("testScene");
        skybox = modelCreator.createSkyboxModel(new String[]{"right", "left", "top", "bottom", "back", "front"});
        renderer = new Renderer();
        renderer.prepare();


        PostProcessing.init();
        ConsoleWindow.init();


        camera = new Camera(new Vector3f(30, 10, 30), new Vector3f(0, 25, 0));

        DisplayManager.setCallbacks();


        String tree = "Birch";
        MultiTexturedModel treeModel = (MultiTexturedModel) OBJLoader.loadTexturedOBJ("OBJ/" + tree + "Tree_1.obj", null);
        treeModel.setTexture("" + tree + "Tree_Bark", TextureLoader.getTexture("Textures/" + tree + "Tree_Bark.png"));
        treeModel.setTexture("" + tree + "Tree_Leaves", TextureLoader.getTexture("Textures/" + tree + "Tree_Leaves.png"));

        Terrain terrain = new Terrain();
        terrain.amplitude = 3;
        terrain.scale = 2;
        terrain.textureScale = 20;
        loadedScene.addEntity(new Entity("Terrain").addComponent(terrain));
        loadedScene.addEntity(new Entity("Water").addComponent(new Water()));
        loadedScene.addEntity(new Entity("Tree").addComponent(new ModelRenderer().setModel(treeModel)));

        light = new Light(new Vector3f(10, 30, 10), new Vector3f(255 / 255f, 241  / 255f, 204 / 255f));

        reflectionBuffer = new Framebuffer(DisplayManager.getWidth(), DisplayManager.getHeight(), Framebuffer.DEPTH_TEXTURE);
        refractionBuffer = new Framebuffer(DisplayManager.getWidth(), DisplayManager.getHeight(), Framebuffer.DEPTH_TEXTURE);
        frameBuffer = new Framebuffer(DisplayManager.getWidth(), DisplayManager.getHeight(), Framebuffer.DEPTH_TEXTURE);

        glEnable(GL_CLIP_PLANE0);
        while (!glfwWindowShouldClose(DisplayManager.window)) {
            renderer.beginFrame();
            Time.updateTime();
            Mouse.update();
            camera.update();
            waterMovement += 0.0005f;
            LuaScriptingManager.Update();

            if(Mouse.isMousePressed(0))
                LuaScriptingManager.LeftClick();

            refractionBuffer.bind();
            clipDirection = -1;
            clipHeight = waterHeight;
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
            renderer.renderSkybox(skybox);
            for (Entity entity : loadedScene.getEntities()) {
                if(entity.getComponent(Water.class) != null)
                    continue;
                renderer.render(entity);
            }
            refractionBuffer.unbind();

            reflectionBuffer.bind();
            clipDirection = 1;
            clipHeight = -waterHeight;
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
            camera.waterInvert(clipHeight);
            renderer.renderSkybox(skybox);
            for (Entity entity : loadedScene.getEntities()) {
                if(entity.getComponent(Water.class) != null)
                    continue;
                renderer.render(entity);
            }
            camera.waterInvert(clipHeight);
            reflectionBuffer.unbind();

            frameBuffer.bind();
            clipDirection = -1;
            clipHeight = 10000;
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            renderer.renderSkybox(skybox);
            for (Entity entity : loadedScene.getEntities())
                renderer.render(entity);
            frameBuffer.unbind();

            renderer.endScene(frameBuffer);
        }
    }
}
