package engine.rendering;

import editor.*;
import engine.GameEngine;
import engine.TerrainManager;
import engine.WaterManger;
import engine.ecs.Entity;
import engine.ecs.component.ObjRenderer;
import engine.ecs.component.Transform;
import engine.postprocessing.PostProcessing;
import engine.rendering.model.*;
import engine.rendering.texture.Texture;
import engine.rendering.texture.TextureLoader;
import engine.scene.Scene;
import engine.scene.SceneManager;
import engine.shader.*;
import engine.util.MatrixBuilder;
import engine.util.Time;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class Renderer {

    private static WaterShader waterShader;

    private static StandardShader defaultShader;

    private static SkyboxShader skyboxShader;

    private static TerrainShader terrainShader;

    private static GridShader gridShader;

    private static MousePickingShader mousePickingShader;

    public static Framebuffer reflectionBuffer;

    public static Framebuffer refractionBuffer;

    public static Framebuffer frameBuffer;

    public static Framebuffer mousePickingBuffer;

    public static Texture waterDUDV;

    public static Texture waterNormalMap;

    private static SkyboxModel skybox;

    public static final int MAX_POINT_LIGHTS = 5;

    public static final int MAX_SPOT_LIGHTS = 5;

    public static float clipHeight = 0;

    public static float clipDirection = 1;

    public static float waterMovement = 0f;


    public static void initSkybox(){
        skybox = ModelCreator.createSkyboxModel(new String[]{"right", "left", "top", "bottom", "back", "front"});
    }


    public static void updateProjection(int width, int height) {
        Matrix4f mat = MatrixBuilder.createProjectionMatrix(width, height);
        defaultShader.start();
        defaultShader.loadProjectionMatrix(mat);
        defaultShader.stop();

        waterShader.start();
        waterShader.loadProjectionMatrix(mat);
        waterShader.stop();

        skyboxShader.start();
        skyboxShader.loadProjectionMatrix(mat);
        skyboxShader.stop();

        terrainShader.start();
        terrainShader.loadProjectionMatrix(mat);
        terrainShader.stop();

        gridShader.start();
        gridShader.loadProjectionMatrix(mat);
        gridShader.stop();

        mousePickingShader.start();
        mousePickingShader.loadProjectionMatrix(mat);
        mousePickingShader.stop();
    }

    public static void init() {
        glEnable(GL_MULTISAMPLE);
        // Set the clear color
        glClearColor(0.5f, 0.5f, 1.0f, 1.0f);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_STENCIL_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        defaultShader = new StandardShader();
        skyboxShader = new SkyboxShader();
        terrainShader = new TerrainShader();
        gridShader = new GridShader();
        waterShader = new WaterShader();
        mousePickingShader = new MousePickingShader();
        updateProjection(DisplayManager.getWidth(), DisplayManager.getHeight());
        Renderer.initSkybox();
        Renderer.initFramebuffers();
        Renderer.initWater();
        DisplayManager.setCallbacks();
    }


    public static void renderGame(){
        Renderer.mousePickingBuffer.bind();
        renderToMousePickingBuffer(SceneManager.loadedScene);
        Renderer.mousePickingBuffer.unbind();


        Renderer.refractionBuffer.bind();
        clipDirection = -1;
        clipHeight = WaterManger.waterHeight+0.01f;
        renderScene(SceneManager.loadedScene, skybox);
        Renderer.refractionBuffer.unbind();

        Renderer.reflectionBuffer.bind();
        clipDirection = 1;
        clipHeight = -WaterManger.waterHeight;
        SceneManager.loadedScene.camera.waterInvert(clipHeight);
        renderScene(SceneManager.loadedScene, skybox);
        SceneManager.loadedScene.camera.waterInvert(clipHeight);
        Renderer.reflectionBuffer.unbind();

        Renderer.frameBuffer.bind();
        clipDirection = -1;
        clipHeight = 10000;
        renderScene(SceneManager.loadedScene, skybox);
        WaterManger.render();
        Renderer.frameBuffer.unbind();
    }

    public static void renderScene(Scene scene, SkyboxModel skybox){
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
        glDisable(GL_CLIP_PLANE0);
        glDisable(GL_DEPTH_TEST);
        Renderer.renderSkybox(skybox);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CLIP_PLANE0);
        TerrainManager.renderChunks();
        for (Entity entity : scene.getEntities()) {
            var obj = entity.getComponent(ObjRenderer.class);

            if(obj == null || obj.getModel() == null) return;

            Renderer.renderModel(obj.getModel(), entity.getTransform(), obj.cullBack);
        }
    }

    public static void renderToMousePickingBuffer(Scene scene){
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
        glDisable(GL_CLIP_PLANE0);
        glEnable(GL_DEPTH_TEST);
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL_FILL);
        int i = 0;
        for (Entity entity : scene.getEntities()) {
            var obj = entity.getComponent(ObjRenderer.class);

            if(obj == null || obj.getModel() == null) return;

            Renderer.renderPicking(obj.getModel(), entity.getTransform(), false, i + 1);
            i++;
        }
        glEnable(GL_CLIP_PLANE0);
    }


    private static void renderModel(Model m, Transform transform, boolean cullBacks) {
        for(Mesh mesh : m.getMeshes()){
            renderTextured(mesh, transform, cullBacks);
        }
    }


    private static void renderPicking(Model m, Transform transform, boolean cullBacks, int entityId) {
        for(Mesh mesh : m.getMeshes()){
            renderMousePicking(mesh, transform, cullBacks, entityId);
        }
    }


    public static void renderWater(WaterModel model, Vector3f position, float damper, float reflect) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        waterShader.start();
        waterShader.connectTextures();
        waterShader.setMaterial(damper, reflect);
        waterShader.loadWaterMovement(waterMovement);
        waterShader.loadCameraPosition(
                SceneManager.loadedScene.camera.getPosition());
        waterShader.loadTransformationMatrix(
                MatrixBuilder.createTransformationMatrix(
                        position,
                        new Vector3f(0, 0, 0),
                        new Vector3f(1, 1, 1))
        );
        waterShader.loadViewMatrix(
                SceneManager.loadedScene.camera.getViewMatrix());
        glBindVertexArray(model.getVaoID());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, 
                reflectionBuffer.getColorTexture());
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, 
                refractionBuffer.getColorTexture());
        glActiveTexture(GL_TEXTURE2);
        glBindTexture(GL_TEXTURE_2D, 
                waterDUDV.textureID());
        glActiveTexture(GL_TEXTURE3);
        glBindTexture(GL_TEXTURE_2D, 
                waterNormalMap.textureID());
        glActiveTexture(GL_TEXTURE4);
        glBindTexture(GL_TEXTURE_2D, 
                refractionBuffer.getDepthTexture());

        glDrawElements(
                GL_TRIANGLES,
                model.getVertexCount(),
                GL_UNSIGNED_INT,
                0
        );

        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
        waterShader.stop();

        glDisable(GL_BLEND);
    }



    private static void renderMousePicking(Mesh model, Transform transform, boolean cull, int entityId) {
        if(cull)
            glEnable(GL_CULL_FACE);
        else
            glDisable(GL_CULL_FACE);
        mousePickingShader.start();

        float red = (float) (entityId & 0xff) / 0xff;
        float green = (float) ((entityId >> 8) & 0xff) / 0xff;
        float blue = (float) ((entityId >> 16) & 0xff) / 0xff;
        // float alpha = (float) ((entityId >> 24) & 0xff) / 0xff;

        mousePickingShader.loadColor(new Vector4f(red, green, blue, 1.0f));
        mousePickingShader.loadTransformationMatrix(
                MatrixBuilder.createTransformationMatrix(
                        transform.getPosition(),
                        transform.getRotation(),
                        transform.getScale()
                )
        );
        mousePickingShader.loadViewMatrix(
                SceneManager.loadedScene.camera.getViewMatrix());
        glBindVertexArray(model.getVaoID());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        glDrawElements(GL_TRIANGLES, model.getVertexCount(), GL_UNSIGNED_INT, 0);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
        mousePickingShader.stop();
        glEnable(GL_CULL_FACE);
    }


    public static void renderTextured(Mesh model, Transform transform, boolean cullBack) {
        if(cullBack)
            glEnable(GL_CULL_FACE);
        else
            glDisable(GL_CULL_FACE);
        defaultShader.start();
        Matrix4f view = SceneManager.loadedScene.camera.getViewMatrix();
        defaultShader.loadMaterial(model.getMaterial());
        defaultShader.loadLights(SceneManager.loadedScene.getLights(), view);
        defaultShader.loadTransformationMatrix(
                MatrixBuilder.createTransformationMatrix(
                        transform.getPosition(),
                        transform.getRotation(),
                        transform.getScale()
                )
        );
        defaultShader.loadViewMatrix(view);
        defaultShader.setClipPlane(
                new Vector4f(
                        0,
                        Renderer.clipDirection,
                        0,
                        Renderer.clipHeight
                )
        );

        glBindVertexArray(model.getVaoID());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glActiveTexture(GL_TEXTURE0);
        if(model.getMaterial().getLoadedTexture() != null) {
            glBindTexture(GL_TEXTURE_2D, model.getMaterial().getLoadedTexture().textureID());
        }else {
            glBindTexture(GL_TEXTURE_2D, Primitives.white.textureID());
        }
        glDrawElements(GL_TRIANGLES, model.getVertexCount(), GL_UNSIGNED_INT, 0);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
        defaultShader.stop();
        glEnable(GL_CULL_FACE);
    }

    public static void renderTerrain(TerrainModel model) {
        terrainShader.start();
        Matrix4f view = SceneManager.loadedScene.camera.getViewMatrix();
        terrainShader.loadViewMatrix(view);
        terrainShader.loadLights(SceneManager.loadedScene.getLights(), view);

        terrainShader.setTextureScales(
                TerrainManager.textureScale1,
                TerrainManager.textureScale2,
                TerrainManager.textureScale3,
                TerrainManager.textureScale4);

        terrainShader.connectTextures();
        terrainShader.loadTransformationMatrix(model.getTransformationMatrix());

        terrainShader.setClipPlane(new Vector4f(0,
                Renderer.clipDirection,
                0, Renderer.clipHeight));

        glBindVertexArray(model.getVaoID());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, model.getSplat().textureID());

        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, model.getT1().textureID());
        glActiveTexture(GL_TEXTURE2);
        glBindTexture(GL_TEXTURE_2D, model.getT2().textureID());
        glActiveTexture(GL_TEXTURE3);
        glBindTexture(GL_TEXTURE_2D, model.getT3().textureID());
        glActiveTexture(GL_TEXTURE4);
        glBindTexture(GL_TEXTURE_2D, model.getT4().textureID());


        glDrawElements(GL_TRIANGLES, model.getVertexCount(),
                GL_UNSIGNED_INT, 0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
        terrainShader.stop();
    }

    public static void renderSkybox(SkyboxModel model) {
        skyboxShader.start();
        skyboxShader.loadViewMatrix(
                MatrixBuilder.createStationaryViewMatrix(
                        SceneManager.loadedScene.camera
                )
        );
        glBindVertexArray(model.getVaoID());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_CUBE_MAP, model.getCubemapTexture());
        glDrawArrays(GL_TRIANGLES, 0, model.getVertexCount());
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);
        skyboxShader.stop();
        skyboxShader.stop();
    }

    public static void beginFrame() {
        DisplayManager.newImguiFrame();
        if(GameEngine.editorMode) {
            DisplayManager.createDockspace();
        }
        glEnable(GL_CULL_FACE);
        glEnable(GL_CLIP_PLANE0);
        glEnable(GL_MULTISAMPLE);
        // Set the clear color
        glClearColor(0.5f, 0.5f, 1.0f, 1.0f);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_STENCIL_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        waterMovement += Time.getDeltaTime() / 30f;
        waterMovement %=1;
    }

    public static void endScene(Camera camera, Entity selected) {

        PostProcessing.doPostProcessing(
                frameBuffer.getColorTexture(),
                frameBuffer.getDepthTexture()
        );

        if(GameEngine.editorMode) {
            GameViewportWindow.render(
                    PostProcessing.finalBuffer,
                    mousePickingBuffer,
                    camera,
                    selected
            );

            LightingWindow.render(SceneManager.loadedScene.getLights());
            WindowMenubar.render();
            ConsoleWindow.render();
            ExplorerWindow.render();
            InspectorWindow.render();

        }
            DisplayManager.endImguiFrame();

        glfwSwapBuffers(DisplayManager.window); // swap the color buffers
        glfwPollEvents();
    }

    public static void cleanUp() {
        defaultShader.cleanUp();
        skyboxShader.cleanUp();
        terrainShader.cleanUp();
        gridShader.cleanUp();
    }

    public static void initFramebuffers() {
        reflectionBuffer = new Framebuffer(
                DisplayManager.getWidth(),
                DisplayManager.getHeight(),
                Framebuffer.DEPTH_TEXTURE);
        refractionBuffer = new Framebuffer(
                DisplayManager.getWidth(),
                DisplayManager.getHeight(),
                Framebuffer.DEPTH_TEXTURE);
        frameBuffer = new Framebuffer(
                DisplayManager.getWidth(),
                DisplayManager.getHeight(),
                Framebuffer.DEPTH_TEXTURE);
        mousePickingBuffer = new Framebuffer(
                DisplayManager.getWidth(),
                DisplayManager.getHeight(),
                Framebuffer.DEPTH_TEXTURE);
    }

    public static void initWater() {
        waterDUDV = TextureLoader.loadTexture("water/waterDUDV.png");
        waterNormalMap = TextureLoader.loadTexture("water/waterNormalMap.png");
    }
}