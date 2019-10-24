package guis.basics;

import guis.GuiComponent;
import guis.GuiInterface;
import guis.constraints.GuiConstraints;
import guis.constraints.GuiConstraintsManager;
import java.awt.Color;

public abstract class GuiBasics extends GuiComponent {

    private float baseWidth, baseHeight;

    public float getBaseWidth() {
        return this.baseWidth;
    }

    public float getBaseHeight() {
        return this.baseHeight;
    }

    private void initConstraints(GuiConstraints width, GuiConstraints height) {
        GuiConstraintsManager constraints = new GuiConstraintsManager();
        constraints.setHeightConstraint(height);
        constraints.setWidthConstraint(width);

        setConstraints(constraints);

        this.baseHeight = getHeight();
        this.baseWidth = getWidth();

        update(constraints);
    }

    public GuiBasics(GuiInterface parent, String texture, GuiConstraints width, GuiConstraints height) {
        super(parent, texture);

        initConstraints(width, height);
    }

    public GuiBasics(GuiInterface parent, Color color, GuiConstraints width, GuiConstraints height) {
        super(parent, color);

        initConstraints(width, height);
    }

    public abstract void setXPreset(GuiConstraints xConstraint);

    public abstract void setYPreset(GuiConstraints yConstraint);

    public abstract void setWidthPreset(GuiConstraints widthConstraint);

    public abstract void setHeightPreset(GuiConstraints heightConstraint);

    public void update(GuiConstraintsManager constraints) {
        setConstraints(constraints);
    }
}
