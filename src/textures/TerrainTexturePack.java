package textures;

public class TerrainTexturePack {

    private TerrainTexture backgroundTexture, rTexture, gTexture, bTexture;

    public TerrainTexturePack(TerrainTexture backgroundTexture, TerrainTexture rTexture,
            TerrainTexture gTexture, TerrainTexture bTexture) {
        this.backgroundTexture = backgroundTexture;
        this.rTexture = rTexture;
        this.gTexture = gTexture;
        this.bTexture = bTexture;
    }

    public TerrainTexture getBackgroundTexture() {
        return this.backgroundTexture;
    }

    public TerrainTexture getrTexture() {
        return this.rTexture;
    }

    public TerrainTexture getgTexture() {
        return this.gTexture;
    }

    public TerrainTexture getbTexture() {
        return this.bTexture;
    }
}
