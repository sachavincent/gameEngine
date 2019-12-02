package guis.presets.sliders;

import guis.GuiInterface;
import guis.basics.GuiRectangle;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RelativeConstraint;
import guis.presets.GuiBackground;
import java.awt.Color;

public class GuiRectangleSlider extends GuiAbstractSlider {

    public GuiRectangleSlider(GuiInterface parent, Color color, Color colorMovable,
            GuiConstraintsManager constraintsManager) {
        super(parent, color, colorMovable, constraintsManager);
    }

    @Override
    void addCursor(Color color) {
        this.sliderCursor = new GuiRectangle(this, new GuiBackground(color), new RelativeConstraint(.08f, this),
                new RelativeConstraint(1, this));
    }

//    @Override
//    void addCursor(String texture) {
//        sliderCursor = new GuiRectangle(this, texture, new RelativeConstraint(.08f, this),
//                new RelativeConstraint(1, this));
//    }

}
