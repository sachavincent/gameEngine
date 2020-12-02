package textures;

import fontMeshCreator.Text;
import guis.presets.Background;

public class FontTexture extends Texture {

    private Text text;

    public FontTexture(String fileName) {
        super(new Background<>(fileName));
    }

    public Text getText() {
        return this.text;
    }
}
