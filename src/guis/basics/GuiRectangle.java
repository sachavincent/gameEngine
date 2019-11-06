package guis.basics;

import guis.GuiInterface;
import guis.constraints.GuiConstraints;
import java.awt.Color;

public class GuiRectangle extends GuiShape {

    public GuiRectangle(GuiInterface gui, String texture, GuiConstraints width, GuiConstraints height) {
        super(gui, texture, width, height, true);
    }

    public GuiRectangle(GuiInterface gui, Color color, GuiConstraints width, GuiConstraints height) {
        super(gui, color, width, height, true);
    }

    public GuiRectangle(GuiInterface gui, String texture, GuiConstraints width, GuiConstraints height, boolean filled) {
        super(gui, texture, width, height, filled);
    }

    public GuiRectangle(GuiInterface gui, Color color, GuiConstraints width, GuiConstraints height, boolean filled) {
        super(gui, color, width, height, filled);
    }

    @Override
    public String toString() {
        return "GuiRectangle{" + super.toString() + "}";
    }
}
