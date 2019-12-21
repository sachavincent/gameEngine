package guis.presets;

import java.awt.Color;

public class GuiBackground<E> {

    private E background;

    public GuiBackground(String texture) {
        background = (E) texture;
    }

    public GuiBackground(Integer texture) {
        background = (E) texture;
    }

    public GuiBackground(Color color) {
        background = (E) color;
    }

    public E getBackground() {
        return this.background;
    }
}
