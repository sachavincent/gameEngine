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
        if (width == null || height == null)
            return;

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

    public void setXPreset(GuiConstraints xConstraint) {
        GuiConstraintsManager constraints = new GuiConstraintsManager();
        constraints.setxConstraint(xConstraint);

        update(constraints);
    }

    public void setYPreset(GuiConstraints yConstraint) {
        GuiConstraintsManager constraints = new GuiConstraintsManager();
        constraints.setyConstraint(yConstraint);

        update(constraints);
    }

    public void setWidthPreset(GuiConstraints widthConstraint) {
        GuiConstraintsManager constraints = new GuiConstraintsManager();
        constraints.setWidthConstraint(widthConstraint);

        update(constraints);
    }

    public void setHeightPreset(GuiConstraints heightConstraint) {
        GuiConstraintsManager constraints = new GuiConstraintsManager();
        constraints.setHeightConstraint(heightConstraint);

        update(constraints);
    }

    public void update(GuiConstraintsManager constraints) {
        setConstraints(constraints);
    }
}
