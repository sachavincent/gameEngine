package guis.presets;

import guis.GuiInterface;
import guis.basics.GuiRectangle;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RelativeConstraint;
import java.awt.Color;

public class GuiSlider extends GuiPreset {

    public GuiSlider(GuiInterface parent, String texture, GuiConstraintsManager constraintsManager) {
        super(parent);

        setConstraints(constraintsManager);

        this.getBasics().add(new GuiRectangle(this, texture, new RelativeConstraint(1f, this),
                new RelativeConstraint(.32f, this)));
        this.getBasics().add(new GuiRectangle(this, texture, new RelativeConstraint(.08f, this),
                new RelativeConstraint(1, this)));
    }

    public GuiSlider(GuiInterface parent, GuiConstraintsManager constraintsManager, Color colorMovable, Color color) {
        super(parent);

        setConstraints(constraintsManager);

        this.getBasics().add(new GuiRectangle(this, colorMovable, new RelativeConstraint(1f, this),
                new RelativeConstraint(.32f, this)));
        this.getBasics().add(new GuiRectangle(this, color, new RelativeConstraint(.08f, this),
                new RelativeConstraint(1, this)));
    }

    @Override
    public void onClick() {
        System.out.println("Click");
    }

    @Override
    public void onHover() {

    }

    @Override
    public void onScroll() {

    }

    @Override
    public void onType() {

    }

}
