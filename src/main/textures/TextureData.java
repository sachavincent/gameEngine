package main.textures;

import java.nio.ByteBuffer;

public class TextureData {

    private int width, height;
    private ByteBuffer buffer;

    public TextureData(ByteBuffer buffer, int width, int height) {
        this.width = width;
        this.height = height;
        this.buffer = buffer;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public ByteBuffer getBuffer() {
        return this.buffer;
    }
}
