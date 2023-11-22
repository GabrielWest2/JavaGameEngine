package engine;

import engine.rendering.texture.Texture;
import engine.rendering.texture.TextureLoader;
import engine.util.FastNoiseLite;
import org.joml.Vector2i;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author gabed
 * @Date 7/23/2022
 */
public class TerrainManager {

    public static float textureScale1 = 120f;

    public static float textureScale2 = 500f;

    public static float textureScale3 = 120f;

    public static float textureScale4 = 0.75f;


    public static float offsetX = 0;

    public static float offsetY = 0;

    public static float scale = 2;

    public static float amplitude = 10;

    static final FastNoiseLite noise = new FastNoiseLite();

    private static HashMap<Vector2i, TerrainChunk> terrainChunks = new HashMap();


    public static Texture t1;

    public static Texture t2;

    public static Texture t3;

    public static Texture t4;

    public static Texture splatmap;

    public static BufferedImage heightmap;

    public static void init(){
        splatmap = TextureLoader.loadTexture("Terrain/Terrain_splatmap_0.png");
        t1 = TextureLoader.loadTexture("Terrain/Texture_Grass_Diffuse.png");
        t2 = TextureLoader.loadTexture("Terrain/Texture_Dirt_Diffuse.png");
        t3 = TextureLoader.loadTexture("Terrain/Texture_Gravel_Diffuse.png");
        t4 = TextureLoader.loadTexture("Terrain/Texture_Rock_Diffuse.png");

        try {
            heightmap = ImageIO.read(new File("res/Terrain/heightmap.png"));
        } catch (IOException e) {
            System.out.println("[ERROR] Couldn't load terrain heightmap! ");
            e.printStackTrace();
        }

        terrainChunks.put(new Vector2i(0, 0), new TerrainChunk(0, 0, 1));


    }

    public static void regenerateChunks(int lod){
        terrainChunks = new HashMap();
        long t = System.currentTimeMillis();
        terrainChunks.put(new Vector2i(0, 0), new TerrainChunk(0, 0, lod));

        System.out.println("Generated chunks; took " + (System.currentTimeMillis()-t)+"ms");
    }

    public static void renderChunks(){
        for(TerrainChunk chunk : terrainChunks.values()){
            chunk.render();
        }
    }

}