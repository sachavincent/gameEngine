package textures;

import guis.presets.GuiBackground;

public class TerrainTexture extends Texture {

    public TerrainTexture(String pathFile) {
        super(new GuiBackground(pathFile));
    }

}
