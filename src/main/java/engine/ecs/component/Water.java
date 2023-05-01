package engine.ecs.component;

import engine.GameEngine;
import engine.ecs.Component;

public class Water extends Component {
    public int width = 1000;

    @Override
    public void onAdded() {
        generateWaterMesh();
    }

    @Override
    public void onVariableChanged() {
        generateWaterMesh();
    }

    public void generateWaterMesh() {
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





        if (entity.getComponent(ModelRenderer.class) == null) {
            entity.addComponent(new ModelRenderer());
        }

        entity.getComponent(ModelRenderer.class).setModel(GameEngine.getInstance().modelCreator.loadToWaterVAO(vertices, indices, uvCoords));
    }

}
