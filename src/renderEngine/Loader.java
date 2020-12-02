package renderEngine;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import models.InstancedRawModel;
import models.RawModel;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import textures.TextureData;
import util.math.Vector3f;

public class Loader {

    private static Loader instance;

    private final List<Integer> vaos     = new ArrayList<>();
    private final List<Integer> vbos     = new ArrayList<>();
    private final List<Integer> textures = new ArrayList<>();

    public static Loader getInstance() {
        return instance == null ? (instance = new Loader()) : instance;
    }

    private Loader() {
    }

    public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indices,
            Vector3f min, Vector3f max) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);

        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 2, textureCoords);
        storeDataInAttributeList(2, 3, normals);
        unbindVAO();

        return new RawModel(vaoID, indices.length, min, max);
    }

    public InstancedRawModel loadInstancesToVAO(float[] positions, float[] textureCoords, float[] normals,
            int[] indices, Vector3f min, Vector3f max) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);

        glEnableVertexAttribArray(0);
        storeDataInAttributeList(0, 3, positions);
        glEnableVertexAttribArray(1);
        storeDataInAttributeList(1, 2, textureCoords);
        glEnableVertexAttribArray(2);
        storeDataInAttributeList(2, 3, normals);
        unbindVAO();

        final int VECTOR4F_SIZE_BYTES = 4 * 4;

        final int MATRIX_SIZE_BYTES = 4 * VECTOR4F_SIZE_BYTES;


        int nbInstances = 5000;  // TODO: 5000 is completely random

        GL40.glBindVertexArray(vaoID);

        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
//        FloatBuffer buffer = MemoryUtil.memAllocFloat(nbInstances * MATRIX_SIZE_FLOATS);
//        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_DYNAMIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
//        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, test2, GL15.GL_DYNAMIC_DRAW);
        int start = 3;

        for (int i = 0; i < 4; i++) {
            GL20.glVertexAttribPointer(start, 4, GL_FLOAT, false, MATRIX_SIZE_BYTES, i * VECTOR4F_SIZE_BYTES);
            GL41.glVertexAttribDivisor(start, 1);
            glEnableVertexAttribArray(start);
            start++;
        }
        unbindVAO();

        return new InstancedRawModel(vboID, vaoID, indices.length, min, max, nbInstances);
    }

    public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);

        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 2, textureCoords);
        storeDataInAttributeList(2, 3, normals);
        unbindVAO();

        return new RawModel(vaoID, indices.length);
    }

    public RawModel loadToVAO(float[] positions, int dimensions) {
        int vaoID = createVAO();

        storeDataInAttributeList(0, dimensions, positions);

        unbindVAO();

        return new RawModel(vaoID, positions.length / dimensions);
    }

    public RawModel loadToVAO(float[] positions, int dimensions, int attributeNumber) {
        int vaoID = createVAO();

        this.storeDataInAttributeList(attributeNumber, dimensions, positions);

        unbindVAO();

        return new RawModel(vaoID, positions.length / dimensions);
    }

    public int loadToVAO(float[] positions, float[] textureCoords) {
        int vaoID = createVAO();

        this.storeDataInAttributeList(0, 2, positions);
        this.storeDataInAttributeList(1, 2, textureCoords);

        unbindVAO();

        return vaoID;
    }

    public int loadCubeMap(String[] textureFiles) {
        int texID = GL11.glGenTextures();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);

        for (int i = 0; i < textureFiles.length; i++) {
            TextureData data = decodeTextureFile("res/" + textureFiles[i] + ".png");
            GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, data.getWidth(),
                    data.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());
        }

        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);

        textures.add(texID);
        return texID;
    }

    private TextureData decodeTextureFile(String fileName) {
        int width = 0;
        int height = 0;
        ByteBuffer buffer = null;
        try {
            FileInputStream in = new FileInputStream(fileName);
            PNGDecoder decoder = new PNGDecoder(in);
            width = decoder.getWidth();
            height = decoder.getHeight();
            buffer = ByteBuffer.allocateDirect(4 * width * height);
            decoder.decode(buffer, width * 4, Format.RGBA);
            buffer.flip();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Tried to load texture " + fileName + ", didn't work");
            System.exit(-1);
        }
        return new TextureData(buffer, width, height);
    }

    public void cleanUp() {
        vaos.forEach(GL30::glDeleteVertexArrays);
        vbos.forEach(GL15::glDeleteBuffers);
        textures.forEach(GL11::glDeleteTextures);
    }

    private int createVAO() {
        int vaoID = GL30.glGenVertexArrays();
        vaos.add(vaoID);
        GL30.glBindVertexArray(vaoID);
        return vaoID;
    }

    private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL_FLOAT, false, 0, 0);
    }

    private void unbindVAO() {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }

    private void bindIndicesBuffer(int[] indices) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
        IntBuffer buffer = storeDataInIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    private IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();

        return buffer;
    }

    private FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();

        return buffer;
    }
}

