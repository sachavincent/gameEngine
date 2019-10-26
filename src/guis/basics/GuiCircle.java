package guis.basics;

import guis.Gui;
import guis.constraints.AspectConstraint;
import guis.constraints.GuiConstraints;
import java.awt.Color;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import renderEngine.DisplayManager;

public class GuiCircle extends GuiBasics {

    public GuiCircle(Gui gui, String texture, GuiConstraints dimension) {
        super(gui, texture, new AspectConstraint(1f), dimension);
    }

    public GuiCircle(Gui gui, Color color, GuiConstraints dimension) {
        super(gui, color, new AspectConstraint(1f), dimension);
    }


}
