package engine.terrain;

import engine.GameEngine;
import engine.model.TerrianModel;
import engine.util.FastNoiseLite;
import org.joml.Vector3f;

import java.awt.*;
import java.util.Random;

public class Terrain {


    private static final int TERRAIN_SIZE = 80;
    private static final float UNIT_SIZE = 0.5f;

    private final TerrianModel model;

    public Terrain() {
        this.model = generateTerrain();
    }

    public TerrianModel getModel() {
        return model;
    }

    private TerrianModel generateTerrain() {
        FastNoiseLite noise = new FastNoiseLite();
        noise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        Random random = new Random();
        Vector3f[] verticesVec3 = new Vector3f[(TERRAIN_SIZE + 1) * (TERRAIN_SIZE + 1)];
        Color[] colorsRGB = new Color[(TERRAIN_SIZE + 1) * (TERRAIN_SIZE + 1)];
        for (int i = 0, y = 0; y <= TERRAIN_SIZE; y++) {
            for (int x = 0; x <= TERRAIN_SIZE; x++, i++) {

                float height = noise.GetNoise(x * 10f, y * 10f);
                verticesVec3[i] = new Vector3f(x * UNIT_SIZE, height * 5, y * UNIT_SIZE);


                colorsRGB[i] = height < 0 ? new Color(255, 217, 66) : new Color(102, 204, 71);
            }
        }
        float[] colors = new float[verticesVec3.length * 3];
        float[] vertices = new float[verticesVec3.length * 3];
        int i = 0;
        for (Vector3f vert : verticesVec3) {
            vertices[i] = vert.x;
            vertices[i + 1] = vert.y;
            vertices[i + 2] = vert.z;
            colors[i] = colorsRGB[i / 3].getRed() / 256f;
            colors[i + 1] = colorsRGB[i / 3].getGreen() / 256f;
            colors[i + 2] = colorsRGB[i / 3].getBlue() / 256f;
            i += 3;
        }

        int[] triangles = new int[TERRAIN_SIZE * TERRAIN_SIZE * 6];
        for (int ti = 0, vi = 0, y = 0; y < TERRAIN_SIZE; y++, vi++) {
            for (int x = 0; x < TERRAIN_SIZE; x++, ti += 6, vi++) {
                triangles[ti] = vi;
                triangles[ti + 3] = triangles[ti + 2] = vi + 1;
                triangles[ti + 4] = triangles[ti + 1] = vi + TERRAIN_SIZE + 1;
                triangles[ti + 5] = vi + TERRAIN_SIZE + 2;
            }
        }

        return GameEngine.modelCreator.loadToTerrainVAO(vertices, triangles, colors);
    }
}
