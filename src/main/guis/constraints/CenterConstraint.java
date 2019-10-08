package main.guis.constraints;

import main.guis.constraints.GuiConstraintsManager.Constraints;
import main.guis.constraints.GuiConstraintsManager.ConstraintsType;

public class CenterConstraint extends GuiConstraints {

    public CenterConstraint() {
        super(ConstraintsType.POSITION, Constraints.CENTER);

        this.constraint = 0f;
    }
}
