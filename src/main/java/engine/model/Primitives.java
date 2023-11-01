package engine.model;

import engine.texture.TextureLoader;

/**
 * @author gabed
 * @Date 7/21/2022
 */
public class Primitives {

    public static TexturedModel cube;
    public static TexturedModel plane;
    public static TexturedModel sphere;

    static {
        //cube = OBJLoader.loadSimpleTexturedOBJ("engine/primitive/cube.obj", TextureLoader.loadTexture("white.png"));
        //plane = OBJLoader.loadSimpleTexturedOBJ("engine/primitive/plane.obj", TextureLoader.loadTexture("white.png"));
        //sphere = OBJLoader.loadSimpleTexturedOBJ("engine/primitive/sphere.obj", TextureLoader.loadTexture("white.png"));
    }

}
