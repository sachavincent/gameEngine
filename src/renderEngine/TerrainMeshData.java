package renderEngine;

public class TerrainMeshData {

    private final Integer[] vertices;
    private final Integer[] indices;
    private final Integer[] isEdge;

    public TerrainMeshData(Integer[] vertices, Integer[] isEdge, Integer[] indices) {
        this.vertices = vertices;
        this.indices = indices;
        this.isEdge = isEdge;
    }

    public Integer[] getIsEdge() {
        return this.isEdge;
    }

    public Integer[] getVertices() {
        return this.vertices;
    }

    public Integer[] getIndices() {
        return this.indices;
    }
}