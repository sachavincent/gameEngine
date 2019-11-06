package guis.basics;

import guis.GuiInterface;
import guis.constraints.GuiConstraints;
import java.awt.Color;

public class GuiEllipse extends GuiShape {

    public GuiEllipse(GuiInterface gui, String texture, GuiConstraints width, GuiConstraints height) {
        super(gui, texture, width, height, true);
    }

    public GuiEllipse(GuiInterface gui, Color color, GuiConstraints width, GuiConstraints height) {
        super(gui, color, width, height, true);
    }

    public GuiEllipse(GuiInterface gui, String texture, GuiConstraints width, GuiConstraints height, boolean filled) {
        super(gui, texture, width, height, filled);
    }

    public GuiEllipse(GuiInterface gui, Color color, GuiConstraints width, GuiConstraints height, boolean filled) {
        super(gui, color, width, height, filled);
    }

    @Override
    public String toString() {
        return "GuiOval{ " + super.toString() + "}";
    }
}
