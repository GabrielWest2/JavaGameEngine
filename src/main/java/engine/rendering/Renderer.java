package engine.rendering;

import editor.*;
import engine.GameEngine;
import engine.TerrainManager;
import engine.ecs.Entity;
import engine.ecs.component.Transform;
import engine.postprocessing.PostProcessing;
import engine.rendering.model.*;
import engine.rendering.texture.Texture;
import engine.rendering.texture.TextureLoader;
import engine.rendering.yaycoolnewmodels.ComplexModel;
import engine.shader.*;
import engine.util.MatrixBuilder;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

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

    public static final int MAX_POINT_LIGHTS = 5;

    public static final int MAX_SPOT_LIGHTS = 5;

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
    }



    public static void renderModel(ComplexModel m, Transform transform, boolean cullBacks) {
        for(Model mesh : m.getMeshes()){
            renderTextured(mesh, transform, cullBacks);
        }
    }


    public static void renderPicking(ComplexModel m, Transform transform, boolean cullBacks, int entityId) {
        for(Model mesh : m.getMeshes()){
            renderMousePicking(mesh, transform, cullBacks, entityId);
        }
    }


    public static void renderWater(WaterModel model, Vector3f position, float damper, float reflect) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        waterShader.start();
        waterShader.connectTextures();
        waterShader.setMaterial(damper, reflect);
        waterShader.loadWaterMovement(GameEngine.waterMovement);
        waterShader.loadCameraPosition(
                GameEngine.getInstance().camera.getPosition());
        waterShader.loadTransformationMatrix(
                MatrixBuilder.createTransformationMatrix(
                        position,
                        new Vector3f(0, 0, 0),
                        new Vector3f(1, 1, 1))
        );
        waterShader.loadViewMatrix(
                GameEngine.getInstance().camera.getViewMatrix());
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



    private static void renderMousePicking(Model model, Transform transform, boolean cull, int entityId) {
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
                GameEngine.getInstance().camera.getViewMatrix());
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


    public static void renderTextured(Model model, Transform transform, boolean cullBack) {
        if(cullBack)
            glEnable(GL_CULL_FACE);
        else
            glDisable(GL_CULL_FACE);
        defaultShader.start();
        Matrix4f view = GameEngine.getInstance().camera.getViewMatrix();
        defaultShader.loadMaterial(model.getMaterial());
        defaultShader.loadLights(GameEngine.getInstance().loadedScene.getLights(), view);
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
                        GameEngine.getInstance().clipDirection,
                        0,
                        GameEngine.getInstance().clipHeight
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
        Matrix4f view = GameEngine.getInstance().camera.getViewMatrix();
        terrainShader.loadViewMatrix(view);
        terrainShader.loadLights(GameEngine.getInstance().loadedScene.getLights(), view);

        terrainShader.setTextureScales(
                TerrainManager.textureScale1,
                TerrainManager.textureScale2,
                TerrainManager.textureScale3,
                TerrainManager.textureScale4);

        terrainShader.connectTextures();
        terrainShader.loadTransformationMatrix(model.getTransformationMatrix());

        terrainShader.setClipPlane(new Vector4f(0,
                GameEngine.getInstance().clipDirection,
                0, GameEngine.getInstance().clipHeight));

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
                        GameEngine.getInstance().camera
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
        DisplayManager.createDockspace();
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
    }

    public static void endScene(Camera camera, Entity selected) {

        PostProcessing.doPostProcessing(
                frameBuffer.getColorTexture(),
                frameBuffer.getDepthTexture()
        );

        GameViewportWindow.render(
                PostProcessing.finalBuffer,
                mousePickingBuffer,
                camera,
                selected
        );

        LightingWindow.render(GameEngine.getInstance().loadedScene.getLights());
        WindowMenubar.render();
        ConsoleWindow.render();
        ExplorerWindow.render();
        InspectorWindow.render();

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