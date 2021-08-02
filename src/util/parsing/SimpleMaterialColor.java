package util.parsing;

import util.math.Vector3f;

import java.awt.*;

public class SimpleMaterialColor extends MaterialColor {

    public SimpleMaterialColor(Vector3f color) {
        this.color = color;
    }

    public SimpleMaterialColor(Color color) {
        this.color = new Vector3f(color.getRed() / 255f,
                color.getGreen() / 255f, color.getBlue() / 255f);
    }

    @Override
    public MaterialColor copy() {
        return new SimpleMaterialColor(this.color);
    }
}
