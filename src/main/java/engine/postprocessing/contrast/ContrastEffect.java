package engine.postprocessing.contrast;

import engine.postprocessing.PostProcessingEffect;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class ContrastEffect extends PostProcessingEffect {

    public float contrast = 0.0f;

    public ContrastEffect() {
        super("contrast");
    }


    public void render(int colorBuffer) {
        start();
        loadUniform(getUniformLocation("contrast"), contrast);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, colorBuffer);
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 8);
        stop();
    }
}
