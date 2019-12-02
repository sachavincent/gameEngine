package guis.basics;

import guis.GuiInterface;
import guis.constraints.GuiConstraints;
import guis.presets.GuiBackground;

public class GuiRectangle extends GuiShape {

    public GuiRectangle(GuiInterface gui, GuiBackground<?> background, GuiConstraints width, GuiConstraints height) {
        super(gui, background, width, height, true);
    }

    public GuiRectangle(GuiInterface gui, GuiBackground<?> background, GuiConstraints width, GuiConstraints height,
            boolean filled) {
        super(gui, background, width, height, filled);
    }

    @Override
    public String toString() {
        return "GuiRectangle{" + super.toString() + "}";
    }
}
