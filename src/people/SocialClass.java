package people;

import java.awt.Color;
import java.util.Locale;
import util.Utils;

public enum SocialClass {
    FARMER(Color.RED);

    private final Color color;
//    private final Item[] items;

    SocialClass(Color color/*, Item... items*/) {
        this.color = color;
//        this.items = items;
    }

//    public Item[] getItems() {
//        return this.items;
//    }

    public Color getColor() {
        return this.color;
    }

    public static int getNbClasses() {
        return values().length;
    }

    public String getName() {
        return Utils.formatText(name());
    }
}