package guis.basics;

import static renderEngine.GuiRenderer.filledCircle;

import guis.GuiInterface;
import guis.constraints.GuiConstraints;
import guis.presets.Background;
import models.RawModel;
import org.lwjgl.opengl.GL11;

public class GuiEllipse extends GuiShape {

    public GuiEllipse(GuiInterface gui, Background<?> texture, GuiConstraints width, GuiConstraints height) {
        this(gui, texture, width, height, true);
    }

    public GuiEllipse(GuiInterface gui, Background<?> texture, GuiConstraints width, GuiConstraints height,
            boolean filled) {
        super(gui, texture, width, height, filled);

        this.outlineWidth = 5;
    }

    @Override
    public float getCornerRadius() {
        return 0;
    }

    @Override
    public int getRenderingMode() {
        return GL11.GL_TRIANGLE_FAN;
    }

    @Override
    public RawModel getTemplate() {
        return filledCircle;
    }
}
