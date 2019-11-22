package guis.presets.checkbox.buttons;

import guis.GuiInterface;
import guis.basics.GuiRectangle;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RelativeConstraint;
import java.awt.Color;

public class GuiRectangleCheckbox extends GuiCheckbox {

    public GuiRectangleCheckbox(GuiInterface parent, Color colorBackground, GuiConstraintsManager constraintsManager) {
        super(parent, colorBackground, constraintsManager);
    }

    @Override
    protected void addBackgroundComponent(Color background) {
        checkboxLayout = new GuiRectangle(this, background, new RelativeConstraint(1, this),
                new RelativeConstraint(1, this), false);
    }

    public void setOutlineWidth(int width) {
        ((GuiRectangle) this.checkboxLayout).setOutlineWidth(width);
    }

    @Override
    public String toString() {
        return "GuiRectangleCheckbox{" +
                "checkboxLayout=" + checkboxLayout +
                ", checkmark=" + checkmark +
                "} ";
    }
}
