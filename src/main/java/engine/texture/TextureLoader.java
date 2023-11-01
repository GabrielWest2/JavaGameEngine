package engine.texture;

import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_LOD_BIAS;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class TextureLoader {

    private static final List<Integer> textureIDs = new ArrayList<>();
    private static int width, height;

    private static Texture loadImage(String path) {
        int[] pixels = null;
        try {
            BufferedImage image = ImageIO.read(new FileInputStream(path));
            if(image == null){
                System.out.println("Image is null!!");
            }
            width = image.getWidth();
            height = image.getHeight();
            pixels = new int[width * height];
            image.getRGB(0, 0, width, height, pixels, 0, width);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int[] data = new int[width * height];
        for (int i = 0; i < width * height; i++) {
            int a = (pixels[i] & 0xff000000) >> 24;
            int r = (pixels[i] & 0xff0000) >> 16;
            int g = (pixels[i] & 0xff00) >> 8;
            int b = (pixels[i] & 0xff);

            data[i] = a << 24 | b << 16 | g << 8 | r;
        }

        int result = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, result);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, 0);

        float amount = Math.min(4f, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
        glTexParameterf(GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, amount);

        IntBuffer buffer = ByteBuffer.allocateDirect(data.length << 2)
                .order(ByteOrder.nativeOrder()).asIntBuffer();
        buffer.put(data).flip();

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA,
                GL_UNSIGNED_BYTE, buffer);
        glGenerateMipmap(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, 0);
        return new Texture(result);
    }

    public static int loadCubeMap(String[] textureFiles) {
        int texId = GL11.glGenTextures();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL_TEXTURE_CUBE_MAP, texId);

        for (int t = 0; t < textureFiles.length; t++) {
            int[] pixels = null;
            try {
                BufferedImage image = ImageIO.read(new FileInputStream("res/skybox/" + textureFiles[t] + ".png"));
                width = image.getWidth();
                height = image.getHeight();
                pixels = new int[width * height];
                image.getRGB(0, 0, width, height, pixels, 0, width);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int[] data = new int[width * height];
            for (int i = 0; i < width * height; i++) {
                int a = (pixels[i] & 0xff000000) >> 24;
                int r = (pixels[i] & 0xff0000) >> 16;
                int g = (pixels[i] & 0xff00) >> 8;
                int b = (pixels[i] & 0xff);

                data[i] = a << 24 | b << 16 | g << 8 | r;
            }

            int result = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, result);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            //glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            IntBuffer buffer = ByteBuffer.allocateDirect(data.length << 2)
                    .order(ByteOrder.nativeOrder()).asIntBuffer();
            buffer.put(data).flip();

            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + t, 0, GL_RGBA, width, height, 0, GL_RGBA,
                    GL_UNSIGNED_BYTE, buffer);
            textureIDs.add(texId);
        }
        return texId;
    }


    public static Texture loadTexture(String path) {
        Texture texture = loadImage("res/" + path);
        textureIDs.add(texture.textureID());
        return texture;
    }


    public static void cleanUp() {
        for (int tex : textureIDs) {
            GL11.glDeleteTextures(tex);
        }
    }
}
