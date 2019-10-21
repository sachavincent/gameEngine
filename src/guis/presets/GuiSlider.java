package guis.presets;

import guis.Gui;
import guis.basics.GuiRectangle;
import guis.constraints.PixelConstraint;

public class GuiSlider extends GuiPreset {

    public GuiSlider(Gui parent, String texture) {
        super(parent, texture);

        this.getBasics().add(new GuiRectangle(parent, texture, new PixelConstraint(150), new PixelConstraint(10)));
        this.getBasics().add(new GuiRectangle(parent, texture, new PixelConstraint(10), new PixelConstraint(50)));
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
