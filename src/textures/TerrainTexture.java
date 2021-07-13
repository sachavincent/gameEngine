package textures;

import guis.presets.Background;
import java.awt.Color;
import java.io.File;

public class TerrainTexture extends Texture {

    public TerrainTexture(File file) {
        super(new Background<>(file));
    }

    public TerrainTexture(Color color) {
        super(new Background<>(color));
    }
}
