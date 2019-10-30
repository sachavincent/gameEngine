package guis.constraints;

import guis.GuiComponent;
import guis.constraints.GuiConstraintsManager.Constraints;
import guis.constraints.GuiConstraintsManager.ConstraintsType;

public class PixelConstraint extends GuiConstraints {

    private GuiComponent relativeTo;

    public PixelConstraint(int pixel, GuiComponent relativeTo) {
        super(ConstraintsType.BOTH, Constraints.PIXEL);

        this.relativeTo = relativeTo;
        this.constraint = pixel;
    }

    public PixelConstraint(int pixel) {
        super(ConstraintsType.BOTH, Constraints.PIXEL);

        this.constraint = pixel;
    }

    public GuiComponent getRelativeTo() {
        return this.relativeTo;
    }
}
