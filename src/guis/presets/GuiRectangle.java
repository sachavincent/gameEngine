package guis.presets;

import guis.Gui;
import guis.constraints.AspectConstraint;
import guis.constraints.GuiConstraints;
import guis.constraints.GuiConstraintsManager;

public class GuiRectangle extends GuiPreset {

    public GuiRectangle(Gui gui, String texture, GuiConstraints width, GuiConstraints height) {
        super(gui, texture);

        GuiConstraintsManager constraints = new GuiConstraintsManager();
        constraints.setHeightConstraint(height);
        constraints.setWidthConstraint(width);

        setConstraints(constraints);

        update(constraints);
    }

    @Override
    public void setXPreset(GuiConstraints xConstraint) {
        GuiConstraintsManager constraints = new GuiConstraintsManager();
        constraints.setxConstraint(xConstraint);

        update(constraints);
    }

    @Override
    public void setYPreset(GuiConstraints yConstraint) {
        GuiConstraintsManager constraints = new GuiConstraintsManager();
        constraints.setyConstraint(yConstraint);

        update(constraints);
    }

    @Override
    public void setWidthPreset(GuiConstraints widthConstraint) {
        GuiConstraintsManager constraints = new GuiConstraintsManager();
        constraints.setWidthConstraint(widthConstraint);

        update(constraints);
    }

    @Override
    public void setHeightPreset(GuiConstraints heightConstraint) {
        GuiConstraintsManager constraints = new GuiConstraintsManager();
        constraints.setHeightConstraint(heightConstraint);

        update(constraints);
    }
}
