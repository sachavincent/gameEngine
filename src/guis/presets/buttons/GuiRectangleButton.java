package guis.presets.buttons;

import fontMeshCreator.Text;
import guis.GuiInterface;
import guis.basics.GuiRectangle;
import guis.basics.GuiShape;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RelativeConstraint;
import guis.presets.Background;
import java.awt.Color;

public class GuiRectangleButton extends GuiAbstractButton {

    public GuiRectangleButton(GuiInterface parent, Background<?> background, Color borderColor, Text text,
            GuiConstraintsManager constraintsManager) {
        super(parent, background, borderColor, text, constraintsManager);
    }

    public GuiRectangleButton(GuiInterface parent, Background<?> background, Color borderColor, Text text,
            Text tooltipGui, GuiConstraintsManager constraintsManager) {
        super(parent, background, borderColor, text, tooltipGui, constraintsManager);
    }

    public GuiRectangleButton(GuiInterface parent, Background<?> background, Color borderColor,
            GuiConstraintsManager constraintsManager) {
        super(parent, background, borderColor, constraintsManager);
    }

    public GuiRectangleButton(GuiInterface parent, Background<?> background, Color borderColor, Text text) {
        super(parent, background, borderColor, text);
    }

    public GuiRectangleButton(GuiInterface parent, Background<?> background, Color borderColor, Text text,
            GuiConstraintsManager constraintsManager, int cornerRadius) {
        super(parent, background, borderColor, text, constraintsManager, cornerRadius);
    }

    public GuiRectangleButton(GuiInterface parent, Background<?> background, Color borderColor, Text text,
            Text tooltipGui, GuiConstraintsManager constraintsManager, int cornerRadius) {
        super(parent, background, borderColor, text, tooltipGui, constraintsManager, cornerRadius);
    }

    public GuiRectangleButton(GuiInterface parent, Background<?> background, Color borderColor, Text text,
            Text tooltipGui, int cornerRadius) {
        super(parent, background, borderColor, text, tooltipGui, cornerRadius);
    }

    public GuiRectangleButton(GuiInterface parent, Background<?> background, Color borderColor, Text text,
            Text tooltipGui) {
        super(parent, background, borderColor, text, tooltipGui);
    }

    public GuiRectangleButton(GuiInterface parent, Background<?> background, Color borderColor,
            GuiConstraintsManager constraintsManager, int cornerRadius) {
        super(parent, background, borderColor, constraintsManager, cornerRadius);
    }

    @Override
    public GuiShape createShape(Background<?> baseColor, Color borderColor) {
        boolean filled = baseColor != null && !baseColor.equals(Background.NO_BACKGROUND);
        GuiRectangle guiRectangle = new GuiRectangle(this, baseColor, new RelativeConstraint(1),
                new RelativeConstraint(1), filled);
        guiRectangle.setBorderColor(borderColor);
        return guiRectangle;
    }

    @Override
    protected void setFilter() {
        this.filterLayout = new GuiRectangle(this, new Background<>(Color.WHITE), new RelativeConstraint(1),
                new RelativeConstraint(1));
    }


    @Override
    public String toString() {
        return "GuiRectangleButton{" +
                "filterLayout=" + this.filterLayout +
                "} ";
    }
}
