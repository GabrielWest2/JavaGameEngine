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
    /*
    private TerrianModel generateTerrain(){


        int count = VERTEX_COUNT * VERTEX_COUNT;
        float[] vertices = new float[count * 3];
        float[] colors = new float[count * 3];

        int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
        int vertexPointer = 0;
        for(int i=0;i<VERTEX_COUNT;i++){
            for(int j=0;j<VERTEX_COUNT;j++){
                vertices[vertexPointer*3] = (float)j/((float)VERTEX_COUNT - 1) * SIZE;
                //vertices[vertexPointer*3+1] = noise.GetNoise(i* 5, j * 5) * 5;
                vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * SIZE;

                /*if(vertices[vertexPointer*3+1] > 1) {

                    colors[vertexPointer * 3] = 0f;
                    colors[vertexPointer * 3 + 1] = 0f;
                    colors[vertexPointer * 3 + 2] = 0f;
                }else{
                    colors[vertexPointer * 3] = 1;
                    colors[vertexPointer * 3 + 1] = 1;
                    colors[vertexPointer * 3 + 2] = 1;
                }
                vertexPointer+=3;
            }
        }
        int pointer = 0;
        for(int gz=0;gz<VERTEX_COUNT-1;gz++){
            for(int gx=0;gx<VERTEX_COUNT-1;gx++){
                int topLeft = (gz*VERTEX_COUNT)+gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }

        System.out.println("TERRAIN MESH HAS " + indices.length + " INDICES. IT SHOULD HAVE " + (7 * 7 * 6));
        return GameEngine.modelCreator.loadToTerrainVAO(vertices, indices, colors);
    }
    */


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
