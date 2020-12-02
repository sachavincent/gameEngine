package people;

import java.awt.Color;

public enum SocialClass {
    FARMER(Color.RED);

    private final Color color;

    SocialClass(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return this.color;
    }

    public static int getNbClasses() {
        return values().length;
    }
}
