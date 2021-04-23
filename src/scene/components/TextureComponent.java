package scene.components;

import models.TexturedModel;

public class TextureComponent implements Component {

    private final TexturedModel texture;

    public TextureComponent(TexturedModel texture) {
        this.texture = texture;
    }

    public TexturedModel getTexture() {
        return this.texture;
    }
}
