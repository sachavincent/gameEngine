package guis.presets.checkbox;

import guis.GuiInterface;
import guis.constraints.GuiConstraintsManager;
import java.awt.Color;

public class GuiCircularCheckbox extends GuiAbstractCheckbox {

    public GuiCircularCheckbox(GuiInterface parent, Color colorBackground, GuiConstraintsManager constraintsManager) {
        super(parent, colorBackground, constraintsManager);
    }

//    @Override
//    protected void addBackgroundComponent(Color background) {
//        checkboxLayout = new GuiEllipse(this, new Background<>(background), new RelativeConstraint(1, this),
//                new RelativeConstraint(1, this), false);
//    }

//    public void setOutlineWidth(double width) {
//        ((GuiEllipse) this.checkboxLayout).setOutlineWidth(width);
//    }

    @Override
    public String toString() {
        return "GuiCircularCheckbox{" +
//                "checkboxLayout=" + checkboxLayout +
                ", checkmark=" + checkmark +
                "} ";
    }
}