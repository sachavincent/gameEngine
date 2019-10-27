package guis.basics;

import guis.GuiInterface;
import guis.constraints.AspectConstraint;
import guis.constraints.GuiConstraints;
import guis.constraints.GuiConstraintsManager;
import java.awt.Color;

public class GuiOval extends GuiBasics {

    public GuiOval(GuiInterface gui, String texture, GuiConstraints width, GuiConstraints height) {
        super(gui, texture, width, height);
    }

    public GuiOval(GuiInterface gui, Color color, GuiConstraints width, GuiConstraints height) {
        super(gui, color, width, height);
    }

    @Override
    public String toString() {
        return "GuiOval{ " + super.toString() + "}";
    }
}
