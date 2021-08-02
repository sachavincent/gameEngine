package guis.constraints;

import guis.GuiInterface;
import guis.constraints.GuiConstraintsManager.Constraints;
import guis.constraints.GuiConstraintsManager.ConstraintsType;

public class CenterConstraint extends GuiConstraints {

    public CenterConstraint(GuiInterface relativeTo) {
        super(ConstraintsType.POSITION, Constraints.CENTER);

        this.constraint = 0f;
        this.relativeTo = relativeTo;
    }

    public CenterConstraint() {
        this(null);
    }
}
