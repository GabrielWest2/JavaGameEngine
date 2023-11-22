package engine.postprocessing;

import engine.rendering.DisplayManager;
import engine.postprocessing.brightnessfilter.BrightnessFilterEffect;
import engine.postprocessing.combine.CombineEffect;
import engine.postprocessing.contrast.ContrastEffect;
import engine.postprocessing.horizontalblur.HorizontalBlurEffect;
import engine.postprocessing.verticalblur.VerticalBlurEffect;
import engine.shader.Framebuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL13.*;


public class PostProcessing {

    private static final float[] POSITIONS = {-1f, 1f, -1f, -1f, 1f, 1f, 1f, -1f};

    private static int quad, v;

    public static ContrastEffect contrastEffect = new ContrastEffect();

    public static HorizontalBlurEffect horizontalBlurEffect = new HorizontalBlurEffect();

    public static VerticalBlurEffect verticalBlurEffect = new VerticalBlurEffect();

    public static BrightnessFilterEffect brightnessFilterEffect = new BrightnessFilterEffect();

    public static CombineEffect combineEffect = new CombineEffect();

    public static Framebuffer framebuffer;
    public static Framebuffer framebuffer1;
    public static Framebuffer framebuffer2;
    public static Framebuffer framebuffer3;
    public static Framebuffer finalBuffer;


    public static void init() {
        framebuffer = new Framebuffer(DisplayManager.getWidth(), DisplayManager.getHeight(), Framebuffer.NONE);
        framebuffer1 = new Framebuffer(DisplayManager.getWidth(), DisplayManager.getHeight(), Framebuffer.NONE);
        framebuffer2 = new Framebuffer(DisplayManager.getWidth(), DisplayManager.getHeight(), Framebuffer.NONE);
        framebuffer3 = new Framebuffer(DisplayManager.getWidth(), DisplayManager.getHeight(), Framebuffer.NONE);
        finalBuffer = new Framebuffer(DisplayManager.getWidth(), DisplayManager.getHeight(), Framebuffer.NONE);

        createFullscreenQuad();
    }

    private static void createFullscreenQuad() {
        quad = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(quad);
        int vboID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(POSITIONS.length);
        buffer.put(POSITIONS);
        buffer.flip();
        v = buffer.capacity();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }

    public static void doPostProcessing(int colorBuffer, int depthBuffer) {
        start();

        finalBuffer.bind();
        contrastEffect.render(colorBuffer, depthBuffer);
        finalBuffer.unbind();

        /*
        finalBuffer.bind();
        contrastEffect.render(colorBuffer);
        finalBuffer.unbind();
        */
		/*
		framebuffer.bind();
		contrastEffect.render(colorBuffer);
		framebuffer.unbind();


		framebuffer1.bind();
		brightnessFilterEffect.render(framebuffer.getColorTexture());
		framebuffer1.unbind();

		framebuffer2.bind();
		horizontalBlurEffect.render(framebuffer1.getColorTexture());
		framebuffer2.unbind();

		framebuffer3.bind();
		verticalBlurEffect.render(framebuffer2.getColorTexture());
		framebuffer3.unbind();

		finalBuffer.bind();
		combineEffect.render(colorBuffer, framebuffer3.getColorTexture());
		finalBuffer.unbind();*/

        end();
    }


    public static void cleanUp() {

    }

    private static void start() {

        GL30.glBindVertexArray(quad);
        GL20.glEnableVertexAttribArray(0);
        glDisable(GL_DEPTH_TEST);
    }

    private static void end() {
        glEnable(GL_DEPTH_TEST);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }


}
