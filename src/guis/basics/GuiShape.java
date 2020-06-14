package guis.basics;

import guis.GuiInterface;
import guis.constraints.GuiConstraints;
import guis.constraints.GuiConstraintsManager;
import guis.presets.GuiBackground;

public abstract class GuiShape extends GuiBasics {

    private boolean filled;
    private int     outlineWidth = 1;

    public GuiShape(GuiInterface parent, GuiBackground<?> background, GuiConstraints width, GuiConstraints height,
            boolean filled) {
        super(parent, background, width, height);

        this.filled = filled;
    }

    public GuiShape(GuiInterface parent, GuiBackground<?> background, GuiConstraintsManager guiConstraintsManager,
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

    public int getOutlineWidth() {
        return this.outlineWidth;
    }

    public void setOutlineWidth(int outlineWidth) {
        this.outlineWidth = outlineWidth;
    }
}