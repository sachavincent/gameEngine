package guis.basics;

import guis.GuiInterface;
import guis.constraints.GuiConstraints;
import guis.constraints.GuiConstraintsManager;
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

    public GuiRectangle(GuiInterface gui, GuiBackground<?> background, GuiConstraintsManager guiConstraintsManager) {
        super(gui, background, guiConstraintsManager, true);
    }

    public GuiRectangle(GuiInterface gui, GuiBackground<?> background, GuiConstraintsManager guiConstraintsManager,
            boolean filled) {
        super(gui, background, guiConstraintsManager, filled);
    }

    @Override
    public String toString() {
        return "GuiRectangle{" + super.toString() + "}";
    }
}
