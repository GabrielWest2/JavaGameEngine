package engine;

import engine.model.ModelCreator;
import engine.model.WaterModel;
import org.joml.Vector3f;

public class WaterManger {
    public static int width = 1500;
    public static final int waterHeight = 0;
    private static WaterModel waterModel;

    public static void init() {
        generateWaterMesh();
    }

    public static void render(){
        Renderer.renderWater(waterModel, new Vector3f( -500, waterHeight, -500), 20.0f, 0.1f);
    }

    public static void generateWaterMesh() {
        float[] vertices = new float[] {
          0, 0, 0,  // Top Left
          1 * width, 0, 0,  // Top Right
          1 * width, 0, 1 * width,  // Bottom Right
          0, 0, 1 * width   // Bottom Left
        };

        int[] indices = new int[] {
                3, 1, 0,
                2, 1, 3
        };

        float[] uvCoords = new float[] {
                0, 0,
                1, 0,
                1, 1,
                0, 1
        };

        waterModel = ModelCreator.loadToWaterVAO(vertices, indices, uvCoords);
    }

}
