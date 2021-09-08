package guis.presets;

import java.awt.Color;
import java.util.Objects;

public class Background<E> {

    public static final Background<?> NO_BACKGROUND    = new Background<>(new Color(0, 0, 0, 0));
    public static final Background<?> WHITE_BACKGROUND = new Background<>(Color.WHITE);
    public static final Background<?> BLACK_BACKGROUND = new Background<>(Color.BLACK);
    public static final Background<?> RED_BACKGROUND   = new Background<>(Color.RED);

    private final E background;

    public Background(E texture) {
        this.background = texture;
    }

    public E getBackground() {
        return this.background;
    }

    @Override
    public String toString() {
        return "Background{" +
                "background=" + this.background +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Background<?> that = (Background<?>) o;
        return Objects.equals(this.background, that.background);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.background);
    }
}
