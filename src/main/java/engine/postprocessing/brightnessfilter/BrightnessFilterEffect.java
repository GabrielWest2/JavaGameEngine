package engine.postprocessing.brightnessfilter;

import engine.postprocessing.PostProcessingEffect;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class BrightnessFilterEffect extends PostProcessingEffect {


    public BrightnessFilterEffect() {
        super("brightnessfilter");
    }


    public void render(int colorBuffer) {
        start();
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, colorBuffer);
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 8);
        stop();
    }
}
