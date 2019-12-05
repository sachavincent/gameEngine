package guis.basics;

import guis.GuiInterface;
import guis.constraints.GuiConstraints;
import guis.presets.GuiBackground;

public class GuiRectangle extends GuiShape {

    public final static float[] POSITIONS_FILLED   = {-1, 1, -1, -1, 1, 1, 1, -1};
    public final static float[] POSITIONS_UNFILLED = {-1, -1, -1, 1, 1, 1, 1, -1};

    public GuiRectangle(GuiInterface gui, GuiBackground<?> background, GuiConstraints width, GuiConstraints height) {
        super(gui, background, width, height, true);
    }

    public GuiRectangle(GuiInterface gui, GuiBackground<?> background, GuiConstraints width, GuiConstraints height,
            boolean filled) {
        super(gui, background, width, height, filled);
    }

    @Override
    public String toString() {
        return "GuiRectangle{" + super.toString() + "}";
    }
}
