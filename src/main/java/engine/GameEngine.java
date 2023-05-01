package engine;

import editor.ConsoleWindow;
import engine.audio.AudioManager;
import engine.audio.AudioSource;
import engine.display.DisplayManager;
import engine.ecs.Entity;
import engine.ecs.Light;
import engine.ecs.component.ModelRenderer;
import engine.ecs.component.Terrain;
import engine.ecs.component.Transform;
import engine.ecs.component.Water;
import engine.input.Keyboard;
import engine.input.Mouse;
import engine.model.*;
import engine.physics.Physics;
import engine.postprocessing.PostProcessing;
import engine.scene.Scene;
import engine.shader.Framebuffer;
import engine.texture.Texture;
import engine.texture.TextureLoader;
import engine.util.Time;
import org.joml.Math;
import org.joml.Vector3f;
import scripting.LuaScriptingManager;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    public static float grassMovement = 0f;
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
        AudioManager.cleanUp();
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }



    private void loop() {
        grass = TextureLoader.getTexture("Terrain/Texture_Grass_Diffuse_Light.png");
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
        Physics.setUpPhysics();
        AudioManager.init();

        int sound = -1;
        try{
            sound = AudioManager.loadSound("res/audio/rito.wav");
        } catch (UnsupportedAudioFileException | IOException e) {
            throw new RuntimeException(e);
        }

        AudioSource source = new AudioSource();
        camera = new Camera(new Vector3f(30, 10, 30), new Vector3f(0, 25, 0));

        DisplayManager.setCallbacks();


        String tree = "Birch";
        VegetationModel grassModel =  OBJLoader.loadVegetationTexturedOBJ("grass.obj", TextureLoader.getTexture("Terrain/Brush_Grass_01.png"));
        MultiTexturedModel flowerModel = (MultiTexturedModel) OBJLoader.loadTexturedOBJ("flowers.obj", null);
        flowerModel.setTexture("Plants", TextureLoader.getTexture("flowers.png"));

        /*MultiTexturedModel treeModel1 = (MultiTexturedModel) OBJLoader.loadTexturedOBJ("OBJ/" + tree + "Tree_1.obj", null);
        treeModel1.setTexture("" + tree + "Tree_Bark", TextureLoader.getTexture("Textures/" + tree + "Tree_Bark.png"));
        treeModel1.setTexture("" + tree + "Tree_Leaves", TextureLoader.getTexture("Textures/" + tree + "Tree_Leaves" +(Math.round(Math.random()*2)+1) + ".png"));
        */

        MultiTexturedModel treeModel1 = (MultiTexturedModel) OBJLoader.loadTexturedOBJ("tree.obj", null);
        treeModel1.setTexture("trunk_Mat.001", TextureLoader.getTexture("Textures/NormalTree_Bark.png"));
        treeModel1.setTexture("leafes_Mat.001", TextureLoader.getTexture("leaf02.png"));


        Terrain terrain = new Terrain();
        terrain.amplitude = 20;
        terrain.scale = 2;
        terrain.textureScale = 20;
        loadedScene.addEntity(new Entity("Terrain").addComponent(terrain));
        terrain.entity.getComponent(ModelRenderer.class).reflectivity = 0.1f;
        terrain.entity.getComponent(ModelRenderer.class).shineDamper = 50.0f;
        Entity water = new Entity("Water");
        water.addComponent(new Water());
        water.getTransform().setPosition(new Vector3f(-500, 0, -500));
        loadedScene.addEntity(water);
        List<Transform> grassPositions = new ArrayList<>();
        List<Transform> flowerPositions = new ArrayList<>();
        for(float x = 0; x < terrain.terrainWidth; x += 0.5f){
            for(float z = 0; z < terrain.terrainWidth; z += 0.5f) {
                float height = terrain.calculateHeight(x, z);
                if(height <= 0)
                    continue;
                boolean flowers = Math.random() > 0.95;

                float newX = (float) (x + ((Math.random()-0.5f) * 0.75f));
                float newZ = (float) (z + ((Math.random()-0.5f) * 0.75f));
                float scale = flowers ? 0.6f : (float) (Math.random() * 0.1f) + 0.3f;

                if(flowers)
                    flowerPositions.add(new Transform(new Vector3f(newX, height, newZ), terrain.calculateObjectRotation(newX, newZ), new Vector3f(scale)));
                else
                    grassPositions.add(new Transform(new Vector3f(newX, height, newZ), terrain.calculateObjectRotation(newX, newZ), new Vector3f(scale)));
            }
        }
        for(int i = 0; i < 20; i ++){
            boolean foundSpot = false;
            float height = 1.0f;
            float x = 0.0f, y = 0.0f;
            while(!foundSpot){
                x = (float) (Math.random() * terrain.terrainWidth);
                y = (float) (Math.random() * terrain.terrainWidth);
                if(terrain.calculateHeight(x, y) > 0){
                    foundSpot = true;
                    height = terrain.calculateHeight(x, y);
                }
            }
            Entity e = new Entity("Tree" + x+":"+y).addComponent(new ModelRenderer().setModel(treeModel1));
            e.getComponent(ModelRenderer.class).cullBack = false;
            e.getTransform().setScale(new Vector3f(1));
            e.getTransform().getPosition().x = x;
            e.getTransform().getPosition().y = height;
            e.getTransform().getPosition().z = y;
            e.getTransform().setRotation(terrain.calculateObjectRotation(x, y).mul(0.1f));
            loadedScene.addEntity(e);
        }

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
            Physics.logic();
            Physics.render();
            LuaScriptingManager.Update();
            waterMovement += 0.0005f;
            grassMovement += 0.03f;
            waterMovement %=1;

            if(Keyboard.isKeyPressedThisFrame(GLFW_KEY_F)){
                source.play(sound);
            }
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
            renderer.renderTerrainDetails( grassPositions, grassModel);
            renderer.renderTerrainDetails(flowerPositions, flowerModel);
            frameBuffer.unbind();

            renderer.endScene(frameBuffer);
        }
    }
}
