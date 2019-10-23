package guis.basics;

import guis.GuiComponent;
import guis.GuiInterface;
import guis.constraints.GuiConstraints;
import guis.constraints.GuiConstraintsManager;

public abstract class GuiBasics extends GuiComponent {

    float baseWidth, baseHeight;

    public float getBaseWidth() {
        return this.baseWidth;
    }

    public float getBaseHeight() {
        return this.baseHeight;
    }

    public GuiBasics(GuiInterface parent, String texture) {
        super(parent, texture);
    }

    public abstract void setXPreset(GuiConstraints xConstraint);

    public abstract void setYPreset(GuiConstraints yConstraint);

    public abstract void setWidthPreset(GuiConstraints widthConstraint);

    public abstract void setHeightPreset(GuiConstraints heightConstraint);

    public void update(GuiConstraintsManager constraints) {
        setConstraints(constraints);
    }
}
