package fontMeshCreator;

public class TextMeshData {

    private final float[] vertexPositions;
    private final float[] textureCoords;
    private final float[] colors;

    public TextMeshData(float[] vertexPositions, float[] textureCoords, float[] colors) {
        this.vertexPositions = vertexPositions;
        this.textureCoords = textureCoords;
        this.colors = colors;
    }

    public float[] getVertexPositions() {
        return this.vertexPositions;
    }

    public float[] getColors() {
        return this.colors;
    }

    public float[] getTextureCoords() {
        return this.textureCoords;
    }

    public int getVertexCount() {
        return this.vertexPositions.length / 2;
    }

}
