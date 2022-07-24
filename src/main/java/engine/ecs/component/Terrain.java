package engine.ecs.component;

import editor.Range;
import engine.model.OBJLoader;
import engine.texture.TextureLoader;
import engine.util.Color;
import engine.util.FastNoiseLite;
import org.joml.Vector3f;

/**
 * @author gabed
 * @Date 7/23/2022
 */
public class Terrain extends Component {
    @Range(min = 1)
    public static int TERRAIN_SIZE = 80;
    @Range(min = 0.05f, max = 1.0f)
    public static float UNIT_SIZE = 0.5f;
    public static float offsetX = 0, offsetY = 0, scale = 10, amplitude = 2;
    public static Color color1 = new Color(255f / 255f, 217f / 255f, 66f / 255f), color2 = new Color(102f / 255f, 204f / 255f, 71f / 255f);

    @Override
    public void onAdded() {
        generateTerrain();
    }

    @Override
    public void onVariableChanged() {
        generateTerrain();
    }

    public void generateTerrain() {
        FastNoiseLite noise = new FastNoiseLite();
        noise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        Vector3f[] verticesVec3 = new Vector3f[(TERRAIN_SIZE + 1) * (TERRAIN_SIZE + 1)];
        Color[] colorsRGB = new Color[(TERRAIN_SIZE + 1) * (TERRAIN_SIZE + 1)];

        for (int i = 0, y = 0; y <= TERRAIN_SIZE; y++) {
            for (int x = 0; x <= TERRAIN_SIZE; x++, i++) {

                float height = noise.GetNoise((x + offsetX) * scale, (y + offsetY) * scale);
                verticesVec3[i] = new Vector3f(x * UNIT_SIZE, height * amplitude, y * UNIT_SIZE);
                colorsRGB[i] = height < 0 ? color1 : color2;
            }
        }

        float[] colors = new float[verticesVec3.length * 3];
        float[] vertices = new float[verticesVec3.length * 3];
        float[] textureCoords = new float[verticesVec3.length * 3];
        int[] triangles = new int[TERRAIN_SIZE * TERRAIN_SIZE * 6];
        float[] normals = new float[TERRAIN_SIZE * TERRAIN_SIZE * 6];

        for (int i = 0; i < triangles.length / 3; i += 3) {
            Vector3f p1 = verticesVec3[triangles[i]];
            Vector3f p2 = verticesVec3[triangles[i + 1]];
            Vector3f p3 = verticesVec3[triangles[i + 2]];
            Vector3f v1 = p2.sub(p1);
            Vector3f v2 = p3.sub(p1);
            Vector3f normal = v1.cross(v2);
            normals[i] = normal.x;
            normals[i + 1] = normal.y;
            normals[i + 2] = normal.z;
        }
        for (int i = 0; i < normals.length; i++) {
            System.out.println(normals[i]);
        }


        int i = 0;
        for (Vector3f vert : verticesVec3) {

            vertices[i] = vert.x;
            vertices[i + 1] = vert.y;
            vertices[i + 2] = vert.z;
            colors[i] = colorsRGB[i / 3].r;
            colors[i + 1] = colorsRGB[i / 3].g;
            colors[i + 2] = colorsRGB[i / 3].b;
            i += 3;
        }
        for (int ti = 0, vi = 0, y = 0; y < TERRAIN_SIZE; y++, vi++) {
            for (int x = 0; x < TERRAIN_SIZE; x++, ti += 6, vi++) {
                triangles[ti] = vi;
                triangles[ti + 3] = triangles[ti + 2] = vi + 1;
                triangles[ti + 4] = triangles[ti + 1] = vi + TERRAIN_SIZE + 1;
                triangles[ti + 5] = vi + TERRAIN_SIZE + 2;
            }
        }


        if (entity.getComponent(ModelRenderer.class) == null) {
            entity.addComponent(new ModelRenderer());
        }

        entity.getComponent(ModelRenderer.class).setModel(OBJLoader.loadTexturedOBJ("Link.obj", TextureLoader.getTexture("white.png")));//GameEngine.getInstance().modelCreator.loadToTerrainVAO(vertices, triangles, textureCoords, normals, GameEngine.grass));
    }
}
