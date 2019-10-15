package guis.presets;

import guis.Gui;
import guis.constraints.AspectConstraint;
import guis.constraints.GuiConstraints;
import guis.constraints.GuiConstraintsManager;

public class GuiSquare extends GuiPreset {

    public GuiSquare(Gui gui, String texture, GuiConstraints dimension) {
        super(gui, texture);

        GuiConstraintsManager constraints = new GuiConstraintsManager();
        constraints.setHeightConstraint(dimension);
        constraints.setWidthConstraint(new AspectConstraint(1f));

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
        constraints.setHeightConstraint(new AspectConstraint(1f));

        update(constraints);
    }

    @Override
    public void setHeightPreset(GuiConstraints heightConstraint) {
        GuiConstraintsManager constraints = new GuiConstraintsManager();
        constraints.setHeightConstraint(heightConstraint);
        constraints.setWidthConstraint(new AspectConstraint(1f));

        update(constraints);
    }
}
