package guis.basics;

import guis.GuiInterface;
import guis.constraints.GuiConstraints;
import guis.constraints.GuiConstraintsManager;
import guis.presets.Background;

public class GuiRectangle extends GuiShape {

    public final static int[] POSITIONS_FILLED   = {-1, 1, -1, -1, 1, 1, 1, -1};
    public final static int[] POSITIONS_UNFILLED = {-1, -1, -1, 1, 1, 1, 1, -1};

    public GuiRectangle(GuiInterface parent, Background<?> background, GuiConstraints width, GuiConstraints height) {
        this(parent, background, width, height, true);
    }

    public GuiRectangle(GuiInterface parent, Background<?> background, GuiConstraints width, GuiConstraints height,
            boolean filled) {
        super(parent, background, width, height, filled);
    }

    public GuiRectangle(GuiInterface parent, Background<?> background, GuiConstraintsManager guiConstraintsManager) {
        this(parent, background, guiConstraintsManager, true);
    }

    public GuiRectangle(GuiInterface parent, Background<?> background, GuiConstraintsManager guiConstraintsManager,
            boolean filled) {
        super(parent, background, guiConstraintsManager, filled);
    }

    public GuiRectangle(GuiInterface parent, Background<?> background, boolean filled) {
        this(parent, background, null, filled);
    }

    public GuiRectangle(GuiInterface parent, Background<?> background) {
        this(parent, background, null, true);
    }
}
