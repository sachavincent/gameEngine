package textures;

import fontMeshCreator.Text;

public class FontTexture extends Texture {
    private Text text;

    public FontTexture(String fileName) {
        super(fileName);
    }

    public Text getText() {
        return this.text;
    }
}
