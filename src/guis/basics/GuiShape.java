package guis.basics;

import static renderEngine.GuiRenderer.filledQuad;

import guis.GuiInterface;
import guis.constraints.GuiConstraints;
import guis.constraints.GuiConstraintsManager;
import guis.presets.Background;
import java.awt.Color;
import models.RawModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import renderEngine.DisplayManager;
import renderEngine.GuiRenderer;
import util.math.Maths;

public abstract class GuiShape extends GuiBasics {

    protected boolean filled;
    protected int     outlineWidth = 2;
    protected Color   borderColor;
    protected boolean borderEnabled = false;

    public GuiShape(GuiInterface parent, Background<?> background, GuiConstraints width, GuiConstraints height,
            boolean filled) {
        super(parent, background, width, height);

        setFilled(filled);
    }

    public GuiShape(GuiInterface parent, Background<?> background, GuiConstraintsManager guiConstraintsManager,
            boolean filled) {
        super(parent, background, guiConstraintsManager);

        setFilled(filled);
    }

    public boolean isFilled() {
        return this.filled;
    }

    public Color getBorderColor() {
        return this.borderColor;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        this.borderEnabled = this.borderColor != null;
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
    }

    public boolean isBorderEnabled() {
        return this.borderEnabled;
    }

    @Override
    public String toString() {
        return "GuiShape{" +
                "filled=" + filled +
                "} ";
    }

    public int getOutlineWidth() {
        return this.outlineWidth;
    }

    public void setOutlineWidth(int outlineWidth) {
        this.outlineWidth = (int) Maths.clamp(outlineWidth, 0, (int) (this.width * DisplayManager.WIDTH));
    }

    @Override
    public RawModel getTemplate() {
        return filledQuad;
    }

    public int getRenderingMode() {
        return GL11.GL_TRIANGLE_STRIP;
    }

    @Override
    public void render() {
        GL30.glBindVertexArray(getTemplate().getVaoID());
        GL20.glEnableVertexAttribArray(0);

        GuiRenderer.loadGui(this);

        GL11.glDrawArrays(getRenderingMode(), 0, getTemplate().getVertexCount());
    }
}