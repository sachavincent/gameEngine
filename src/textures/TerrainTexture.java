package textures;

import guis.presets.Background;
import java.awt.Color;

public class TerrainTexture extends Texture {

    public TerrainTexture(String pathFile) {
        super(new Background<>(pathFile));
    }

    public TerrainTexture(Color color) {
        super(new Background<>(color));
    }
}
