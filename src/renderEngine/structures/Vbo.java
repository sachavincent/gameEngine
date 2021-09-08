package renderEngine.structures;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;

public class Vbo {

    private final int vboId;
    private final int type;

    private int count;

    public Vbo(int type) {
        this.vboId = GL15.glGenBuffers();
        this.type = type;
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

    public <Type extends Number> void storeData(Type[] data) {
        if (data == null || data.length == 0)
            return;
        if (data[0] instanceof Integer) {
            IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
            for (Type d : data)
                buffer.put((Integer) d);
            buffer.flip();
            storeData(buffer);
        } else if (data[0] instanceof Float) {
            FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
            for (Type d : data)
                buffer.put((Float) d);
            buffer.flip();
            storeData(buffer);
        } else
            throw new IllegalArgumentException("Wrong data type");

        this.count = data.length;
    }

    public void storeData(Integer[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        for (Integer d : data)
            buffer.put(d);
        buffer.flip();
        storeData(buffer);
        this.count = data.length;
    }

    public int getDataLength() {
        return this.count;
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