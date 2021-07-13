package textures;

import guis.presets.Background;
import java.io.File;

public class FontTexture extends Texture {

    public FontTexture(File file) {
        super(new Background<>(file));
    }
}
