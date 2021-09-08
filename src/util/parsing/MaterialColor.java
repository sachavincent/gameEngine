package util.parsing;

import java.awt.Color;
import util.math.Vector3f;

public abstract class MaterialColor {

    protected Vector3f color;

    public abstract MaterialColor copy();

    public final Vector3f getColor() {
        return this.color;
    }

    public final Color getAWTColor() {
        return new Color(this.color.getX(), this.color.getY(), this.color.getZ());
    }
}