package guis.constraints;

import guis.GuiInterface;
import guis.constraints.GuiConstraintsManager.Constraints;
import guis.constraints.GuiConstraintsManager.ConstraintsType;

public class RelativeConstraint extends GuiConstraints {

    private GuiInterface relativeTo;

    public RelativeConstraint(float relative, GuiInterface relativeTo) {
        super(ConstraintsType.BOTH, Constraints.RELATIVE);

        if (relative < -1 || relative > 1)
            return;

        this.relativeTo = relativeTo;
        this.constraint = relative;
    }

    public RelativeConstraint(float relative) {
        super(ConstraintsType.BOTH, Constraints.RELATIVE);

        if (relative < 0 || relative > 1)
            return;

        this.constraint = relative;
    }

    public GuiInterface getRelativeTo() {
        return this.relativeTo;
    }
}
