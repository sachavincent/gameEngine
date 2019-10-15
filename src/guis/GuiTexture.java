package guis;

import textures.Texture;
import util.vector.Vector2f;

public class GuiTexture extends Texture {

    private Vector2f position, scale;

    public GuiTexture(String file, Vector2f position, Vector2f scale) {
        super(file);
        this.position = position;
        this.scale = scale;
    }

    public Vector2f getPosition() {
        return this.position;
    }

    public Vector2f getScale() {
        return this.scale;
    }
}
