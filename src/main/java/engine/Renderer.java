package engine;

import editor.ConsoleWindow;
import editor.DebugWindow;
import editor.GameViewportWindow;
import engine.ecs.Entity;
import engine.input.Mouse;
import engine.model.TexturedModel;
import engine.postprocessing.PostProcessing;
import engine.shader.Framebuffer;
import engine.shader.ShaderProgram;
import engine.shader.StaticShader;
import engine.shader.TerrainShader;
import engine.shader.display.DisplayManager;
import engine.texture.NormalMappedModel;
import engine.util.MatrixBuilder;
import engine.util.Time;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;

public class Renderer {
    private final List<ShaderProgram> shaders = new ArrayList<>();

    public void setShader(ShaderProgram shader) {
        this.shaders.add(shader);
    }

    public void UpdateProjection() {
        for (ShaderProgram shader : shaders) {
            Matrix4f mat = MatrixBuilder.createProjectionMatrix();
            shader.start();
            shader.loadProjectionMatrix(mat);
            shader.stop();
        }
    }

    public void Prepare() {


        glEnable(GL_MULTISAMPLE);
        // Set the clear color
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
    }

    public void Render(Entity entity, StaticShader shader) {

        shader.loadProjectionMatrix(MatrixBuilder.createProjectionMatrix());
        shader.loadTransformationMatrix(MatrixBuilder.createTransformationMatrix(entity.getTransform().getPosition(), entity.getTransform().getRotation(), entity.getTransform().getScale()));
        GL30.glBindVertexArray(entity.getModel().getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        if (entity.getModel() instanceof TexturedModel) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, ((TexturedModel) entity.getModel()).getTexture().getTextureID());
        }
        if (entity.getModel() instanceof NormalMappedModel) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, ((NormalMappedModel) entity.getModel()).getTexture().getTextureID());
            GL13.glActiveTexture(GL13.GL_TEXTURE1);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, ((NormalMappedModel) entity.getModel()).getNormalMap().getTextureID());
        }
        GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);

    }

    public void Render(Entity entity, TerrainShader terrainShader) {
        //glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
        terrainShader.start();
        terrainShader.loadProjectionMatrix(MatrixBuilder.createProjectionMatrix());
        terrainShader.loadTransformationMatrix(MatrixBuilder.createTransformationMatrix(entity.getTransform().getPosition(), entity.getTransform().getRotation(), entity.getTransform().getScale()));
        GL30.glBindVertexArray(entity.getModel().getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        if (entity.getModel() instanceof TexturedModel) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, ((TexturedModel) entity.getModel()).getTexture().getTextureID());
        }

        GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        terrainShader.stop();
        //glPolygonMode( GL_FRONT_AND_BACK, GL_FILL );
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
}
