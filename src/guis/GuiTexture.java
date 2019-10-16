package guis;

import textures.Texture;
import util.vector.Vector2f;

public class GuiTexture extends Texture {

    private Vector2f position, scale;

    private float alpha;

    public GuiTexture(String file, Vector2f position, Vector2f scale) {
        super(file);
        this.position = position;
        this.scale = scale;
        this.alpha = 1f;
    }

    public float getAlpha() {
        return this.alpha;
    }

    public void setAlpha(float alpha) {
        if (alpha > 1f || alpha < 0f)
            return;

        this.alpha = alpha;
    }

    public Vector2f getPosition() {
        return this.position;
    }

    public Vector2f getScale() {
        return this.scale;
    }
}
