package guis.constraints;

import guis.components.GuiComponent;
import guis.constraints.GuiConstraintsManager.Constraints;
import guis.constraints.GuiConstraintsManager.ConstraintsType;

public class RelativeConstraint extends GuiConstraints {

    private GuiComponent relativeTo;

    public RelativeConstraint(float relative, GuiComponent relativeTo) {
        super(ConstraintsType.BOTH, Constraints.RELATIVE);

        if (relative < -1 || relative > 1)
            return;

        this.relativeTo = relativeTo;
        this.constraint = relative;
    }

    public RelativeConstraint(float relative) {
        super(ConstraintsType.BOTH, Constraints.RELATIVE);

        if (relative < 0 || relative > 11)
            return;

        this.constraint = relative;
    }

    public GuiComponent getRelativeTo() {
        return this.relativeTo;
    }
}
