package engine.model;

import engine.texture.Texture;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.assimp.Assimp.aiProcess_PreTransformVertices;

public class ModelLoader {
    private static HashMap<String, Model> cachedModels = new HashMap<>();
    /*
    Model loading using ASSIMP
    adapted from
    https://ahbejarano.gitbook.io/lwjglgamedev/chapter-09
     */
    public static Model loadUsingAssimp(String path, Texture texture){
        if(cachedModels.containsKey(path)){
            System.out.println("Using cached model \'" + path + "\'");
            return cachedModels.get(path);
        }

        AIScene aiScene = aiImportFile("res/" + path, aiProcess_CalcTangentSpace | // calculate tangents and bitangents if possible
                aiProcess_JoinIdenticalVertices | // join identical vertices/ optimize indexing
                //aiProcess_ValidateDataStructure  | // perform a full validation of the loader's output
                aiProcess_Triangulate | // Ensure all verticies are triangulated (each 3 vertices are triangle)
                aiProcess_ConvertToLeftHanded | // convert everything to D3D left handed space (by default right-handed, for OpenGL)
                aiProcess_SortByPType | // ?
                aiProcess_ImproveCacheLocality | // improve the cache locality of the output vertices
                aiProcess_RemoveRedundantMaterials | // remove redundant materials
                aiProcess_FindDegenerates | // remove degenerated polygons from the import
                aiProcess_FindInvalidData | // detect invalid model data, such as invalid normal vectors
                aiProcess_GenUVCoords | // convert spherical, cylindrical, box and planar mapping to proper UVs
                aiProcess_TransformUVCoords | // preprocess UV transformations (scaling, translation ...)
                aiProcess_FindInstances | // search for instanced meshes and remove them by references to one master
                aiProcess_LimitBoneWeights | // limit bone weights to 4 per vertex
                aiProcess_OptimizeMeshes | // join small meshes, if possible;
                aiProcess_PreTransformVertices);

        assert aiScene != null;
        Model m = processMesh(AIMesh.create(Objects.requireNonNull(aiScene.mMeshes()).get(0)), texture);
        cachedModels.put(path, m);
        return m;
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
        // return data;
        float[] newData = new float[data.length];
        for(int i = 0; i < data.length; i+=2){
            newData[i] =   data[i];
            newData[i+1] = data[i+1];
        }
        return data;
    }

    private static float[] processVertices(AIMesh aiMesh) {
        AIVector3D.Buffer buffer = aiMesh.mVertices();
        float[] data = new float[buffer.remaining() * 3];
        int pos = 0;
        while (buffer.remaining() > 0) {
            AIVector3D vert = buffer.get();
            data[pos++] = vert.x();
            data[pos++] = vert.y();
            data[pos++] = vert.z();
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
}
