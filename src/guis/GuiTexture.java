package guis;

import java.awt.Color;
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

    public GuiTexture(Color color, Vector2f position, Vector2f scale) {
        super(color);

        this.position = position;
        this.scale = scale;
        this.alpha = 1f;
    }

    public float getAlpha() {
        return this.alpha;
    }

    public void setAlpha(float alpha) {
        if (alpha < 0f)
            return;

        this.alpha = Float.min(alpha, 1f);
    }

    public Vector2f getPosition() {
        return this.position;
    }

    public Vector2f getScale() {
        return this.scale;
    }
}
