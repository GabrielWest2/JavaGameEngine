package engine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import editor.ConsoleWindow;
import editor.ExplorerWindow;
import engine.audio.AudioManager;
import engine.display.DisplayManager;
import engine.ecs.Component;
import engine.ecs.Entity;
import engine.ecs.Light;
import engine.ecs.component.ObjRenderer;
import engine.ecs.component.Transform;
import engine.hud.HudManager;
import engine.input.Keyboard;
import engine.input.Mouse;
import engine.model.ModelCreator;
import engine.model.SkyboxModel;
import engine.physics.Physics;
import engine.postprocessing.PostProcessing;
import engine.scene.Scene;
import engine.serialization.ComponentDeserializer;
import engine.shader.Framebuffer;
import engine.texture.Texture;
import engine.texture.TextureLoader;
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
    public static Texture waterDUDV;
    public static Texture waterNormalMap;
    public static Framebuffer reflectionBuffer;
    public static Framebuffer refractionBuffer;
    public static Framebuffer frameBuffer;
    public static Framebuffer mousePickingBuffer;
    public static float waterMovement = 0f;
    public static float grassMovement = 0f;

    public static Gson gson;
    //public static VegetationModel grassModel;
    public Camera camera;
    public Light light;
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


    private boolean wireframe = false;

    Transform t = new Transform();
    private void loop() {
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .create();

        grass = TextureLoader.loadTexture("terrain/Texture_Grass_Diffuse.png");
        waterDUDV = TextureLoader.loadTexture("water/waterDUDV.png");
        waterNormalMap = TextureLoader.loadTexture("water/waterNormalMap.png");
        loadedScene = new Scene("testScene");
        skybox = ModelCreator.createSkyboxModel(new String[]{"right", "left", "top", "bottom", "back", "front"});
        Renderer.init();


        PostProcessing.init();
        ConsoleWindow.init();
        Physics.init();
        TerrainManager.init();
        WaterManger.init();
        AudioManager.init();

        /*
        int sound = -1;
        try{
            sound = AudioManager.loadSound("res/audio/rito.wav");
        } catch (UnsupportedAudioFileException | IOException e) {
            throw new RuntimeException(e);
        }

        AudioSource source = new AudioSource();

         */
        camera = new Camera(new Vector3f(30, 10, 30), new Vector3f(0, 25, 0));

        DisplayManager.setCallbacks();



        light = new Light(new Vector3f(10, 3000, 10), new Vector3f(255 / 255f, 241  / 255f, 204 / 255f));

        reflectionBuffer = new Framebuffer(DisplayManager.getWidth(), DisplayManager.getHeight(), Framebuffer.DEPTH_TEXTURE);
        refractionBuffer = new Framebuffer(DisplayManager.getWidth(), DisplayManager.getHeight(), Framebuffer.DEPTH_TEXTURE);
        frameBuffer = new Framebuffer(DisplayManager.getWidth(), DisplayManager.getHeight(), Framebuffer.DEPTH_TEXTURE);
        mousePickingBuffer = new Framebuffer(DisplayManager.getWidth(), DisplayManager.getHeight(), Framebuffer.DEPTH_TEXTURE);


        for(int x = 0; x < 16; x++){
            for(int z = 0; z < 16; z++){
                Entity e = new Entity("Tile " + x+"-"+z);
                e.getTransform().setPosition(new Vector3f(x*2, 0, z*2));
                ObjRenderer obj = new ObjRenderer()
                        .setPaths("models/grass2.obj", "models/ColorPaletteBLUE.png");
                e.addComponent(obj);
                loadedScene.addEntity(e);
            }
        }

       
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

            /*
            if(Keyboard.isKeyPressedThisFrame(GLFW_KEY_F)){
               // source.play(sound);
            }
            */

            if(Keyboard.isKeyPressedThisFrame(GLFW_KEY_Z)){
                wireframe = !wireframe;
            }
            if(Mouse.isMousePressed(0))
                LuaScriptingManager.LeftClick();

            mousePickingBuffer.bind();
            renderToMousePickingBuffer();
            mousePickingBuffer.unbind();


            refractionBuffer.bind();
            clipDirection = -1;
            clipHeight = WaterManger.waterHeight+0.01f;
            renderScene();
            refractionBuffer.unbind();

            reflectionBuffer.bind();
            clipDirection = 1;
            clipHeight = -WaterManger.waterHeight;
            camera.waterInvert(clipHeight);
            renderScene();
            camera.waterInvert(clipHeight);
            reflectionBuffer.unbind();

            frameBuffer.bind();
            clipDirection = -1;
            clipHeight = 10000;
            renderScene();
            WaterManger.render();
            HudManager.renderHud();
            Physics.render();
            frameBuffer.unbind();

            Renderer.endScene(frameBuffer, mousePickingBuffer, camera, ExplorerWindow.selectedEntity);
        }
    }

    private void renderScene(){
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
        glDisable(GL_CLIP_PLANE0);
        glDisable(GL_DEPTH_TEST);
        Renderer.renderSkybox(skybox);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CLIP_PLANE0);
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, wireframe ? GL11.GL_LINE : GL_FILL);
        //TerrainManager.renderChunks();
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL_FILL);
        for (Entity entity : loadedScene.getEntities()) {
            Renderer.render(entity);
        }
        //Renderer.renderTextured(Primitives.plane, t);
    }

    private void renderToMousePickingBuffer(){
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
        glDisable(GL_CLIP_PLANE0);
        glEnable(GL_DEPTH_TEST);
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL_FILL);
        int i = 0;
        for (Entity entity : loadedScene.getEntities()) {
            Renderer.renderPicking(entity, i+1);
            i++;
        }
        glEnable(GL_CLIP_PLANE0);
    }
}
