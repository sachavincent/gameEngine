package guis.presets;

import guis.GuiInterface;
import guis.basics.GuiRectangle;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.PixelConstraint;
import guis.constraints.RelativeConstraint;

public class GuiSlider extends GuiPreset {

    public GuiSlider(GuiInterface parent, String texture) {
        super(parent, texture);


        GuiConstraintsManager constraints = new GuiConstraintsManager();
        constraints.setDefault();
        constraints.setWidthConstraint(new PixelConstraint(100));
        constraints.setHeightConstraint(new PixelConstraint(25));
        setConstraints(constraints);

        this.getBasics().add(new GuiRectangle(this, texture, new RelativeConstraint(1f, this),
                new RelativeConstraint(.32f, this)));
        this.getBasics().add(new GuiRectangle(this, texture, new RelativeConstraint(.08f, this),
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
