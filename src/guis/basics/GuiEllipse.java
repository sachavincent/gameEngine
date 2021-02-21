package guis.basics;

import static renderEngine.GuiRenderer.filledCircle;
import static renderEngine.GuiRenderer.unfilledCircle;

import guis.GuiInterface;
import guis.constraints.GuiConstraints;
import guis.presets.Background;
import models.RawModel;
import org.lwjgl.opengl.GL11;

public class GuiEllipse extends GuiShape {

    public GuiEllipse(GuiInterface gui, Background<?> texture, GuiConstraints width, GuiConstraints height) {
        super(gui, texture, width, height, true);
    }

    public GuiEllipse(GuiInterface gui, Background<?> texture, GuiConstraints width, GuiConstraints height,
            boolean filled) {
        super(gui, texture, width, height, filled);
    }

    @Override
    public String toString() {
        return "GuiEllipse{ " + super.toString() + "}";
    }

    public int getRenderingMode() {
        return filled ? GL11.GL_TRIANGLE_FAN : GL11.GL_LINE_STRIP;
    }

    @Override
    public RawModel getTemplate() {
        return filled ? filledCircle : unfilledCircle;
    }
}
