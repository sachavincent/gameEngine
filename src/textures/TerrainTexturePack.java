package textures;

public class TerrainTexturePack {

    private final TerrainTexture backgroundTexture;
    private final TerrainTexture redTexture;
    private final TerrainTexture greenTexture;
    private final TerrainTexture blueTexture;

    public TerrainTexturePack(TerrainTexture backgroundTexture, TerrainTexture redTexture,
            TerrainTexture greenTexture, TerrainTexture blueTexture) {
        this.backgroundTexture = backgroundTexture;
        this.redTexture = redTexture;
        this.greenTexture = greenTexture;
        this.blueTexture = blueTexture;
    }

    public TerrainTexture getBackgroundTexture() {
        return this.backgroundTexture;
    }

    public TerrainTexture getRedTexture() {
        return this.redTexture;
    }

    public TerrainTexture getGreenTexture() {
        return this.greenTexture;
    }

    public TerrainTexture getBlueTexture() {
        return this.blueTexture;
    }
}
