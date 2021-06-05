package guis.presets;

import static renderEngine.GuiRenderer.filledQuad;

import guis.GuiComponent;
import guis.GuiInterface;
import guis.constraints.GuiConstraintsManager;
import models.RawModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import renderEngine.GuiRenderer;

public abstract class GuiPreset extends GuiComponent {

    protected GuiPreset(GuiInterface parent, GuiConstraintsManager constraintsManager) {
        this(parent, Background.NO_BACKGROUND, constraintsManager);
    }

    protected GuiPreset(GuiInterface parent, Background<?> background, GuiConstraintsManager constraintsManager) {
        this(parent, background);

        setConstraints(constraintsManager);
    }

    protected GuiPreset(GuiInterface parent, Background<?> background) {
        super(parent, background);
    }

    @Override
    public RawModel getTemplate() {
        return filledQuad;
    }

    @Override
    public void render() {
        GL30.glBindVertexArray(getTemplate().getVaoID());
        GL20.glEnableVertexAttribArray(0);

        GuiRenderer.loadGui(this);
        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, getTemplate().getVertexCount());
    }
}
