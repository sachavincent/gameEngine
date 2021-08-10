package textures;

public class TerrainTexture extends Texture {

    private final Float[][] heights;

    public TerrainTexture(Float[][] heights) {
        super(heights);
        this.heights = heights;
    }

    public Float[][] getHeights() {
        return this.heights;
    }
}
