package textures;

import guis.presets.GuiBackground;
import java.awt.Color;

public class TerrainTexture extends Texture {

    public TerrainTexture(String pathFile) {
        super(new GuiBackground<>(pathFile));
    }

    public TerrainTexture(Color color) {
        super(new GuiBackground<>(color));
    }
}
