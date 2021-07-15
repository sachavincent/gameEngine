package util.parsing.colladaParser.dataStructures;

import java.util.HashMap;
import java.util.Map;
import util.parsing.objParser.Material;

public class MeshData {

    private final float[] vertices;
    private final float[] textureCoords;
    private final float[] normals;
    private final int[]   indices;

    private final Map<Material, int[]> materialIndices;

    private final float[] tangents;

    private final int[]   jointIds;
    private final float[] vertexWeights;


    /**
     * For Models without NormalMap
     */
    public MeshData(float[] vertices, float[] textureCoords, float[] normals, int[] indices) {
        this(vertices, textureCoords, normals, indices, new HashMap<>(Map.of(new Material("TEMP"), new int[0])), new float[0]);
    }


    /**
     * For Models with NormalMap
     */
    public MeshData(float[] vertices, float[] textureCoords, float[] normals, int[] indices, Map<Material, int[]> materialIndices,
            float[] tangents) {
        this(vertices, textureCoords, normals, indices, materialIndices, new int[0], new float[0], tangents);
    }

    /**
     * For AnimatedModels
     */
    public MeshData(float[] vertices, float[] textureCoords, float[] normals, int[] indices, Map<Material, int[]> materialIndices,
            int[] jointIds, float[] vertexWeights) {
        this(vertices, textureCoords, normals, indices, materialIndices, jointIds,
                vertexWeights,//TODO: Animated textured models
                new float[0]);
    }

    private MeshData(float[] vertices, float[] textureCoords, float[] normals, int[] indices, Map<Material, int[]> materialIndices,
            int[] jointIds, float[] vertexWeights, float[] tangents) {
        this.vertices = vertices;
        this.textureCoords = textureCoords;
        this.normals = normals;
        this.indices = indices;
        this.materialIndices = materialIndices;
        this.jointIds = jointIds;
        this.vertexWeights = vertexWeights;
        this.tangents = tangents;
    }

    public int[] getJointIds() {
        return this.jointIds;
    }

    public float[] getVertexWeights() {
        return this.vertexWeights;
    }

    public float[] getVertices() {
        return this.vertices;
    }

    public float[] getTextureCoords() {
        return this.textureCoords;
    }

    public float[] getNormals() {
        return this.normals;
    }

    public Map<Material, int[]> getMaterialIndices() {
        return this.materialIndices;
    }

    public int[] getIndices() {
        return this.indices;
    }

    public int getVertexCount() {
        return this.vertices.length / 3;
    }

    public float[] getTangents() {
        return this.tangents;
    }

    public Map<Material, int[]> temp;
    public void setTempValue(Map<Material, int[]> tempLocalIndicesMap) {
        temp = tempLocalIndicesMap;
    }
}