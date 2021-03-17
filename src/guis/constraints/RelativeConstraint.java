package guis.constraints;

import guis.GuiInterface;
import guis.constraints.GuiConstraintsManager.Constraints;
import guis.constraints.GuiConstraintsManager.ConstraintsType;

public class RelativeConstraint extends GuiConstraints {

    public RelativeConstraint(float relative, GuiInterface relativeTo) {
        super(ConstraintsType.BOTH, Constraints.RELATIVE);

        if (relativeTo == null)
            throw new NullPointerException("Null relative interface");

        if (relative < -1 || relative > 1)
            throw new IllegalArgumentException("Relative value: " + relative + " not allowed");

        this.relativeTo = relativeTo;
        this.constraint = relative;
    }

    public RelativeConstraint(float relative) {
        super(ConstraintsType.BOTH, Constraints.RELATIVE);

        if (relative < 0 || relative > 1)
            throw new IllegalArgumentException("Relative value: " + relative + " not allowed");

        this.constraint = relative;
    }
}
