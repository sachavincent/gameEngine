package guis.presets.checkbox;

import guis.GuiInterface;
import guis.basics.GuiRectangle;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RelativeConstraint;
import guis.presets.GuiBackground;
import java.awt.Color;

public class GuiRectangleCheckbox extends GuiAbstractCheckbox {

    public GuiRectangleCheckbox(GuiInterface parent, Color colorBackground, GuiConstraintsManager constraintsManager) {
        super(parent, colorBackground, constraintsManager);
    }

    @Override
    protected void addBackgroundComponent(Color background) {
        checkboxLayout = new GuiRectangle(this, new GuiBackground<>(background), new RelativeConstraint(1, this),
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
