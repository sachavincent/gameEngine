package guis.basics;

import static renderEngine.DisplayManager.MAX_LINE_WIDTH;
import static renderEngine.DisplayManager.MIN_LINE_WIDTH;

import guis.GuiInterface;
import guis.constraints.GuiConstraints;
import guis.constraints.GuiConstraintsManager;
import guis.presets.Background;
import renderEngine.DisplayManager;

public abstract class GuiShape extends GuiBasics {

    private boolean filled;
    private double  outlineWidth = DisplayManager.MIN_LINE_WIDTH;

    public GuiShape(GuiInterface parent, Background<?> background, GuiConstraints width, GuiConstraints height,
            boolean filled) {
        super(parent, background, width, height);

        this.filled = filled;
    }

    public GuiShape(GuiInterface parent, Background<?> background, GuiConstraintsManager guiConstraintsManager,
            boolean filled) {
        super(parent, background, guiConstraintsManager);

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

    public double getOutlineWidth() {
        return this.outlineWidth;
    }

    public void setOutlineWidth(double outlineWidth) {
        if (outlineWidth > MAX_LINE_WIDTH)
            outlineWidth = MAX_LINE_WIDTH;
        else if (outlineWidth < MIN_LINE_WIDTH)
            outlineWidth = MIN_LINE_WIDTH;

        this.outlineWidth = outlineWidth;
    }
}