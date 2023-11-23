package engine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import editor.ConsoleWindow;
import editor.ExplorerWindow;
import engine.audio.AudioManager;
import engine.ecs.Component;
import engine.ecs.Entity;
import engine.ecs.component.ObjRenderer;
import engine.input.Mouse;
import engine.physics.Physics;
import engine.postprocessing.PostProcessing;
import engine.rendering.Camera;
import engine.rendering.DisplayManager;
import engine.rendering.Renderer;
import engine.rendering.model.ModelCreator;
import engine.rendering.model.SkyboxModel;
import engine.rendering.texture.Texture;
import engine.rendering.texture.TextureLoader;
import engine.scene.Scene;
import engine.serialization.ComponentDeserializer;
import engine.util.Time;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import scripting.LuaScriptingManager;

import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;


public class GameEngine {

    public static GameEngine instance;

    public static Texture grass;

    public static float waterMovement = 0f;

    public static float grassMovement = 0f;

    public static Gson gson;

    public Camera camera;

    public Scene loadedScene;

    public SkyboxModel skybox;

    public float clipHeight = 0;

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
        ModelCreator.cleanUp();
        TextureLoader.cleanUp();
        Renderer.cleanUp();
        AudioManager.cleanUp();
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }


    private void loop() {
        gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Component.class, new ComponentDeserializer())
            .create();

        grass = TextureLoader.loadTexture("terrain/Texture_Grass_Diffuse.png");

        loadedScene = new Scene("testScene");
        skybox = ModelCreator.createSkyboxModel(new String[]{"right", "left", "top", "bottom", "back", "front"});
        Renderer.init();


        PostProcessing.init();
        ConsoleWindow.init();
        Physics.init();
        TerrainManager.init();
        WaterManger.init();
        AudioManager.init();

        camera = new Camera(new Vector3f(30, 10, 30), new Vector3f(0, 25, 0));

        DisplayManager.setCallbacks();

        Renderer.initFramebuffers();
        Renderer.initWater();

        while (!glfwWindowShouldClose(DisplayManager.window)) {
            Renderer.beginFrame();
            Time.updateTime();
            Mouse.update();
            camera.update();
            Physics.logic();
            Physics.render();
            LuaScriptingManager.Update();
            waterMovement += 0.0005f;
            grassMovement += 0.03f;
            waterMovement %=1;



            if(Mouse.isMousePressed(0))
                LuaScriptingManager.LeftClick();

            Renderer.mousePickingBuffer.bind();
            renderToMousePickingBuffer();
            Renderer.mousePickingBuffer.unbind();


            Renderer.refractionBuffer.bind();
            clipDirection = -1;
            clipHeight = WaterManger.waterHeight+0.01f;
            renderScene();
            Renderer.refractionBuffer.unbind();

            Renderer.reflectionBuffer.bind();
            clipDirection = 1;
            clipHeight = -WaterManger.waterHeight;
            camera.waterInvert(clipHeight);
            renderScene();
            camera.waterInvert(clipHeight);
            Renderer.reflectionBuffer.unbind();

            Renderer.frameBuffer.bind();
            clipDirection = -1;
            clipHeight = 10000;
            renderScene();
            WaterManger.render();
            Physics.render();
            Renderer.frameBuffer.unbind();

            Renderer.endScene(camera, ExplorerWindow.selectedEntity);
        }
    }

    private void renderScene(){
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
        glDisable(GL_CLIP_PLANE0);
        glDisable(GL_DEPTH_TEST);
        Renderer.renderSkybox(skybox);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CLIP_PLANE0);
        //TerrainManager.renderChunks();
        for (Entity entity : loadedScene.getEntities()) {
            var obj = entity.getComponent(ObjRenderer.class);

            if(obj == null || obj.getModel() == null) return;

            Renderer.renderModel(obj.getModel(), entity.getTransform(), obj.cullBack);
        }
    }

    private void renderToMousePickingBuffer(){
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
        glDisable(GL_CLIP_PLANE0);
        glEnable(GL_DEPTH_TEST);
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL_FILL);
        int i = 0;
        for (Entity entity : loadedScene.getEntities()) {
            var obj = entity.getComponent(ObjRenderer.class);

            if(obj == null || obj.getModel() == null) return;

            Renderer.renderPicking(obj.getModel(), entity.getTransform(), false, i + 1);
            i++;
        }
        glEnable(GL_CLIP_PLANE0);
    }
}
