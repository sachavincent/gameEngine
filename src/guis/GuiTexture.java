package guis;

import java.awt.Color;
import textures.Texture;
import util.vector.Vector2f;

public class GuiTexture extends Texture {

    private Vector2f position, scale;

    private float finalAlpha, alpha;

    GuiTexture(String file, Vector2f position, Vector2f scale) {
        super(file);

        this.position = position;
        this.scale = scale;

        this.alpha = 1f;
        this.finalAlpha = 1f;
    }

    GuiTexture(Color color, Vector2f position, Vector2f scale) {
        super(color);

        this.position = position;
        this.scale = scale;

        this.alpha = 1f;
        this.finalAlpha = 1f;
    }

    public float getAlpha() {
        return this.alpha;
    }

    public float getFinalAlpha() {
        return this.finalAlpha;
    }

    public void setAlpha(float alpha) {
        if (alpha < 0f)
            return;

        this.alpha = Float.min(alpha, finalAlpha);
    }

    public void setFinalAlpha(float finalAlpha) {
        this.finalAlpha = Float.min(finalAlpha, 1f);
        this.alpha = this.finalAlpha;
    }

    public Vector2f getPosition() {
        return this.position;
    }

    public Vector2f getScale() {
        return this.scale;
    }

    public void setScale(Vector2f scale) {
        this.scale = scale;
    }

    @Override
    public String toString() {
        return "GuiTexture{" +
                "position=" + position +
                ", scale=" + scale +
                ", finalAlpha=" + finalAlpha +
                ", alpha=" + alpha +
                "} ";
    }
}
