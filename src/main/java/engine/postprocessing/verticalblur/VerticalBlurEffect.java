package engine.postprocessing.verticalblur;

import engine.display.DisplayManager;
import engine.postprocessing.PostProcessingEffect;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class VerticalBlurEffect extends PostProcessingEffect {
    public VerticalBlurEffect() {
        super("verticalblur");
    }


    public void render(int colorBuffer, int depthBuffer) {
        start();
        loadUniform(getUniformLocation("targetHeight"), (float) DisplayManager.getHeight() / 7f);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, colorBuffer);
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 8);
        stop();
    }
}
