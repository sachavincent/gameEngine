package util.colladaParser.dataStructures;

public class MeshData {

    private static final int DIMENSIONS = 3;

    private final float[] vertices;
    private final float[] textureCoords;
    private final float[] normals;
    private final int[]   indices;

    private final float[] tangents;

    private final int[]   jointIds;
    private final float[] vertexWeights;


    /**
     * For Models without NormalMap
     */
    public MeshData(float[] vertices, float[] textureCoords, float[] normals, int[] indices) {
        this(vertices, textureCoords, normals, indices, new float[0]);
    }


    /**
     * For Models with NormalMap
     */
    public MeshData(float[] vertices, float[] textureCoords, float[] normals, int[] indices, float[] tangents) {
        this(vertices, textureCoords, normals, indices, new int[0], new float[0], tangents);
    }

    /**
     * For AnimatedModels
     */
    public MeshData(float[] vertices, float[] textureCoords, float[] normals, int[] indices,
            int[] jointIds, float[] vertexWeights) {
        this(vertices, textureCoords, normals, indices, jointIds, vertexWeights, new float[0]);
    }

    private MeshData(float[] vertices, float[] textureCoords, float[] normals, int[] indices,
            int[] jointIds, float[] vertexWeights, float[] tangents) {
        this.vertices = vertices;
        this.textureCoords = textureCoords;
        this.normals = normals;
        this.indices = indices;
        this.jointIds = jointIds;
        this.vertexWeights = vertexWeights;
        this.tangents = tangents;
    }

    public int[] getJointIds() {
        return jointIds;
    }

    public float[] getVertexWeights() {
        return vertexWeights;
    }

    public float[] getVertices() {
        return vertices;
    }

    public float[] getTextureCoords() {
        return textureCoords;
    }

    public float[] getNormals() {
        return normals;
    }

    public int[] getIndices() {
        return indices;
    }

    public int getVertexCount() {
        return vertices.length / DIMENSIONS;
    }

    public float[] getTangents() {
        return this.tangents;
    }
}
