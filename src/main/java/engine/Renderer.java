package engine;

import editor.ConsoleWindow;
import editor.DebugWindow;
import editor.GameViewportWindow;
import engine.display.DisplayManager;
import engine.ecs.Entity;
import engine.ecs.component.Transform;
import engine.input.Mouse;
import engine.model.Model;
import engine.model.SkyboxModel;
import engine.model.TerrianModel;
import engine.model.TexturedModel;
import engine.postprocessing.PostProcessing;
import engine.shader.*;
import engine.util.MatrixBuilder;
import engine.util.Time;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;

public class Renderer {
    private StaticShader defaultShader;
    private SkyboxShader skyboxShader;
    private TerrainShader terrainShader;
    private GridShader gridShader;


    public void UpdateProjection() {
        Matrix4f mat = MatrixBuilder.createProjectionMatrix();
        defaultShader.start();
        defaultShader.loadProjectionMatrix(mat);
        defaultShader.stop();

        skyboxShader.start();
        skyboxShader.loadProjectionMatrix(mat);
        skyboxShader.stop();

        terrainShader.start();
        terrainShader.loadProjectionMatrix(mat);
        terrainShader.stop();

        gridShader.start();
        gridShader.loadProjectionMatrix(mat);
        gridShader.stop();
    }

    public void Prepare() {
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
    }

    public void Render(Entity entity) {
        if (TerrianModel.class.isAssignableFrom(entity.getModel().getClass()))
            RenderTerrain((TerrianModel) entity.getModel(), entity.getTransform());
        else if (TexturedModel.class.isAssignableFrom(entity.getModel().getClass()))
            RenderTextured((TexturedModel) entity.getModel(), entity.getTransform());
        else if (SkyboxModel.class.isAssignableFrom(entity.getModel().getClass()))
            RenderSkybox((SkyboxModel) entity.getModel());
        else
            RenderDefault(entity);
    }

    public void Render(Model model) {
        if (TerrianModel.class.isAssignableFrom(model.getClass()))
            RenderTerrain((TerrianModel) model, new Transform());
        else if (TexturedModel.class.isAssignableFrom(model.getClass()))
            RenderTextured((TexturedModel) model, new Transform());
        else if (SkyboxModel.class.isAssignableFrom(model.getClass()))
            RenderSkybox((SkyboxModel) model);
        else
            RenderDefault(new Entity(new Transform(), model));
    }

    private void RenderDefault(Entity entity) {
        defaultShader.start();
        defaultShader.loadLight(GameEngine.light);
        defaultShader.setMaterial(20, 0.5f);
        defaultShader.loadTransformationMatrix(MatrixBuilder.createTransformationMatrix(entity.getTransform().getPosition(), entity.getTransform().getRotation(), entity.getTransform().getScale()));
        defaultShader.loadViewMatrix(MatrixBuilder.createViewMatrix(GameEngine.camera));
        GL30.glBindVertexArray(entity.getModel().getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        defaultShader.stop();
    }

    private void RenderTextured(TexturedModel model, Transform transform) {
        defaultShader.start();
        defaultShader.loadLight(GameEngine.light);
        defaultShader.setMaterial(20, 0.5f);
        defaultShader.loadTransformationMatrix(MatrixBuilder.createTransformationMatrix(transform.getPosition(), transform.getRotation(), transform.getScale()));
        defaultShader.loadViewMatrix(MatrixBuilder.createViewMatrix(GameEngine.camera));
        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureID());
        GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        defaultShader.stop();
    }

    private void RenderTerrain(TerrianModel model, Transform transform) {
        terrainShader.start();
        terrainShader.loadTransformationMatrix(MatrixBuilder.createTransformationMatrix(transform.getPosition(), transform.getRotation(), transform.getScale()));
        terrainShader.loadViewMatrix(MatrixBuilder.createViewMatrix(GameEngine.camera));
        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);

        GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        terrainShader.stop();
    }

    private void RenderSkybox(SkyboxModel model) {
        skyboxShader.start();
        skyboxShader.loadViewMatrix(MatrixBuilder.createStationaryViewMatrix(GameEngine.camera));
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

    public void beginFrame(Framebuffer fb) {
        fb.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
        DisplayManager.newImguiFrame();
        DisplayManager.createDockspace();
        Time.updateTime();
        Mouse.update();
    }

    public void endScene(Framebuffer fb) {

        fb.unbind();
        PostProcessing.doPostProcessing(fb.getColorTexture());
        GameViewportWindow.render(PostProcessing.finalBuffer);
        ConsoleWindow.render();
        DebugWindow.render();
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
