package guis.basics;

import guis.GuiInterface;
import guis.constraints.GuiConstraints;
import guis.constraints.GuiConstraintsManager;
import java.awt.Color;

public class GuiRectangle extends GuiBasics {

    public GuiRectangle(GuiInterface gui, String texture, GuiConstraints width, GuiConstraints height) {
        super(gui, texture, width, height);
    }

    public GuiRectangle(GuiInterface gui, Color color, GuiConstraints width, GuiConstraints height) {
        super(gui, color, width, height);
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
