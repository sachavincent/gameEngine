package renderEngine;

public class TerrainMeshData {

    private final int[] vertices;
    private final int[] indices;
    private final int[] isEdge;

    public TerrainMeshData(int[] vertices, int[] isEdge, int[] indices) {
        this.vertices = vertices;
        this.indices = indices;
        this.isEdge = isEdge;
    }

    public int[] getIsEdge() {
        return this.isEdge;
    }

    public int[] getVertices() {
        return this.vertices;
    }

    public int[] getIndices() {
        return this.indices;
    }
}
