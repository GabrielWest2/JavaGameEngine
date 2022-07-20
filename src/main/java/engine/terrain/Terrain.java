package engine.terrain;

import engine.GameEngine;
import engine.model.TerrianModel;
import engine.util.FastNoiseLite;
import org.joml.Vector3f;

public class Terrain {


    public static int TERRAIN_SIZE = 80;
    public static float UNIT_SIZE = 0.5f;
    public static float offsetX = 0, offsetY = 0, scale = 10, amplitude = 2;
    public static Vector3f color1 = new Vector3f(255f / 255f, 217f / 255f, 66f / 255f), color2 = new Vector3f(102f / 255f, 204f / 255f, 71f / 255f);
    private TerrianModel model;

    public Terrain() {
        this.model = generateTerrain();
    }

    public TerrianModel getModel() {
        return model;
    }

    public void UpdateModel() {
        this.model = generateTerrain();
    }


    private TerrianModel generateTerrain() {
        FastNoiseLite noise = new FastNoiseLite();
        noise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        Vector3f[] verticesVec3 = new Vector3f[(TERRAIN_SIZE + 1) * (TERRAIN_SIZE + 1)];
        Vector3f[] colorsRGB = new Vector3f[(TERRAIN_SIZE + 1) * (TERRAIN_SIZE + 1)];
        for (int i = 0, y = 0; y <= TERRAIN_SIZE; y++) {
            for (int x = 0; x <= TERRAIN_SIZE; x++, i++) {

                float height = noise.GetNoise((x + offsetX) * scale, (y + offsetY) * scale);
                verticesVec3[i] = new Vector3f(x * UNIT_SIZE, height * amplitude, y * UNIT_SIZE);
                colorsRGB[i] = height < 0 ? color1 : color2;
            }
        }
        float[] colors = new float[verticesVec3.length * 3];
        float[] vertices = new float[verticesVec3.length * 3];
        int i = 0;
        for (Vector3f vert : verticesVec3) {

            vertices[i] = vert.x;
            vertices[i + 1] = vert.y;
            vertices[i + 2] = vert.z;
            colors[i] = colorsRGB[i / 3].x;
            colors[i + 1] = colorsRGB[i / 3].y;
            colors[i + 2] = colorsRGB[i / 3].z;
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
