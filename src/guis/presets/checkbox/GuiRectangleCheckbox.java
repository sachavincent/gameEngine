package guis.presets.checkbox;

import guis.GuiInterface;
import guis.basics.GuiRectangle;
import guis.basics.GuiShape;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RelativeConstraint;
import guis.presets.Background;
import java.awt.Color;

public class GuiRectangleCheckbox extends GuiAbstractCheckbox {

    public GuiRectangleCheckbox(GuiInterface parent, Background<?> background, Color borderColor) {
        super(parent, background, borderColor);
    }

    public GuiRectangleCheckbox(GuiInterface parent, Background<?> background, Color borderColor,
            GuiConstraintsManager constraintsManager) {
        super(parent, background, borderColor, constraintsManager);
    }

    @Override
    public GuiShape createShape(Background<?> baseColor, Color borderColor) {
        boolean filled = baseColor != null && !baseColor.equals(Background.NO_BACKGROUND);
        GuiRectangle guiRectangle = new GuiRectangle(this, baseColor, new RelativeConstraint(1),
                new RelativeConstraint(1), filled);
        guiRectangle.setBorderColor(borderColor);
        return guiRectangle;
    }
}
