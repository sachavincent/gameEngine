package guis.presets;

import java.awt.Color;

public class GuiBackground<E> {

    public final static GuiBackground<?> NO_BACKGROUND    = new GuiBackground<>(new Color(0, 0, 0, 255));
    public final static GuiBackground<?> WHITE_BACKGROUND = new GuiBackground<>(Color.WHITE);
    public final static GuiBackground<?> BLACK_BACKGROUND = new GuiBackground<>(Color.BLACK);

    private final E background;

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

    @Override
    public String toString() {
        return "GuiBackground{" +
                "background=" + background +
                '}';
    }
}
