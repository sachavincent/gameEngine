package textures;

import fontMeshCreator.Text;
import guis.presets.GuiBackground;

public class FontTexture extends Texture {

    private Text text;

    public FontTexture(String fileName) {
        super(new GuiBackground(fileName));
    }

    public Text getText() {
        return this.text;
    }
}
