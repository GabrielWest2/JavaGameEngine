package engine;

import engine.model.ModelCreator;
import engine.model.TerrianModel;
import engine.texture.Texture;
import engine.texture.TextureLoader;
import engine.util.FastNoiseLite;
import org.joml.Math;
import org.joml.Vector2i;
import org.joml.Vector3f;

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
    public static int terrainWidth = 2000;
    public static float unitSize = 0.05f;
    public static float textureScale = 20f;

    public static float offsetX = 0;
    public static float offsetY = 0;
    public static float scale = 10;
    public static float amplitude = 5;

    private static final FastNoiseLite noise = new FastNoiseLite();
    private static HashMap<Vector2i, TerrianModel> terrainModels = new HashMap();
  //  private static Texture splatMap;
    private static BufferedImage heightMap;
    public static void init(){
      //  splatMap = TextureLoader.loadTexture("Terrain_splatmap.png");
        try {
            heightMap = ImageIO.read(new File("res/Terrain_heightmap.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        createTerrain(0, 0);
        //createTerrain(1, 0);
    }

    public static void renderChunks(){
        for(TerrianModel model : terrainModels.values()){
            Renderer.renderTerrain(model, 20f, 0.2f);
        }
    }

    public static void createTerrain(int chunkX, int chunkY) {
        float[] vertices = new float[((terrainWidth + 1) * (terrainWidth + 1)) * 3];
        float[] textureCoords = new float[((terrainWidth + 1) * (terrainWidth + 1)) * 3];
        int[] triangles = new int[terrainWidth * terrainWidth * 6];
        float[] normals = new float[terrainWidth * terrainWidth * 6];


        int f = 0;
        int t = 0;
        for (int i = 0, y = chunkY*terrainWidth; y <= terrainWidth + chunkY*terrainWidth; y++) {
            for (int x = chunkX*terrainWidth; x <= terrainWidth + chunkX*terrainWidth; x++, i++) {
                System.out.println("NEW X " + x);
                float height = calculateHeight(x, y);
                vertices[f] = x * unitSize;
                vertices[f + 1] = height;
                vertices[f + 2] = y * unitSize;

                Vector3f normal = calculateNormal(x, y);
                normals[f] = normal.x;
                normals[f + 1] = normal.y;
                normals[f + 2] = normal.z;

                textureCoords[t] = x * unitSize / terrainWidth;
                textureCoords[t+1] = y * unitSize / terrainWidth;
                f += 3;
                t += 2;
            }
        }
        for (int ti = 0, vi = 0, y = 0; y < terrainWidth; y++, vi++) {
            for (int x = 0; x < terrainWidth; x++, ti += 6, vi++) {
                triangles[ti] = vi;
                triangles[ti + 3] = triangles[ti + 2] = vi + 1;
                triangles[ti + 4] = triangles[ti + 1] = vi + terrainWidth + 1;
                triangles[ti + 5] = vi + terrainWidth + 2;
            }
        }

        TerrianModel model = ModelCreator.loadToTerrainVAO(vertices, triangles, textureCoords, normals, GameEngine.grass);
        terrainModels.put(new Vector2i(chunkX, chunkY), model);
    }

    public static Vector3f calculateNormal(float x, float y){
        float heightL = calculateHeight(x - 1, y);
        float heightR = calculateHeight(x + 1, y);
        float heightD = calculateHeight(x, y - 1);
        float heightU = calculateHeight(x, y + 1);

        Vector3f normal = new Vector3f(heightL-heightR, 2f, heightD-heightU);
        normal = normal.normalize();
        return normal;
    }

    public static float calculateHeight(float x, float y){
        //System.out.println("(" + x+", "+y+")");
        if(x < 0 || x >  terrainWidth || y < 0 || y > terrainWidth)
            return 1.0f;
        try {
            float normalX = x / (float) terrainWidth;
            float normalY = y / (float) terrainWidth;
            int sampleX = (int) (normalX * (float) heightMap.getWidth());
            int sampleY = (int) (normalY * (float) heightMap.getHeight());

            int color = heightMap.getRGB(sampleX, sampleY);
            int blue = color & 0xff;
            int green = (color & 0xff00) >> 8;
            int red = (color & 0xff0000) >> 16;
            System.out.println(red);
            return red/5f;
        }catch(Exception e){
            e.printStackTrace();
        }

        return 0;
       // noise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        //return noise.GetNoise((x + offsetX) * scale, (y + offsetY) * scale) * amplitude + 1;
    }

    public static Vector3f calculateObjectRotation(float x, float y){
        Vector3f normal = calculateNormal(x, y);
        float theta = Math.acos(normal.y);
        float phi = (float) (Math.PI-(normal.z / Math.abs(normal.z)) * Math.acos(normal.x / Math.sqrt(normal.x * normal.x + normal.z * normal.z)));
        return new Vector3f(0, (float) Math.toDegrees(phi), (float) Math.toDegrees(theta));
    }


    public float getTextureScale() {
        return textureScale;
    }
}
