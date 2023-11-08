package engine.model;

import de.javagl.obj.*;
import engine.GameEngine;
import engine.texture.Texture;
import org.lwjgl.assimp.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.assimp.Assimp.aiImportFile;


public class OBJLoader {
    /*
    Model loading using ASSIMP
    adapted from
    https://ahbejarano.gitbook.io/lwjglgamedev/chapter-09
     */
    public static Model loadUsingAssimp(String path, Texture texture){
        AIScene aiScene = aiImportFile("res/" + path, 0);

        assert aiScene != null;
        return processMesh(AIMesh.create(Objects.requireNonNull(aiScene.mMeshes()).get(0)), texture);
    }
    private static int[] processIndices(AIMesh aiMesh) {
        List<Integer> indices = new ArrayList<>();
        int numFaces = aiMesh.mNumFaces();
        AIFace.Buffer aiFaces = aiMesh.mFaces();
        for (int i = 0; i < numFaces; i++) {
            AIFace aiFace = aiFaces.get(i);
            IntBuffer buffer = aiFace.mIndices();
            while (buffer.remaining() > 0) {
                indices.add(buffer.get());
            }
        }
        return indices.stream().mapToInt(Integer::intValue).toArray();
    }

    private static float[] processTextCoords(AIMesh aiMesh) {
        AIVector3D.Buffer buffer = aiMesh.mTextureCoords(0);
        if (buffer == null) {
            return new float[]{};
        }
        float[] data = new float[buffer.remaining() * 2];
        int pos = 0;
        while (buffer.remaining() > 0) {
            AIVector3D textCoord = buffer.get();
            data[pos++] = textCoord.x();
            data[pos++] = 1 - textCoord.y();
        }
        return data;
    }

    private static float[] processVertices(AIMesh aiMesh) {
        AIVector3D.Buffer buffer = aiMesh.mVertices();
        float[] data = new float[buffer.remaining() * 3];
        int pos = 0;
        while (buffer.remaining() > 0) {
            AIVector3D textCoord = buffer.get();
            data[pos++] = textCoord.x();
            data[pos++] = textCoord.y();
            data[pos++] = textCoord.z();
        }
        return data;
    }

    private static float[] processNormals(AIMesh aiMesh) {
        AIVector3D.Buffer buffer = aiMesh.mNormals();
        float[] data = new float[buffer.remaining() * 3];
        int pos = 0;
        while (buffer.remaining() > 0) {
            AIVector3D textCoord = buffer.get();
            data[pos++] = textCoord.x();
            data[pos++] = textCoord.y();
            data[pos++] = textCoord.z();
        }
        return data;
    }
    private static Model processMesh(AIMesh aiMesh, Texture texture) {
        float[] vertices = processVertices(aiMesh);
        float[] textCoords = processTextCoords(aiMesh);
        int[] indices = processIndices(aiMesh);
        float[] normals = processNormals(aiMesh);

        // Texture coordinates may not have been populated. We need at least the empty slots
        if (textCoords.length == 0) {
            int numElements = (vertices.length / 3) * 2;
            textCoords = new float[numElements];
        }

        return ModelCreator.loadToTexturedVAO(vertices, indices, textCoords, normals, texture);
    }


    public static Model loadTexturedOBJ(String path, Texture texture) {
        try {
            InputStream inputStream = new FileInputStream("res/" + path);
            Obj obj = ObjUtils.convertToRenderable(
                    ObjReader.read(inputStream));

            Map<String, Obj> materialMeshes = ObjSplitting.splitByMaterialGroups(obj);

            if(materialMeshes.size() != 0) {
                HashMap<String, TexturedModel> models = new HashMap<>();
                HashMap<String, Texture> textures = new HashMap<>();
                for (int i = 0; i < materialMeshes.size(); i++) {
                    Obj materialMesh = materialMeshes.values().stream().toList().get(i);
                    String name = materialMeshes.keySet().stream().toList().get(i);
                    TexturedModel model;

                    model = ModelCreator.loadToTexturedVAO(ObjData.getVertices(materialMesh), ObjData.getFaceVertexIndices(materialMesh), ObjData.getTexCoords(materialMesh, 2), ObjData.getNormals(materialMesh), texture);


                    models.put(name, model);
                    //TODO set default texture?
                    textures.put(name, null);
                }

                MultiTexturedModel model = new MultiTexturedModel(models, textures);
                return model;
            }else {
                TexturedModel model = ModelCreator.loadToTexturedVAO(ObjData.getVertices(obj), ObjData.getFaceVertexIndices(obj), ObjData.getTexCoords(obj, 2), ObjData.getNormals(obj), texture);
                return model;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static TexturedModel loadSimpleTexturedOBJ(String path, Texture texture) {
        try {
            InputStream inputStream = new FileInputStream("res/" + path);
            Obj obj = ObjUtils.convertToRenderable(
                    ObjReader.read(inputStream));

            Map<String, Obj> materialMeshes = ObjSplitting.splitByMaterialGroups(obj);


            TexturedModel model = ModelCreator.loadToTexturedVAO(ObjData.getVertices(obj), ObjData.getFaceVertexIndices(obj), ObjData.getTexCoords(obj, 2), ObjData.getNormals(obj), texture);
            return model;



        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static VegetationModel loadVegetationTexturedOBJ(String path, Texture texture) {
        try {
            InputStream inputStream = new FileInputStream("res/" + path);
            Obj obj = ObjUtils.convertToRenderable(ObjReader.read(inputStream));
            VegetationModel model = ModelCreator.loadToVegetationVAO(ObjData.getVertices(obj), ObjData.getFaceVertexIndices(obj), ObjData.getTexCoords(obj, 2), ObjData.getNormals(obj), texture);
            return model;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static Model loadOBJWithoutMTL(String path) {
        try {
            InputStream inputStream = new FileInputStream("res/" + path);
             Obj obj = ObjUtils.convertToRenderable(ObjReader.read(inputStream));

            Model model = ModelCreator.loadToVAO(ObjData.getVertices(obj), ObjData.getFaceVertexIndices(obj), ObjData.getTexCoords(obj, 2), ObjData.getNormals(obj));
            return model;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
