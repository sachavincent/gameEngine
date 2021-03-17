package guis.basics;

import static renderEngine.DisplayManager.MAX_LINE_WIDTH;
import static renderEngine.DisplayManager.MIN_LINE_WIDTH;
import static renderEngine.GuiRenderer.filledQuad;
import static renderEngine.GuiRenderer.unfilledQuad;

import fontMeshCreator.Line;
import fontMeshCreator.Text;
import guis.GuiInterface;
import guis.constraints.GuiConstraints;
import guis.constraints.GuiConstraintsManager;
import guis.presets.Background;
import java.util.List;
import models.RawModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import renderEngine.GuiRenderer;
import util.math.Vector2f;

public abstract class GuiShape extends GuiBasics {

    protected boolean filled;
    private double  outlineWidth = 0;

    public GuiShape(GuiInterface parent, Background<?> background, GuiConstraints width, GuiConstraints height,
            boolean filled) {
        super(parent, background, width, height);

        this.filled = filled;
    }

    public GuiShape(GuiInterface parent, Background<?> background, GuiConstraintsManager guiConstraintsManager,
            boolean filled) {
        super(parent, background, guiConstraintsManager);

        this.filled = filled;
    }

    public boolean isFilled() {
        return this.filled;
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
    }

    @Override
    public String toString() {
        return "GuiShape{" +
                "filled=" + filled +
                "} ";
    }

    public double getOutlineWidth() {
        return this.outlineWidth;
    }

    public void setOutlineWidth(double outlineWidth) {
        if (outlineWidth > MAX_LINE_WIDTH)
            outlineWidth = MAX_LINE_WIDTH;
        else if (outlineWidth < MIN_LINE_WIDTH)
            outlineWidth = MIN_LINE_WIDTH;

        this.outlineWidth = outlineWidth;
    }

    @Override
    public RawModel getTemplate() {
        return filled ? filledQuad : unfilledQuad;
    }

    public int getRenderingMode() {
        return filled ? GL11.GL_TRIANGLE_STRIP : GL11.GL_LINE_LOOP;
    }

    @Override
    public void render() {
        GL30.glBindVertexArray(getTemplate().getVaoID());
        GL20.glEnableVertexAttribArray(0);

        if (isFilled()) {
            GuiRenderer.loadTexture(getTexture(), getCornerRadius());

            GL11.glDrawArrays(getRenderingMode(), 0, getTemplate().getVertexCount());
        } else
            GuiRenderer.renderUnfilledShape(this, getRenderingMode(), getCornerRadius());
    }
}