package guis.constraints;

import guis.constraints.GuiConstraintsManager.Constraints;
import guis.constraints.GuiConstraintsManager.ConstraintsType;

public abstract class GuiConstraints {

    float constraint;

    private Constraints constraints;

    private ConstraintsType constraintsType;

    public GuiConstraints(ConstraintsType constraintsType, Constraints constraints) {
        this.constraintsType = constraintsType;
        this.constraints = constraints;
    }

    public Constraints getConstraint() {
        return this.constraints;
    }

    public ConstraintsType getConstraintType() {
        return this.constraintsType;
    }

    public float constraint() {
        return this.constraint;
    }
}
