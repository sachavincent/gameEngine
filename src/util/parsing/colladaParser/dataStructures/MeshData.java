package util.parsing.colladaParser.dataStructures;

import java.util.HashMap;
import java.util.Map;
import util.parsing.objParser.Material;

public class MeshData {

    private final float[]              vertices;
    private final float[]              textureCoords;
    private final float[]              normals;
    private final Map<Material, int[]> indicesList;

    private final float[] tangents;

    private final int[]   jointIds;
    private final float[] vertexWeights;


    /**
     * For Models without NormalMap
     */
    public MeshData(float[] vertices, float[] textureCoords, float[] normals, int[] indices) {
        this(vertices, textureCoords, normals, new HashMap<>(Map.of(new Material("TEMP"), indices)), new float[0]);
    }


    /**
     * For Models with NormalMap
     */
    public MeshData(float[] vertices, float[] textureCoords, float[] normals, Map<Material, int[]> indicesList,
            float[] tangents) {
        this(vertices, textureCoords, normals, indicesList, new int[0], new float[0], tangents);
    }

    /**
     * For AnimatedModels
     */
    public MeshData(float[] vertices, float[] textureCoords, float[] normals, int[] indices,
            int[] jointIds, float[] vertexWeights) {
        this(vertices, textureCoords, normals, new HashMap<>(Map.of(new Material("TEMP"), indices)), jointIds,
                vertexWeights,//TODO: Animated textured models
                new float[0]);
    }

    private MeshData(float[] vertices, float[] textureCoords, float[] normals, Map<Material, int[]> indicesList,
            int[] jointIds, float[] vertexWeights, float[] tangents) {
        this.vertices = vertices;
        this.textureCoords = textureCoords;
        this.normals = normals;
        this.indicesList = indicesList;
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

    public Map<Material, int[]> getIndicesList() {
        return this.indicesList;
    }

    public int getVertexCount() {
        return this.vertices.length / 3;
    }

    public float[] getTangents() {
        return this.tangents;
    }
}
