package guis.basics;

import guis.GuiInterface;
import guis.constraints.GuiConstraints;
import java.awt.Color;

public abstract class GuiShape extends GuiBasics {

    private boolean filled;
    private int outlineWidth = 1;

    public GuiShape(GuiInterface parent, String texture, GuiConstraints width, GuiConstraints height, boolean filled) {
        super(parent, texture, width, height);

        this.filled = filled;
    }

    public GuiShape(GuiInterface parent, Color color, GuiConstraints width, GuiConstraints height, boolean filled) {
        super(parent, color, width, height);

        this.filled = filled;
    }

    public boolean isFilled() {
        return this.filled;
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
    }

    @Override
    public String toString() {
        return "GuiShape{" +
                "filled=" + filled +
                "} " + super.toString();
    }

    public int getOutlineWidth() {
        return this.outlineWidth;
    }

    public void setOutlineWidth(int outlineWidth) {
        this.outlineWidth = outlineWidth;
    }
}
