package engine;

import editor.*;
import engine.display.DisplayManager;
import engine.ecs.Entity;
import engine.ecs.component.ModelRenderer;
import engine.ecs.component.ObjRenderer;
import engine.ecs.component.Terrain;
import engine.ecs.component.Transform;
import engine.model.*;
import engine.postprocessing.PostProcessing;
import engine.shader.*;
import engine.util.MatrixBuilder;
import imgui.ImGui;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.List;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;

public class Renderer {
    private WaterShader waterShader;
    private StaticShader defaultShader;
    private SkyboxShader skyboxShader;
    private TerrainShader terrainShader;
    private GridShader gridShader;
    private GrassShader vegetationShader;



    public void updateProjection() {
        Matrix4f mat = MatrixBuilder.createProjectionMatrix();
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

    }

    public void prepare() {
        glEnable(GL_MULTISAMPLE);
        // Set the clear color
        glClearColor(0.5f, 0.5f, 1.0f, 1.0f);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        defaultShader = new StaticShader();
        skyboxShader = new SkyboxShader();
        terrainShader = new TerrainShader();
        gridShader = new GridShader();
        waterShader = new WaterShader();
        vegetationShader = new GrassShader();
        updateProjection();
    }

    public void renderTerrainDetails(List<Transform> transforms, VegetationModel model){
        glDisable(GL_CULL_FACE);
        vegetationShader.start();
        vegetationShader.loadLight(GameEngine.getInstance().light);
        vegetationShader.setMaterial(20, 0.2f);
        vegetationShader.loadMovement(GameEngine.grassMovement);
        vegetationShader.loadViewMatrix(GameEngine.getInstance().camera.getViewMatrix());
        vegetationShader.setClipPlane(new Vector4f(0, GameEngine.getInstance().clipDirection, 0, GameEngine.getInstance().clipHeight));
        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().textureID());

