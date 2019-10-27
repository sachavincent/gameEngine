package guis.basics;

import guis.GuiInterface;
import guis.constraints.AspectConstraint;
import guis.constraints.GuiConstraints;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.PixelConstraint;
import java.awt.Color;

public class GuiCircle extends GuiBasics {

    public GuiCircle(GuiInterface gui, String texture) {
        super(gui, texture, new AspectConstraint(1f), new PixelConstraint(100));
    }

    public GuiCircle(GuiInterface gui, Color color) {
        super(gui, color, new AspectConstraint(1f), new PixelConstraint(100));
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

}