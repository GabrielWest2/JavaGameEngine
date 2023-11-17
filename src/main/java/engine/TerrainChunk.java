package engine;

import engine.error.InvalidTerrainChunkError;
import engine.rendering.Renderer;
import engine.rendering.model.ModelCreator;
import engine.rendering.model.TerrainModel;
import engine.util.MatrixBuilder;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.awt.*;

import static engine.TerrainManager.heightmap;

public class TerrainChunk {
    private TerrainModel model;

    private final int chunkX;

    private final int chunkY;

    public static final int terrainChunkWidth = 8;

    public static final int totalWidth = 1000;
    public static final int totalHeight = 6000;

    public final Matrix4f transformationMatrix;

    public TerrainChunk(int chunkX, int chunkY, int lod){
        transformationMatrix = MatrixBuilder.createTransformationMatrix(new Vector3f(chunkX * totalWidth, 0, chunkY * totalWidth), new Vector3f(0.0f), new Vector3f(1.0f));
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        try {
            createTerrain(chunkX, chunkY, lod);
        } catch (InvalidTerrainChunkError e) {
            e.printStackTrace();
        }
    }

    private void createTerrain(int chunkX, int chunkY, int LOD) throws InvalidTerrainChunkError {
        if(chunkX < 0 || chunkY < 0 || chunkX > terrainChunkWidth || chunkY > terrainChunkWidth){
            throw new InvalidTerrainChunkError("Invalid Chunk: " + chunkX+ ", " + chunkY);
        }

        int divisions = (int) (1024f/(float)LOD);

        float unitLength = (float)totalWidth/(float)divisions;

        int vertexWidth = divisions + 1;

        System.out.println(divisions);
        System.out.println(unitLength);
        System.out.println(vertexWidth);

        float[] vertices = new float[vertexWidth * vertexWidth * 3];
        float[] normals = new float[vertexWidth * vertexWidth * 3];
        float[] textureCoords = new float[vertexWidth * vertexWidth * 2];
        int[] triangles = new int[divisions * divisions * 6];

        int i = 0;
        int j = 0;
        for (int y = 0; y < vertexWidth; y++) {
            for (int x = 0; x < vertexWidth; x++, i += 3, j += 2) {
                vertices[i] = x * unitLength;
                vertices[i+ 1] = getTextureHeight((float) x / vertexWidth, (float) y / vertexWidth);
                vertices[i + 2] = y * unitLength;

                Vector3f normal = calculateNormal((float) x / vertexWidth, (float) y / vertexWidth, 1f / vertexWidth * heightmap.getWidth());
                normals[i] = normal.x;
                normals[i+ 1] = normal.y;
                normals[i + 2] = normal.z;

                textureCoords[j] = (float)x / (float) divisions;
                textureCoords[j + 1] = (float)y / (float) divisions;
            }
        }

        i = 0;
        for (int y = 0; y < vertexWidth - 1; y++) {
            for (int x = 0; x < vertexWidth - 1; x++, i += 6) {
                int coord1 = y * vertexWidth + x;
                triangles[i] = coord1 + vertexWidth;
                triangles[i + 1] = coord1 + 1;
                triangles[i + 2] = coord1;

                triangles[i + 3] = coord1 + vertexWidth;
                triangles[i + 4] = coord1 + vertexWidth + 1;
                triangles[i + 5] = coord1 + 1;
            }
        }

        model = ModelCreator.loadToTerrainVAO(vertices, triangles, textureCoords, normals, transformationMatrix);
    }


    public float getTextureHeight(float x, float y){
        int pixel = heightmap.getRGB((int) Math.round((0 + x) * heightmap.getWidth()), Math.round((y * heightmap.getHeight())));
        Color color = new Color(pixel, true);
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        int alpha = color.getAlpha();

        int height = red + (green << 8) + (blue << 16) + (alpha << 24);

        return ((height)/4294967296f)*totalHeight;

        //noise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        //return noise.GetNoise((x + 2323), (y + 43434)) * 50 + 1;
    }

    public Vector3f calculateNormal(float x, float y, float offset){
        offset = 4f / heightmap.getWidth();
        try {
            float heightL = getTextureHeight(x - offset, y);
            float heightR = getTextureHeight(x + offset, y);
            float heightD = getTextureHeight(x, y - offset);
            float heightU = getTextureHeight(x, y + offset);

            Vector3f normal = new Vector3f(heightL-heightR, 2f, heightD-heightU);
            normal = normal.normalize();
            return normal;
        }catch(Exception e){
            return new Vector3f(0, -1, 0);
        }


    }


    public TerrainModel getModel() {
        return this.model;
    }

    public void render() {
        Renderer.renderTerrain(model, 20f, 0.05f);
    }
}
