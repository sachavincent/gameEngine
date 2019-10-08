package main.textures;

import main.fontMeshCreator.GUIText;

public class FontTexture extends Texture {
    private GUIText guiText;

    public FontTexture(String fileName) {
        super(fileName);
    }

    public GUIText getGuiText() {
        return this.guiText;
    }
}
