package guis.constraints;

import guis.constraints.GuiConstraintsManager.Constraints;
import guis.constraints.GuiConstraintsManager.ConstraintsType;

public class CenterConstraint extends GuiConstraints {

    public CenterConstraint() {
        super(ConstraintsType.POSITION, Constraints.CENTER);

        this.constraint = 0f;
    }
}
