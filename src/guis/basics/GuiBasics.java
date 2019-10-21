package guis.basics;

import guis.Gui;
import guis.GuiComponent;
import guis.constraints.GuiConstraints;
import guis.constraints.GuiConstraintsManager;

public abstract class GuiBasics extends GuiComponent {

    public GuiBasics(Gui parent, String texture) {
        super(parent, texture);
    }

    public abstract void setXPreset(GuiConstraints xConstraint);

    public abstract void setYPreset(GuiConstraints yConstraint);

    public abstract void setWidthPreset(GuiConstraints widthConstraint);

    public abstract void setHeightPreset(GuiConstraints heightConstraint);

    void update(GuiConstraintsManager constraints) {
        setConstraints(constraints);
    }
}
