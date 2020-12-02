package guis.basics;

import guis.GuiComponent;
import guis.GuiInterface;
import guis.constraints.GuiConstraints;
import guis.constraints.GuiConstraintsManager;
import guis.presets.Background;

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

    public GuiBasics(GuiInterface parent, Background<?> background, GuiConstraints width, GuiConstraints height) {
        super(parent, background);

        initConstraints(width, height);
    }

    public GuiBasics(GuiInterface parent, Background<?> background, GuiConstraintsManager guiConstraintsManager) {
        super(parent, background);

        setConstraints(guiConstraintsManager);
    }

    public void setXConstraint(GuiConstraints xConstraint) {
        GuiConstraintsManager constraints = new GuiConstraintsManager();
        constraints.setxConstraint(xConstraint);

        update(constraints);
    }

    public void setYConstraint(GuiConstraints yConstraint) {
        GuiConstraintsManager constraints = new GuiConstraintsManager();
        constraints.setyConstraint(yConstraint);

        update(constraints);
    }

    public void setWidthConstraint(GuiConstraints widthConstraint) {
        GuiConstraintsManager constraints = new GuiConstraintsManager();
        constraints.setWidthConstraint(widthConstraint);

        update(constraints);
    }

    public void setHeightConstraint(GuiConstraints heightConstraint) {
        GuiConstraintsManager constraints = new GuiConstraintsManager();
        constraints.setHeightConstraint(heightConstraint);

        update(constraints);
    }

    void update(GuiConstraintsManager constraints) {
        setConstraints(constraints);
    }

    @Override
    public String toString() {
        return "baseWidth=" + baseWidth +
                ", baseHeight=" + baseHeight +
                ", " + super.toString();
    }
}
