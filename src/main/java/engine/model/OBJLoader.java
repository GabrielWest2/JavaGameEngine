package engine.model;

import de.javagl.obj.*;
import engine.GameEngine;
import engine.texture.Texture;
import engine.texture.TextureLoader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class OBJLoader {


    public static Model loadTexturedOBJ(String path, Texture texture) {
        try {
            InputStream inputStream = new FileInputStream("res/" + path);
            Obj obj = ObjUtils.convertToRenderable(
                    ObjReader.read(inputStream));

            Map<String, Obj> materialMeshes = ObjSplitting.splitByMaterialGroups(obj);
            System.out.println("Checking meshes numbers : " + materialMeshes.size());


            if(materialMeshes.size() != 0) {
                HashMap<String, TexturedModel> models = new HashMap<>();
                HashMap<String, Texture> textures = new HashMap<>();
                for (int i = 0; i < materialMeshes.size(); i++) {
                    Obj materialMesh = materialMeshes.values().stream().toList().get(i);
                    TexturedModel model = GameEngine.getInstance().modelCreator.loadToTexturedVAO(ObjData.getVertices(materialMesh), ObjData.getFaceVertexIndices(materialMesh), ObjData.getTexCoords(materialMesh, 2), ObjData.getNormals(materialMesh), texture);
                    String name = materialMeshes.keySet().stream().toList().get(i);
                    System.out.println(name);
                    models.put(name, model);
                    //TODO set default texture?
                    textures.put(name, null);
                }
                MultiTexturedModel model = new MultiTexturedModel(models, textures);
                return model;
            }else {
                TexturedModel model = GameEngine.getInstance().modelCreator.loadToTexturedVAO(ObjData.getVertices(obj), ObjData.getFaceVertexIndices(obj), ObjData.getTexCoords(obj, 2), ObjData.getNormals(obj), texture);
                return model;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static float[] getFloats(Object[] values) {
        float[] result = new float[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = (float) values[i];
        }
        return result;
    }

    public static int[] getInts(Object[] values) {
        int[] result = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = (int) values[i];
        }
        return result;
    }

}
