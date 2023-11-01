package engine;

import engine.ecs.component.Transform;
import engine.model.ModelCreator;
import engine.model.TerrainModel;
import engine.util.FastNoiseLite;
import org.joml.Math;
import org.joml.Vector3f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static engine.TerrainManager.noise;

public class TerrainChunk {
    private TerrainModel model;
    private final int chunkX, chunkY;

    public static final int terrainChunkWidth = 8;
    public static final int totalWidth = 1000;
    public static final float totalHeight = 600;
    public static final int heightMapWidth = 513;
    public static int terrainWidth = heightMapWidth/terrainChunkWidth;
    public static float unitSize = ((float)totalWidth/(float)terrainChunkWidth)/((float)terrainWidth);

    private static List<Transform> grassPositions = new ArrayList<>();
    private static List<Transform> flowerPositions = new ArrayList<>();

    public TerrainChunk(int chunkX, int chunkY, int lod){
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        createTerrain(chunkX, chunkY, lod*2);
        generateDetails();
    }

    private void createTerrain(int chunkX, int chunkY, int LOD) {
        try {
            if(chunkX < 0 || chunkY < 0 || chunkX > terrainChunkWidth || chunkY > terrainChunkWidth)
                throw new Exception("Invalid Chunk: " + chunkX+ ", " + chunkY);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        float[] vertices = new float[(((terrainWidth/LOD) + 1) * ((terrainWidth/LOD) + 1)) * 3];
        float[] textureCoords = new float[(((terrainWidth/LOD) + 1) * ((terrainWidth/LOD) + 1)) * 3];
        int[] triangles = new int[(terrainWidth/LOD) * (terrainWidth/LOD) * 6];
        float[] normals = new float[(terrainWidth/LOD) * (terrainWidth/LOD) * 6];


        int f = 0;
        int t = 0;
        int i = 0;
        for (int y = chunkY*(terrainWidth/LOD); y <= (terrainWidth/LOD) + chunkY*(terrainWidth/LOD); y++) {
            for (int x = chunkX*(terrainWidth/LOD); x <= (terrainWidth/LOD) + chunkX*(terrainWidth/LOD); x++, i++) {
                float height = getTextureHeight(x*LOD, y*LOD);
                vertices[f] = x * unitSize*LOD;
                vertices[f + 1] = height;
                vertices[f + 2] = y * unitSize*LOD;

                Vector3f normal = calculateNormal((int) x*LOD, (int)y*LOD);
                normals[f] = normal.x;
                normals[f + 1] = normal.y;
                normals[f + 2] = normal.z;

                textureCoords[t] = 1-(y * (unitSize*LOD) / totalWidth);
                textureCoords[t+1] = (x * (unitSize*LOD) / totalWidth);
                f += 3;
                t += 2;
            }
        }
        for (int ti = 0, vi = 0, y = 0; y < (terrainWidth/LOD); y++, vi++) {
            for (int x = 0; x < (terrainWidth/LOD); x++, ti += 6, vi++) {
                triangles[ti] = vi;
                triangles[ti + 3] = triangles[ti + 2] = vi + 1;
                triangles[ti + 4] = triangles[ti + 1] = vi + (terrainWidth/LOD) + 1;
                triangles[ti + 5] = vi + (terrainWidth/LOD) + 2;
            }
        }

        model = ModelCreator.loadToTerrainVAO(vertices, triangles, textureCoords, normals);
    }

    private void generateDetails(){
        for (int y = chunkY*terrainWidth; y <= terrainWidth + chunkY*terrainWidth; y+=1) {
            for (int x = chunkX*terrainWidth; x <= terrainWidth + chunkX*terrainWidth; x+=1) {
                float newX = (float) (x + ((Math.random()-0.5f) * 0.75f));
                float newY = (float) (y + ((Math.random()-0.5f) * 0.75f));
                float scale = (float) (Math.random() * 0.1f) + 0.3f;

                Transform t = new Transform(
                        new Vector3f(newX * unitSize, getTextureHeight(x, y), newY * unitSize),
                        new Vector3f(0, (float) (Math.random()*360f), 0),
                        new Vector3f(scale, scale, scale));
                grassPositions.add(t);
            }
        }
    }
    public float getTextureHeight(int x, int y){
        //return 10;
        /*
        x = Math.clamp(x, 1, TerrainManager.heightMap.getWidth()-2);
        y = TerrainManager.heightMap.getHeight()-Math.clamp(y, 1, TerrainManager.heightMap.getHeight()-1);
        try {
            int pixel = TerrainManager.heightMap.getRGB(x, y);
            Color color = new Color(pixel, true);
            int red = color.getRed();
            int green = color.getGreen();
            int blue = color.getBlue();
            int alpha = color.getAlpha();

            int height = red + (green << 8) + (blue << 16) + (alpha << 24);

            return ((height)/4294967296f)*totalHeight-24;
        }catch(Exception e){
           // System.out.println(x + "   " + y);
           // e.printStackTrace();
        }

        return 0;
        */

        noise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        return noise.GetNoise((x + 2323) * 1, (y + 43434) * 1) * 50 + 1;
        //noise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        //return noise.GetNoise((x + offsetX) * scale, (y + offsetY) * scale) * amplitude + 1;
    }

    public Vector3f calculateNormal(int x, int y){
        float heightL = getTextureHeight(x - 1, y);
        float heightR = getTextureHeight(x + 1, y);
        float heightD = getTextureHeight(x, y - 1);
        float heightU = getTextureHeight(x, y + 1);

        Vector3f normal = new Vector3f(heightL-heightR, 2f, heightD-heightU);
        normal = normal.normalize();
        return normal;
    }

    public Vector3f calculateObjectRotation(int x, int y){
        Vector3f normal = calculateNormal(x, y);
        float theta = Math.acos(normal.y);
        float phi = (float) (Math.PI-(normal.z / Math.abs(normal.z)) * Math.acos(normal.x / Math.sqrt(normal.x * normal.x + normal.z * normal.z)));
        return new Vector3f(0, (float) Math.toDegrees(phi), (float) Math.toDegrees(theta));
    }

    public TerrainModel getModel() {
        return this.model;
    }

    public void render() {
        Renderer.renderTerrain(model, 20f, 0.05f);
        //Renderer.renderTerrainDetails(grassPositions, GameEngine.grassModel);
    }
}
