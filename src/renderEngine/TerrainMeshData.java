package renderEngine;

public class TerrainMeshData {

    private final int[] vertices;
    private final int[] indices;

    public TerrainMeshData(int[] vertices, int[] indices) {
        this.vertices = vertices;
        this.indices = indices;
    }

    public int[] getVertices() {
        return this.vertices;
    }

    public int[] getIndices() {
        return this.indices;
    }
}
