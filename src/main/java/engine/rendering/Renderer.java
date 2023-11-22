package engine.rendering;

import editor.*;
import engine.GameEngine;
import engine.TerrainManager;
import engine.ecs.Entity;
import engine.ecs.component.ModelRenderer;
import engine.ecs.component.ObjRenderer;
import engine.ecs.component.Transform;
import engine.postprocessing.PostProcessing;
import engine.rendering.model.*;
import engine.rendering.yaycoolnewmodels.ComplexModel;
import engine.shader.*;
import engine.util.MatrixBuilder;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;

public class Renderer {

    private static WaterShader waterShader;

    private static StandardShader defaultShader;

    private static SkyboxShader skyboxShader;

    private static TerrainShader terrainShader;

    private static GridShader gridShader;

    private static GrassShader vegetationShader;

    private static MousePickingShader mousePickingShader;

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

        vegetationShader.start();
        vegetationShader.loadProjectionMatrix(mat);
        vegetationShader.stop();

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
        vegetationShader = new GrassShader();
        mousePickingShader = new MousePickingShader();
        updateProjection(DisplayManager.getWidth(), DisplayManager.getHeight());
    }

    private static void renderVegetationModel(VegetationModel model, Transform transform, float damper, float reflect) {
        glDisable(GL_CULL_FACE);
        vegetationShader.start();
        vegetationShader.loadLight(GameEngine.getInstance().light);
        vegetationShader.setMaterial(damper, reflect);
        vegetationShader.loadMovement(GameEngine.grassMovement);
        vegetationShader.loadTransformationMatrix(MatrixBuilder.createTransformationMatrix(transform.getPosition(), transform.getRotation(), transform.getScale()));
        vegetationShader.loadViewMatrix(GameEngine.getInstance().camera.getViewMatrix());
        vegetationShader.setClipPlane(new Vector4f(0, GameEngine.getInstance().clipDirection, 0, GameEngine.getInstance().clipHeight));
        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().textureID());
        GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        vegetationShader.stop();
        glEnable(GL_CULL_FACE);
    }

    public static void render(Entity entity) {
        ModelRenderer mr = entity.getComponent(ModelRenderer.class);
        ObjRenderer obj = entity.getComponent(ObjRenderer.class);

        if(mr != null && mr.getModel() != null) {
            Model m = mr.getModel();
            if (VegetationModel.class.isAssignableFrom(m.getClass()))
                renderVegetationModel((VegetationModel) m, entity.getTransform(), mr.shineDamper, mr.reflectivity);
            else if (SkyboxModel.class.isAssignableFrom(m.getClass()))
                renderSkybox((SkyboxModel) m);

        }
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
        waterShader.loadLight(GameEngine.getInstance().light);
        waterShader.loadCameraPosition(GameEngine.getInstance().camera.getPosition());
        waterShader.loadTransformationMatrix(MatrixBuilder.createTransformationMatrix(position, new Vector3f(0, 0, 0), new Vector3f(1, 1, 1)));
        waterShader.loadViewMatrix(GameEngine.getInstance().camera.getViewMatrix());
        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, GameEngine.reflectionBuffer.getColorTexture());
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, GameEngine.refractionBuffer.getColorTexture());
        GL13.glActiveTexture(GL13.GL_TEXTURE2);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, GameEngine.waterDUDV.textureID());
        GL13.glActiveTexture(GL13.GL_TEXTURE3);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, GameEngine.waterNormalMap.textureID());
        GL13.glActiveTexture(GL13.GL_TEXTURE4);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, GameEngine.refractionBuffer.getDepthTexture());

        GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
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
        mousePickingShader.loadTransformationMatrix(MatrixBuilder.createTransformationMatrix(transform.getPosition(), transform.getRotation(), transform.getScale()));
        mousePickingShader.loadViewMatrix(GameEngine.getInstance().camera.getViewMatrix());
        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
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
        defaultShader.setMaterial(20, 0.5f);
        defaultShader.loadTransformationMatrix(MatrixBuilder.createTransformationMatrix(transform.getPosition(), transform.getRotation(), transform.getScale()));
        defaultShader.loadViewMatrix(view);
        defaultShader.setClipPlane(new Vector4f(0, GameEngine.getInstance().clipDirection, 0, GameEngine.getInstance().clipHeight));
        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        if(model.getMaterial().getLoadedTexture() != null) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getMaterial().getLoadedTexture().textureID());
        }else {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, Primitives.white.textureID());
        }
        GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        defaultShader.stop();
        glEnable(GL_CULL_FACE);
    }



    public static void renderTerrain(TerrainModel model, float damper, float reflect) {
        terrainShader.start();
        terrainShader.loadLight(GameEngine.getInstance().light);
        terrainShader.setMaterial(damper, reflect);
        terrainShader.setTextureScales(TerrainManager.textureScale1,TerrainManager.textureScale2, TerrainManager.textureScale3, TerrainManager.textureScale4);
        terrainShader.connectTextures();
        //terrainShader.loadTransformationMatrix(MatrixBuilder.defaultTransformation);
        terrainShader.loadViewMatrix(GameEngine.getInstance().camera.getViewMatrix());
        terrainShader.setClipPlane(new Vector4f(0, GameEngine.getInstance().clipDirection, 0, GameEngine.getInstance().clipHeight));
        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getSplat().textureID());

        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getT1().textureID());
        GL13.glActiveTexture(GL13.GL_TEXTURE2);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getT2().textureID());
        GL13.glActiveTexture(GL13.GL_TEXTURE3);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getT3().textureID());
        GL13.glActiveTexture(GL13.GL_TEXTURE4);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getT4().textureID());


        GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        terrainShader.stop();
    }

    public static void renderSkybox(SkyboxModel model) {
        skyboxShader.start();
        skyboxShader.loadViewMatrix(MatrixBuilder.createStationaryViewMatrix(GameEngine.getInstance().camera));
        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, model.getCubemapTexture());
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.getVertexCount());
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
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

    public static void endScene(Framebuffer fb, Framebuffer picking, Camera camera, Entity selected) {

        PostProcessing.doPostProcessing(fb.getColorTexture(), fb.getDepthTexture());

        //ImGui.showDemoWindow();

        WindowMenubar.render();
        GameViewportWindow.render(PostProcessing.finalBuffer, picking, camera, selected);
        ConsoleWindow.render();
        DebugWindow.render();
        ExplorerWindow.render();
        InspectorWindow.render();
        LightingWindow.render(GameEngine.getInstance().loadedScene.getLights());
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
}
