package guis.presets;

import java.awt.Color;

public class Background<E> {

    public final static Background<?> NO_BACKGROUND    = new Background<>(new Color(0, 0, 0, 255));
    public final static Background<?> WHITE_BACKGROUND = new Background<>(Color.WHITE);
    public final static Background<?> BLACK_BACKGROUND = new Background<>(Color.BLACK);

    private final E background;

    public Background(String texture) {
        background = (E) texture;
    }

    public Background(Integer texture) {
        background = (E) texture;
    }

    public Background(Color color) {
        background = (E) color;
    }

    public E getBackground() {
        return this.background;
    }

    @Override
    public String toString() {
        return "Background{" +
                "background=" + background +
                '}';
    }
}
