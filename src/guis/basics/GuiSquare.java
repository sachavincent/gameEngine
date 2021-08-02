package guis.basics;

import guis.GuiInterface;
import guis.constraints.AspectConstraint;
import guis.constraints.GuiConstraints;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.PixelConstraint;
import guis.presets.Background;

public class GuiSquare extends GuiShape {

    public GuiSquare(GuiInterface gui, Background<?> background) {
        super(gui, background, new AspectConstraint(1f), new PixelConstraint(100), true);
    }

    public GuiSquare(GuiInterface gui, Background<?> background, boolean filled) {
        super(gui, background, new AspectConstraint(1f), new PixelConstraint(100), filled);
    }

    @Override
    public void setWidthConstraint(GuiConstraints widthConstraint) {
        GuiConstraintsManager constraints = new GuiConstraintsManager();
        constraints.setWidthConstraint(widthConstraint);
        constraints.setHeightConstraint(new AspectConstraint(1f));

        update(constraints);
    }

    @Override
    public void setHeightConstraint(GuiConstraints heightConstraint) {
        GuiConstraintsManager constraints = new GuiConstraintsManager();
        constraints.setHeightConstraint(heightConstraint);
        constraints.setWidthConstraint(new AspectConstraint(1f));

        update(constraints);
    }

    @Override
    public String toString() {
        return "GuiSquare{" + super.toString() + "}";
    }
}
