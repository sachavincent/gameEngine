package guis.presets;

import static renderEngine.GuiRenderer.PROGRESS_ICON;

import guis.GuiInterface;
import guis.constraints.GuiConstraintsManager;
import renderEngine.GuiRenderer;

public class GuiProgressIcon extends GuiPreset {

    protected float progressPercentage;

    public GuiProgressIcon(GuiInterface parent, Background<?> icon, GuiConstraintsManager constraintsManager) {
        super(parent, icon, constraintsManager);

        this.type = PROGRESS_ICON;
    }

    public void setProgressPercentage(float progressPercentage) {
        assert progressPercentage >= 0;
        assert progressPercentage <= 100;

        this.progressPercentage = progressPercentage;
    }

    public float getProgressPercentage() {
        return this.progressPercentage;
    }

    @Override
    public void render() {
        GuiRenderer.loadPercentage(this.progressPercentage / 100f);

        super.render();
    }

    @Override
    public String toString() {
        return "GuiProgressIcon{" +
                "progressPercentage=" + this.progressPercentage +
                "} ";
    }
}
