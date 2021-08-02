package util.parsing;

import util.math.Vector3f;

import java.awt.*;

public abstract class MaterialColor {

    protected Vector3f color;

    public abstract MaterialColor copy();

    public final Vector3f getColor() {
        return this.color;
    }

    public final Color getAWTColor() {
        return new Color(this.color.x, this.color.y, this.color.z);
    }
}
