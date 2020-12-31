package guis.presets;

import java.awt.Color;

public class Background<E> {

    public final static Background<?> NO_BACKGROUND    = new Background<>(new Color(0, 0, 0, 0));
    public final static Background<?> WHITE_BACKGROUND = new Background<>(Color.WHITE);
    public final static Background<?> BLACK_BACKGROUND = new Background<>(Color.BLACK);
    public static final Background<?> RED_BACKGROUND   = new Background<>(Color.RED);

    private final E background;

    public Background(E texture) {
        background = texture;
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
