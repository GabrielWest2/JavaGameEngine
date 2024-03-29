package engine.rendering.model;

import engine.TerrainManager;
import engine.rendering.texture.Texture;
import engine.rendering.texture.TextureLoader;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengles.GLES20.GL_ELEMENT_ARRAY_BUFFER;

public class ModelCreator {

    private static final float SIZE = 825f;

    private static final List<Integer> vaos = new ArrayList<>();

    private static final List<Integer> vbos = new ArrayList<>();

    private static final float[] VERTICES = {
            -SIZE, SIZE, -SIZE,
            -SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, SIZE, -SIZE,
            -SIZE, SIZE, -SIZE,

            -SIZE, -SIZE, SIZE,
            -SIZE, -SIZE, -SIZE,
            -SIZE, SIZE, -SIZE,
            -SIZE, SIZE, -SIZE,
            -SIZE, SIZE, SIZE,
            -SIZE, -SIZE, SIZE,

            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, SIZE,
            SIZE, SIZE, SIZE,
            SIZE, SIZE, SIZE,
            SIZE, SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,

            -SIZE, -SIZE, SIZE,
            -SIZE, SIZE, SIZE,
            SIZE, SIZE, SIZE,
            SIZE, SIZE, SIZE,
            SIZE, -SIZE, SIZE,
            -SIZE, -SIZE, SIZE,

            -SIZE, SIZE, -SIZE,
            SIZE, SIZE, -SIZE,
            SIZE, SIZE, SIZE,
            SIZE, SIZE, SIZE,
            -SIZE, SIZE, SIZE,
            -SIZE, SIZE, -SIZE,

            -SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE, SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE, SIZE,
            SIZE, -SIZE, SIZE
    };


    public static Mesh loadToVAO(float[] positions, int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, positions);
        unbindVAO();
        return new Mesh(vaoID, positions.length);
    }

    public static Mesh loadToVAO(float[] positions, int dimensions) {
        int vaoID = createVAO();
        storeDataInAttributeList(0, dimensions, positions);
        unbindVAO();
        return new Mesh(vaoID, positions.length / dimensions);
    }

    public static Mesh loadToVAO(FloatBuffer positions, IntBuffer indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, positions);
        unbindVAO();
        return new Mesh(vaoID, positions.capacity());
    }

    public static SkyboxModel createSkyboxModel(String[] TEXTURE_FILES) {
        Mesh m = loadToVAO(VERTICES, 3);
        return new SkyboxModel(
                m.getVaoID(),
                m.getVertexCount(),
                TextureLoader.loadCubeMap(TEXTURE_FILES)
        );
    }

    public static TexturedModel loadToTexturedVAO(
            float[] positions,
            int[] indices,
            float[] textureCoords,
            float[] normals,
            Texture texture
    ) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 2, textureCoords);
        storeDataInAttributeList(2, 3, normals);
        unbindVAO();
        return new TexturedModel(vaoID, positions.length, texture);
    }

    public static Mesh loadToTexturedVAO(
              float[] positions,
              int[] indices,
              float[] textureCoords,
              float[] normals
    ) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 2, textureCoords);
        storeDataInAttributeList(2, 3, normals);
        unbindVAO();
        return new Mesh(vaoID, positions.length);
    }

    public static WaterModel loadToWaterVAO(
            float[] positions,
            int[] indices,
            float[] textureCoords
    ) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 2, textureCoords);
        unbindVAO();
        return new WaterModel(vaoID, positions.length);
    }

    public static TerrainModel loadToTerrainVAO(
            float[] positions,
            int[] indices,
            float[] textureCoords,
            float[] normals,
            Matrix4f transformation
    ) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 2, textureCoords);
        storeDataInAttributeList(2, 3, normals);
        unbindVAO();

        return new TerrainModel(
                vaoID,
                positions.length * 3,
                TerrainManager.splatmap,
                TerrainManager.t1,
                TerrainManager.t2,
                TerrainManager.t3,
                TerrainManager.t4,
                transformation
        );
    }


    public static TexturedModel loadToTexturedVAO(
            FloatBuffer positions,
            IntBuffer indices,
            FloatBuffer textureCoords,
            FloatBuffer normals,
            Texture texture
    ) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 2, textureCoords);
        storeDataInAttributeList(2, 3, normals);
        unbindVAO();
        return new TexturedModel(vaoID, positions.capacity(), texture);
    }


    public static Mesh loadToVAO(
            FloatBuffer positions,
            IntBuffer indices,
            FloatBuffer textureCoords,
            FloatBuffer normals
    ) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 2, textureCoords);
        storeDataInAttributeList(2, 3, normals);
        unbindVAO();
        return new Mesh(vaoID, positions.capacity());
    }

    private static int createVAO() {
        int vaoID = GL30.glGenVertexArrays();
        vaos.add(vaoID);
        GL30.glBindVertexArray(vaoID);
        return vaoID;
    }

    private static int storeDataInAttributeList(
            int attributeNumber,
            int coordinateSize,
            float[] data
    ) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(
                attributeNumber,
                coordinateSize,
                GL11.GL_FLOAT,
                false,
                0,
                0
        );
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        return vboID;
    }

    private static void storeDataInAttributeList(
            int attributeNumber,
            int coordinateSize,
            FloatBuffer data
    ) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(
                attributeNumber,
                coordinateSize,
                GL11.GL_FLOAT,
                false,
                0,
                0
        );
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

    }


    public static void cleanUp() {
        for (Integer vao : vaos) {
            GL30.glDeleteVertexArrays(vao);
        }
        for (Integer vbo : vbos) {
            GL15.glDeleteBuffers(vbo);
        }
    }

    private static void unbindVAO() {
        GL30.glBindVertexArray(0);
    }

    private static int bindIndicesBuffer(int[] indices) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboID);
        IntBuffer buffer = storeDataInIntBuffer(indices);
        GL15.glBufferData(
                GL15.GL_ELEMENT_ARRAY_BUFFER,
                buffer,
                GL15.GL_STATIC_DRAW
        );
        return vboID;
    }

    private static int bindIndicesBuffer(IntBuffer buffer) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboID);
        GL15.glBufferData(
                GL15.GL_ELEMENT_ARRAY_BUFFER,
                buffer,
                GL15.GL_STATIC_DRAW
        );
        return vboID;
    }


    public static IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    public static FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }
}
