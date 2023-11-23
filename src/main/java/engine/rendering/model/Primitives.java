package engine.rendering.model;

import engine.rendering.texture.Texture;
import engine.rendering.texture.TextureLoader;

/**
 * @author gabed
 * @Date 7/21/2022
 */
public class Primitives {

    public static Texture white;

    static {
        white = TextureLoader.loadTexture("engine/white.png");
    }

}
