package guis.presets.sliders;

import guis.constraints.GuiConstraints;
import guis.constraints.Side;
import java.awt.Color;

public class ScrollBar {

    private final Side           side;
    private final GuiConstraints dimensionConstraint;
    private final double         minSize; // Between 0 and 2
    private final Color          color;

    public ScrollBar(Side side, GuiConstraints dimensionConstraint, double minSize, Color color) {
        this.side = side;
        this.dimensionConstraint = dimensionConstraint;
        this.minSize = minSize;
        this.color = color;
    }

    public Side getSide() {
        return this.side;
    }

    public GuiConstraints getDimensionConstraint() {
        return this.dimensionConstraint;
    }

    public Color getColor() {
        return this.color;
    }

    public double getMinSize() {
        return this.minSize;
    }
}