package util;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;

public class Vbo {

    private final int vboId;
    private final int type;

    private int count;

    private Vbo(int vboId, int type) {
        this.vboId = vboId;
        this.type = type;
    }

    public static Vbo create(int type) {
        int id = GL15.glGenBuffers();
        return new Vbo(id, type);
    }

    public void bind() {
        GL15.glBindBuffer(this.type, this.vboId);
    }

    public int getId() {
        return this.vboId;
    }

    public void unbind() {
        GL15.glBindBuffer(this.type, 0);
    }

    public void storeData(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        storeData(buffer);
        this.count = data.length;
    }

    public int getDataLength() {
        return this.count;
    }

    public void storeData(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        storeData(buffer);
        this.count = data.length;
    }

    private void storeData(IntBuffer data) {
        GL15.glBufferData(this.type, data, GL15.GL_STATIC_DRAW);
    }

    private void storeData(FloatBuffer data) {
        GL15.glBufferData(this.type, data, GL15.GL_STATIC_DRAW);
    }

    public void delete() {
        GL15.glDeleteBuffers(this.vboId);
    }
}