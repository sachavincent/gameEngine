package guis;

import guis.presets.GuiBackground;
import java.awt.Color;
import textures.Texture;
import util.math.Vector2f;

public class GuiTexture<E> extends Texture {

    private Vector2f position, scale;

    private float finalAlpha, alpha;

    public GuiTexture(GuiBackground<?> background, GuiInterface guiInterface) {
        this(background, new Vector2f(guiInterface.getX(), guiInterface.getY()),
                new Vector2f(guiInterface.getWidth(), guiInterface.getHeight()));
    }

    public GuiTexture(GuiBackground<?> background, Vector2f position, Vector2f scale) {
        super(background);

        this.position = position;
        this.scale = scale;

        if (background.getBackground() instanceof String)
            this.alpha = 1f;
        else if (background.getBackground() instanceof Color)
            this.alpha = ((Color) background.getBackground()).getAlpha() / 255f;
        else if (background.getBackground() instanceof Integer) {
        } else
            throw new IllegalArgumentException("Type invalide");

        this.finalAlpha = this.alpha;
    }

//    GuiTexture(String file) {
//        super(file);
//
//        this.alpha = 1f;
//        this.finalAlpha = this.alpha;
//    }
//
//    GuiTexture(Color color) {
//        super(color);
//
//        this.alpha = color.getAlpha() / 255f;
//        this.finalAlpha = this.alpha;
//    }


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

    public void setPosition(Vector2f position) {
        this.position = position;
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
