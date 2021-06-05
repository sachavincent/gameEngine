package guis.presets.checkbox;

import guis.GuiInterface;
import guis.basics.GuiEllipse;
import guis.basics.GuiShape;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RelativeConstraint;
import guis.presets.Background;
import java.awt.Color;
import renderEngine.GuiRenderer;

public class GuiEllipseCheckbox extends GuiAbstractCheckbox {

    public GuiEllipseCheckbox(GuiInterface parent, Background<?> background, Color borderColor,
            GuiConstraintsManager constraintsManager) {
        super(parent, background, borderColor, constraintsManager);
    }

    public GuiEllipseCheckbox(GuiInterface parent, Background<?> background, Color borderColor) {
        super(parent, background, borderColor);
    }

    @Override
    public GuiShape createShape(Background<?> baseColor, Color borderColor) {
        boolean filled = baseColor != null && !baseColor.equals(Background.NO_BACKGROUND);
        GuiEllipse guiEllipse = new GuiEllipse(this, baseColor, new RelativeConstraint(1),
                new RelativeConstraint(1), filled);
        guiEllipse.setBorderColor(borderColor);
        guiEllipse.setType(GuiRenderer.CIRCLE);
        return guiEllipse;
    }
}