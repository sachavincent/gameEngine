package guis.presets.checkbox;

import guis.GuiInterface;
import guis.basics.GuiEllipse;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RelativeConstraint;
import guis.presets.GuiBackground;
import java.awt.Color;

public class GuiCircularCheckbox extends GuiAbstractCheckbox {

    public GuiCircularCheckbox(GuiInterface parent, Color colorBackground, GuiConstraintsManager constraintsManager) {
        super(parent, colorBackground, constraintsManager);
    }

    @Override
    protected void addBackgroundComponent(Color background) {
        checkboxLayout = new GuiEllipse(this, new GuiBackground(background), new RelativeConstraint(1, this),
                new RelativeConstraint(1, this), false);
    }

    public void setOutlineWidth(int width) {
        ((GuiEllipse) this.checkboxLayout).setOutlineWidth(width);
    }

    @Override
    public String toString() {
        return "GuiCircularCheckbox{" +
                "checkboxLayout=" + checkboxLayout +
                ", checkmark=" + checkmark +
                "} " + super.toString();
    }
}