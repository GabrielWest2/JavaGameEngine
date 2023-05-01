package engine.ecs.component;

import editor.CustomHudName;
import editor.Range;
import engine.GameEngine;
import engine.ecs.Component;
import engine.model.TerrianModel;
import engine.util.FastNoiseLite;
import imgui.ImGui;
import org.joml.Math;
import org.joml.Vector3f;

/**
 * @author gabed
 * @Date 7/23/2022
 */
public class Terrain extends Component {
    @Range(min = 1) @CustomHudName(displayName = "Width")
    public int terrainWidth = 40;

    @Range(min = 0.05f, max = 1.0f) @CustomHudName(displayName = "Resolution")
    public float unitSize = 1f;

    @Range(min = 0f, max = 50f) @CustomHudName(displayName = "Texture Scale")
    public float textureScale = 1f;

    @CustomHudName(displayName = "Offset X")
    public float offsetX = 0;
    @CustomHudName(displayName = "Offset Y")
    public float offsetY = 0;
    @CustomHudName(displayName = "Scale")
    public float scale = 10;
    @CustomHudName(displayName = "Amplitude")
    public float amplitude = 2;

    private final FastNoiseLite noise = new FastNoiseLite();
    private TerrianModel terrainModel;

    @Override
    public void onAdded() {
        generateTerrain();
    }


    @Override
    public void GUI(){
        super.GUI();
        if(ImGui.button("Regenerate Terrain"))
            generateTerrain();
    }

    public void generateTerrain() {
        float[] vertices = new float[((terrainWidth + 1) * (terrainWidth + 1)) * 3];
        float[] textureCoords = new float[((terrainWidth + 1) * (terrainWidth + 1)) * 3];
        int[] triangles = new int[terrainWidth * terrainWidth * 6];
        float[] normals = new float[terrainWidth * terrainWidth * 6];


        int f = 0;
        int t = 0;
        for (int i = 0, y = 0; y <= terrainWidth; y++) {
            for (int x = 0; x <= terrainWidth; x++, i++) {
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


        if (entity.getComponent(ModelRenderer.class) == null) {
            entity.addComponent(new ModelRenderer());
        }

        terrainModel = GameEngine.getInstance().modelCreator.loadToTerrainVAO(vertices, triangles, textureCoords, normals, GameEngine.grass);
        entity.getComponent(ModelRenderer.class).setModel(terrainModel);
    }

    public Vector3f calculateNormal(float x, float y){
        float heightL = calculateHeight(x - 1, y);
        float heightR = calculateHeight(x + 1, y);
        float heightD = calculateHeight(x, y - 1);
        float heightU = calculateHeight(x, y + 1);

        Vector3f normal = new Vector3f(heightL-heightR, 2f, heightD-heightU);
        normal = normal.normalize();
        return normal;
    }

    public float calculateHeight(float x, float y){
        noise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        return noise.GetNoise((x + offsetX) * scale, (y + offsetY) * scale) * amplitude;
    }

    public Vector3f calculateObjectRotation(float x, float y){
        Vector3f normal = calculateNormal(x, y);
        float theta = Math.acos(normal.y);
        float phi = (float) (Math.PI-(normal.z / Math.abs(normal.z)) * Math.acos(normal.x / Math.sqrt(normal.x * normal.x + normal.z * normal.z)));
        return new Vector3f(0, (float) Math.toDegrees(phi), (float) Math.toDegrees(theta));
    }


    public float getTextureScale() {
        return textureScale;
    }
}
