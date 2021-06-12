package renderEngine;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_INT;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import models.RawModel;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL41;
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

    public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, float[] tangents,
            int[] indices, Vector3f min, Vector3f max) {
        int vaoID = createVAO();
        int vboID = bindIndicesBuffer(indices);

        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 2, textureCoords);
        storeDataInAttributeList(2, 3, normals);
        storeDataInAttributeList(3, 3, tangents);
        unbindVAO();

        return new RawModel(vaoID, vboID, indices.length, min, max, true, false);
    }

    public RawModel loadInstancesToVAO(float[] positions, float[] textureCoords, float[] normals,
            float[] tangents, int[] indices, Vector3f min, Vector3f max) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);

        glEnableVertexAttribArray(0);
        storeDataInAttributeList(0, 3, positions);
        glEnableVertexAttribArray(1);
        storeDataInAttributeList(1, 2, textureCoords);
        glEnableVertexAttribArray(2);
        storeDataInAttributeList(2, 3, normals);
        glEnableVertexAttribArray(3);
        storeDataInAttributeList(3, 3, tangents);

        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);

        for (int i = 0; i < 4; i++) {
            GL20.glVertexAttribPointer(i + 4, 4, GL_FLOAT, false, 64, i * 16);
            GL41.glVertexAttribDivisor(i + 4, 1);
            glEnableVertexAttribArray(i + 4);
        }
        unbindVAO();

        return new RawModel(vaoID, vboID, indices.length, min, max, true, true);
    }

    public RawModel loadInstancesToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);

        glEnableVertexAttribArray(0);
        storeDataInAttributeList(0, 3, positions);
        glEnableVertexAttribArray(1);
        storeDataInAttributeList(1, 2, textureCoords);
        glEnableVertexAttribArray(2);
        storeDataInAttributeList(2, 3, normals);

        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);

        for (int i = 0; i < 4; i++) {
            GL20.glVertexAttribPointer(i + 3, 4, GL_FLOAT, false, 64, i * 16);
            GL41.glVertexAttribDivisor(i + 3, 1);
            glEnableVertexAttribArray(i + 3);
        }
        unbindVAO();

        return new RawModel(vaoID, vboID, indices.length, false, true);
    }

    public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indices) {
        int vaoID = createVAO();
        int vboID = bindIndicesBuffer(indices);

        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 2, textureCoords);
        storeDataInAttributeList(2, 3, normals);
        unbindVAO();

        return new RawModel(vaoID, vboID, indices.length, false, false);
    }

    public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, float[] tangents,
            int[] indices) {
        int vaoID = createVAO();
        int vboID = bindIndicesBuffer(indices);

        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 2, textureCoords);
        storeDataInAttributeList(2, 3, normals);
        storeDataInAttributeList(3, 3, tangents);
        unbindVAO();

        return new RawModel(vaoID, vboID, indices.length, true, false);
    }

    public RawModel loadToVAO(float[] positions, int dimensions) {
        int vaoID = createVAO();

        storeDataInAttributeList(0, dimensions, positions);

        unbindVAO();

        return new RawModel(vaoID, 0, positions.length / dimensions, false, false);
    }

    public RawModel loadToVAO(int[] positions, int dimensions) {
        int vaoID = createVAO();

        storeDataInAttributeList(0, dimensions, positions);

        unbindVAO();

        return new RawModel(vaoID, 0, positions.length / dimensions, false, false);
    }

    public RawModel loadToVAO(float[] positions, int dimensions, int attributeNumber) {
        int vaoID = createVAO();

        storeDataInAttributeList(attributeNumber, dimensions, positions);

        unbindVAO();

        return new RawModel(vaoID, 0, positions.length / dimensions, false, false);
    }

    public int loadToVAO(float[] positions, float[] textureCoords, float[] colors) {
        int vaoID = createVAO();

        storeDataInAttributeList(0, 2, positions);
        storeDataInAttributeList(1, 2, textureCoords);
        storeDataInAttributeList(2, 3, colors);

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

    private void storeDataInAttributeList(int attributeNumber, int coordinateSize, int[] data) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        IntBuffer buffer = storeDataInIntBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL_INT, false, 0, 0);
    }

    private void storeInstancedDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) {
        FloatBuffer buffer = storeDataInFloatBuffer(data);
//        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        final int VECTOR4F_SIZE_BYTES = 4 * 4;

        final int MATRIX_SIZE_BYTES = 4 * VECTOR4F_SIZE_BYTES;
        GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL_FLOAT, false, MATRIX_SIZE_BYTES,
                (long) attributeNumber * VECTOR4F_SIZE_BYTES);
        GL20.glEnableVertexAttribArray(attributeNumber);
    }

    private void unbindVAO() {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }

    private int bindIndicesBuffer(int[] indices) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
        IntBuffer buffer = storeDataInIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);

        return vboID;
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