        for(Transform transform : transforms) {
            vegetationShader.loadTransformationMatrix(MatrixBuilder.createTransformationMatrix(transform.getPosition(), transform.getRotation(), transform.getScale()));
            GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        }

        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        vegetationShader.stop();
        glEnable(GL_CULL_FACE);
    }

    public void renderTerrainDetails(List<Transform> transforms, MultiTexturedModel m){
        glDisable(GL_CULL_FACE);
        for(String materialName : m.getMaterialModels().keySet()){
            if (m.getMaterialTextures().get(materialName) == null)
                continue;
            TexturedModel model = m.getMaterialModels().get(materialName);
            defaultShader.start();
            defaultShader.loadLight(GameEngine.getInstance().light);
            defaultShader.setMaterial(20, 0.2f);
            defaultShader.loadViewMatrix(GameEngine.getInstance().camera.getViewMatrix());
            defaultShader.setClipPlane(new Vector4f(0, GameEngine.getInstance().clipDirection, 0, GameEngine.getInstance().clipHeight));
            GL30.glBindVertexArray(model.getVaoID());
            GL20.glEnableVertexAttribArray(0);
            GL20.glEnableVertexAttribArray(1);
            GL20.glEnableVertexAttribArray(2);
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, m.getMaterialTextures().get(materialName).textureID());

            for(Transform transform : transforms) {
                defaultShader.loadTransformationMatrix(MatrixBuilder.createTransformationMatrix(transform.getPosition(), transform.getRotation(), transform.getScale()));
                GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            }

            GL20.glDisableVertexAttribArray(2);
            GL20.glDisableVertexAttribArray(1);
            GL20.glDisableVertexAttribArray(0);
            GL30.glBindVertexArray(0);
            defaultShader.stop();

        }
        glEnable(GL_CULL_FACE);
    }


    private void renderVegetationModel(VegetationModel model, Transform transform, float damper, float reflect) {
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

    public void render(Entity entity) {
        ModelRenderer mr = entity.getComponent(ModelRenderer.class);
        ObjRenderer obj = entity.getComponent(ObjRenderer.class);

        if(mr != null && mr.getModel() != null) {
            Model m = mr.getModel();
            if (TerrianModel.class.isAssignableFrom(m.getClass()))
                renderTerrain((TerrianModel) m, entity.getComponent(Terrain.class), entity.getTransform(), mr.shineDamper, mr.reflectivity);
            else if (VegetationModel.class.isAssignableFrom(m.getClass()))
                renderVegetationModel((VegetationModel) m, entity.getTransform(), mr.shineDamper, mr.reflectivity);
            else if (TexturedModel.class.isAssignableFrom(m.getClass()))
                renderTextured((TexturedModel) m, entity.getTransform(), mr.cullBack, mr.shineDamper, mr.reflectivity);
            else if (SkyboxModel.class.isAssignableFrom(m.getClass()))
                renderSkybox((SkyboxModel) m);
            else if (MultiTexturedModel.class.isAssignableFrom(m.getClass()))
                renderMultiTexturedModel((MultiTexturedModel) m, entity.getTransform(), mr.cullBack, mr.shineDamper, mr.reflectivity);
            else if (WaterModel.class.isAssignableFrom(m.getClass()))
                renderWater((WaterModel) m, entity.getTransform(), mr.shineDamper, mr.reflectivity);
            else
                renderDefault(entity);
        }

        if(obj != null && obj.getModel() != null) {
            Model m = obj.getModel();
            if (TexturedModel.class.isAssignableFrom(m.getClass()))
                renderTextured((TexturedModel) m, entity.getTransform(), obj.cullBack, obj.shineDamper, obj.reflectivity);
        }




    }


    private void renderWater(WaterModel model, Transform transform, float damper, float reflect) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        waterShader.start();
        waterShader.connectTextures();
        waterShader.setMaterial(damper, reflect);
        waterShader.loadWaterMovement(GameEngine.waterMovement);
        waterShader.loadLight(GameEngine.getInstance().light);
        waterShader.loadCameraPosition(GameEngine.getInstance().camera.getPosition());
        waterShader.loadTransformationMatrix(MatrixBuilder.createTransformationMatrix(transform.getPosition(), transform.getRotation(), transform.getScale()));
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

    private void renderMultiTexturedModel(MultiTexturedModel m, Transform transform, boolean cull, float damper, float reflect) {
        if(cull)
            glEnable(GL_CULL_FACE);
        else
            glDisable(GL_CULL_FACE);
        for(String materialName : m.getMaterialModels().keySet()){
            if (m.getMaterialTextures().get(materialName) == null)
                continue;
            TexturedModel model = m.getMaterialModels().get(materialName);
            defaultShader.start();
            defaultShader.loadLight(GameEngine.getInstance().light);
            defaultShader.setMaterial(damper, reflect);
            defaultShader.loadTransformationMatrix(MatrixBuilder.createTransformationMatrix(transform.getPosition(), transform.getRotation(), transform.getScale()));
            defaultShader.loadViewMatrix(GameEngine.getInstance().camera.getViewMatrix());
            defaultShader.setClipPlane(new Vector4f(0, GameEngine.getInstance().clipDirection, 0, GameEngine.getInstance().clipHeight));
            GL30.glBindVertexArray(model.getVaoID());
            GL20.glEnableVertexAttribArray(0);
            GL20.glEnableVertexAttribArray(1);
            GL20.glEnableVertexAttribArray(2);
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, m.getMaterialTextures().get(materialName).textureID());
            GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            GL20.glDisableVertexAttribArray(2);
            GL20.glDisableVertexAttribArray(1);
            GL20.glDisableVertexAttribArray(0);
            GL30.glBindVertexArray(0);
            defaultShader.stop();

        }
        glEnable(GL_CULL_FACE);
    }

    private void renderDefault(Entity entity) {
        ModelRenderer mr = entity.getComponent(ModelRenderer.class);
        Model m = mr.getModel();
        if(mr.cullBack)
            glEnable(GL_CULL_FACE);
        else
            glDisable(GL_CULL_FACE);
        defaultShader.start();
        defaultShader.loadLight(GameEngine.getInstance().light);
        defaultShader.setMaterial(mr.shineDamper, mr.reflectivity);
        defaultShader.loadTransformationMatrix(MatrixBuilder.createTransformationMatrix(entity.getTransform().getPosition(), entity.getTransform().getRotation(), entity.getTransform().getScale()));
        defaultShader.loadViewMatrix(GameEngine.getInstance().camera.getViewMatrix());
        defaultShader.setClipPlane(new Vector4f(0, GameEngine.getInstance().clipDirection, 0, GameEngine.getInstance().clipHeight));
        GL30.glBindVertexArray(m.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        GL11.glDrawElements(GL11.GL_TRIANGLES, m.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        defaultShader.stop();
        glEnable(GL_CULL_FACE);
    }

    private void renderTextured(TexturedModel model, Transform transform, boolean cull, float damper, float reflect) {
        if(cull)
            glEnable(GL_CULL_FACE);
        else
            glDisable(GL_CULL_FACE);
        defaultShader.start();
        defaultShader.loadLight(GameEngine.getInstance().light);
        defaultShader.setMaterial(damper, reflect);
        defaultShader.loadTransformationMatrix(MatrixBuilder.createTransformationMatrix(transform.getPosition(), transform.getRotation(), transform.getScale()));
        defaultShader.loadViewMatrix(GameEngine.getInstance().camera.getViewMatrix());
        defaultShader.setClipPlane(new Vector4f(0, GameEngine.getInstance().clipDirection, 0, GameEngine.getInstance().clipHeight));
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
        defaultShader.stop();
        glEnable(GL_CULL_FACE);
    }


    public void renderTerrain(TerrianModel model, Terrain terrain, Transform transform, float damper, float reflect) {
        if(terrain == null)
            return;
        terrainShader.start();
        terrainShader.loadLight(GameEngine.getInstance().light);
        terrainShader.setMaterial(damper, reflect);
        terrainShader.setTextureScale(terrain.getTextureScale());
        terrainShader.loadTransformationMatrix(MatrixBuilder.createTransformationMatrix(transform.getPosition(), transform.getRotation(), transform.getScale()));
        terrainShader.loadViewMatrix(GameEngine.getInstance().camera.getViewMatrix());
        terrainShader.setClipPlane(new Vector4f(0, GameEngine.getInstance().clipDirection, 0, GameEngine.getInstance().clipHeight));
        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().textureID());
        GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        terrainShader.stop();
    }

    public void renderSkybox(SkyboxModel model) {
        skyboxShader.start();
        skyboxShader.loadViewMatrix(MatrixBuilder.createStationaryViewMatrix(GameEngine.getInstance().camera));
        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, model.getCubemapTexture());
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.getVertexCount());
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        skyboxShader.stop();
        skyboxShader.stop();
    }

    public void beginFrame() {

        DisplayManager.newImguiFrame();
        DisplayManager.createDockspace();
        glEnable(GL_CULL_FACE);
    }

    public void endScene(Framebuffer fb) {

        PostProcessing.doPostProcessing(fb.getColorTexture(), fb.getDepthTexture());

        ImGui.showDemoWindow();

        WindowMenubar.render();
        GameViewportWindow.render(PostProcessing.finalBuffer);
        ConsoleWindow.render();
        DebugWindow.render();
        ExplorerWindow.render();
        InspectorWindow.render();

        DisplayManager.endImguiFrame();
        glfwSwapBuffers(DisplayManager.window); // swap the color buffers
        glfwPollEvents();
    }

    public void cleanUp() {
        defaultShader.cleanUp();
        skyboxShader.cleanUp();
        terrainShader.cleanUp();
        gridShader.cleanUp();
    }
}
