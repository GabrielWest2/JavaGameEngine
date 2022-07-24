package engine.model;

import engine.texture.TextureLoader;

/**
 * @author gabed
 * @Date 7/21/2022
 */
public class Primitives {

    public static Model cube;

    static {
        cube = OBJLoader.loadTexturedOBJ("cube.obj", TextureLoader.getTexture("white.png"));
    }

}
