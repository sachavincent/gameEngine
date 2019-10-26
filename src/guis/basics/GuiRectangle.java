package guis.basics;

import guis.GuiInterface;
import guis.constraints.GuiConstraints;
import guis.constraints.GuiConstraintsManager;
import java.awt.Color;

public class GuiRectangle extends GuiBasics {

    public GuiRectangle(GuiInterface gui, String texture, GuiConstraints width, GuiConstraints height) {
        super(gui, texture, width, height);
    }

    public GuiRectangle(GuiInterface gui, Color color, GuiConstraints width, GuiConstraints height) {
        super(gui, color, width, height);
    }
}
