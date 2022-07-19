package engine.postprocessing.combine;

import engine.postprocessing.PostProcessingEffect;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

public class CombineEffect extends PostProcessingEffect {

    private int colormask = -1;

    public CombineEffect() {
        super("combine");
    }

    public void render(int colorBuffer, int mask) {
        this.colormask = mask;
        this.render(colorBuffer);
    }

    public void render(int colorBuffer) {
        start();
        loadUniform(getUniformLocation("textureSampler"), 0);
        loadUniform(getUniformLocation("textureSampler1"), 1);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, colorBuffer);
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, colormask);
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 8);
        stop();
    }
}
