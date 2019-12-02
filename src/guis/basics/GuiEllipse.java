package guis.basics;

import guis.GuiInterface;
import guis.constraints.GuiConstraints;
import guis.presets.GuiBackground;

public class GuiEllipse extends GuiShape {

    public GuiEllipse(GuiInterface gui, GuiBackground<?> texture, GuiConstraints width, GuiConstraints height) {
        super(gui, texture, width, height, true);
    }

    public GuiEllipse(GuiInterface gui, GuiBackground<?> texture, GuiConstraints width, GuiConstraints height,
            boolean filled) {
        super(gui, texture, width, height, filled);
    }

    @Override
    public String toString() {
        return "GuiOval{ " + super.toString() + "}";
    }
}
