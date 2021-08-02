package util.parsing.objParser.objConverter;

public class ModelDataNM {

    private final float[] vertices;
    private final float[] textureCoords;
    private final float[] normals;
    private final float[] tangents;
    private final int[]   indices;
    private final float   furthestPoint;

    public ModelDataNM(float[] vertices, float[] textureCoords, float[] normals, float[] tangents, int[] indices,
            float furthestPoint) {
        this.vertices = vertices;
        this.textureCoords = textureCoords;
        this.normals = normals;
        this.indices = indices;
        this.furthestPoint = furthestPoint;
        this.tangents = tangents;
    }

    public float[] getVertices() {
        return this.vertices;
    }

    public float[] getTextureCoords() {
        return this.textureCoords;
    }

    public float[] getTangents() {
        return this.tangents;
    }

    public float[] getNormals() {
        return this.normals;
    }

    public int[] getIndices() {
        return this.indices;
    }

    public float getFurthestPoint() {
        return this.furthestPoint;
    }
}