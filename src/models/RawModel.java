package models;

public class RawModel {

    private int vaoID, vertexCount;

    public RawModel(int vaoID, int vertexCount) {
        this.vaoID = vaoID;
        this.vertexCount = vertexCount;
    }

    public int getVaoID() {
        return this.vaoID;
    }

    public int getVertexCount() {
        return this.vertexCount;
    }

}
