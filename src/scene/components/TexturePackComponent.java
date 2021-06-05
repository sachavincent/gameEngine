package scene.components;

import textures.TerrainTexturePack;

public class TexturePackComponent extends Component {

    private final TerrainTexturePack terrainTexturePack;

    public TexturePackComponent(TerrainTexturePack terrainTexturePack) {
        this.terrainTexturePack = terrainTexturePack;
    }

    public TerrainTexturePack getTerrainTexturePack() {
        return this.terrainTexturePack;
    }
}
