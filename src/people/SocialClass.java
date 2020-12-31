package people;

import com.sun.xml.internal.ws.util.StringUtils;
import java.awt.Color;
import java.util.Locale;

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

    public String getName() {
        return StringUtils.capitalize(name().toLowerCase(Locale.ROOT));
    }

}
