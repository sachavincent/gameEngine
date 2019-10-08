package main.models;

public class RawModel {

    private int vaoID, vertexCount;
    private float width, depth, height;

    public RawModel(int vaoID, int vertexCount, float width, float depth, float height) {
        this.vaoID = vaoID;
        this.vertexCount = vertexCount;
        this.width = width;
        this.depth = depth;
        this.height = height;
    }

    public int getVaoID() {
        return this.vaoID;
    }

    public int getVertexCount() {
        return this.vertexCount;
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public float getDepth() {
        return this.depth;
    }
}
