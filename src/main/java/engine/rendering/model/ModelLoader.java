package engine.rendering.model;

import engine.rendering.Material;
import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.lwjgl.assimp.Assimp.*;

public class ModelLoader {

    private static final HashMap<String, Model> cachedModels
            = new HashMap<>();

    /*
    Model loading using ASSIMP
    adapted from
    https://ahbejarano.gitbook.io/lwjglgamedev/chapter-09
     */
    public static List<Mesh> loadUsingAssimp(String path){
       // if(cachedModels.containsKey(path) &&
       //         cachedModels.get(path).getTexture().getFilepath()
       //                 .equals(texture.getFilepath())){
      //      System.out.println("Using cached model \'" + path + "\'");
      //      return cachedModels.get(path);
      // }

        List<Mesh> meshes = new ArrayList<>();

        AIScene aiScene = aiImportFile("res/" + path,
                aiProcess_CalcTangentSpace | // calculate tangents and
                        // bitangents if possible
                aiProcess_JoinIdenticalVertices | // join identical vertices/
                        // optimize indexing
                //aiProcess_ValidateDataStructure  | // perform a full
                        // validation of the loader's output
                aiProcess_Triangulate | // Ensure all verticies are
                        // triangulated (each 3 vertices are triangle)
                aiProcess_ConvertToLeftHanded | // convert everything
                        // to D3D left handed space
                        // (by default right-handed, for OpenGL)
                aiProcess_SortByPType | // ?
                aiProcess_ImproveCacheLocality | // improve the cache locality
                        // of the output vertices
                aiProcess_RemoveRedundantMaterials | // remove redundant
                        // materials
                aiProcess_FindDegenerates | // remove degenerated
                        // polygons from the import
                aiProcess_FindInvalidData | // detect invalid model data,
                        // such as invalid normal vectors
                aiProcess_GenUVCoords | // convert spherical, cylindrical,
                        // box and planar mapping to proper UVs
                aiProcess_TransformUVCoords | // preprocess UV transformations
                        // (scaling, translation ...)
                aiProcess_FindInstances | // search for instanced meshes
                        // and remove them by references to one master
                aiProcess_LimitBoneWeights | // limit bone weights to
                        // 4 per vertex
                aiProcess_OptimizeMeshes | // join small meshes, if possible;
                aiProcess_PreTransformVertices);

        assert aiScene != null;

        int numMaterials = aiScene.mNumMaterials();
        List<Material> materialList = new ArrayList<>();
        for (int i = 0; i < numMaterials; i++) {
            AIMaterial aiMaterial = AIMaterial.create(aiScene.mMaterials()
                    .get(i));
            materialList.add(processMaterial(aiMaterial, "models"));
        }

        int numMeshes = aiScene.mNumMeshes();
        PointerBuffer aiMeshes = aiScene.mMeshes();
        Material defaultMaterial = new Material();
        for (int i = 0; i < numMeshes; i++) {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            Mesh mesh = processMesh(aiMesh);
            int materialIdx = aiMesh.mMaterialIndex();
            Material material;
            if (materialIdx >= 0 && materialIdx < materialList.size()) {
                material = materialList.get(materialIdx);
            } else {
                material = defaultMaterial;
            }
            mesh.setMaterial(material);
            meshes.add(mesh);
        }
        System.out.println("Loaded " + meshes.size() + "meshes");
        for(Mesh m : meshes){
            System.out.println(m.getMaterial().getTexturePath());
        }
        return meshes;
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

    private static float[] processTexCoords(AIMesh aiMesh) {
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
            AIVector3D normal = buffer.get();
            data[pos++] = normal.x();
            data[pos++] = normal.y();
            data[pos++] = normal.z();
        }
        return data;
    }

    private static Material processMaterial(
            AIMaterial aiMaterial,
            String modelDir
    ) {
        Material material = new Material();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            AIColor4D color = AIColor4D.create();

            int result = aiGetMaterialColor(
                    aiMaterial,
                    AI_MATKEY_COLOR_AMBIENT,
                    aiTextureType_NONE,
                    0,
                    color);
            if (result == aiReturn_SUCCESS) {
                material.setAmbientColor(new Vector4f(
                        color.r(),
                        color.g(),
                        color.b(),
                        color.a())
                );
            }

            result = aiGetMaterialColor(
                    aiMaterial,
                    AI_MATKEY_COLOR_DIFFUSE,
                    aiTextureType_NONE,
                    0,
                    color);
            if (result == aiReturn_SUCCESS) {
                material.setDiffuseColor(new Vector4f(
                        color.r(),
                        color.g(),
                        color.b(),
                        color.a()));
                System.out.println("Diffuse: " + material.getDiffuseColor());
            }

            result = aiGetMaterialColor(
                    aiMaterial,
                    AI_MATKEY_COLOR_SPECULAR,
                    aiTextureType_NONE,
                    0,
                    color);
            if (result == aiReturn_SUCCESS) {
                material.setSpecularColor(new Vector4f(
                        color.r(),
                        color.g(),
                        color.b(),
                        color.a()));
            }

            float reflectance = 0.0f;
            float[] shininessFactor = new float[]{0.0f};
            int[] pMax = new int[]{1};
            result = aiGetMaterialFloatArray(
                    aiMaterial,
                    AI_MATKEY_SHININESS_STRENGTH,
                    aiTextureType_NONE,
                    0,
                    shininessFactor,
                    pMax);
            if (result != aiReturn_SUCCESS) {
                reflectance = shininessFactor[0];
            }
            material.setReflectance(reflectance);

            AIString aiTexturePath = AIString.calloc(stack);
            aiGetMaterialTexture(
                    aiMaterial,
                    aiTextureType_DIFFUSE,
                    0,
                    aiTexturePath,
                    (IntBuffer) null,
                    null,
                    null,
                    null,
                    null,
                    null);
            String texturePath = aiTexturePath.dataString();
            if (texturePath.length() > 0) {
                material.setTexturePath(modelDir + File.separator +
                        new File(texturePath).getName());
                material.setDiffuseColor(Material.DEFAULT_COLOR);
            }

            return material;
        }
    }

    private static Mesh processMesh(AIMesh aiMesh) {
        float[] vertices = processVertices(aiMesh);
        float[] textCoords = processTexCoords(aiMesh);
        int[] indices = processIndices(aiMesh);
        float[] normals = processNormals(aiMesh);

        // Texture coordinates may not have been populated.
        // We need at least the empty slots
        if (textCoords.length == 0) {
            int numElements = (vertices.length / 3) * 2;
            textCoords = new float[numElements];
        }
        return ModelCreator.loadToTexturedVAO(
                vertices,
                indices,
                textCoords,
                normals
        );
    }
}
