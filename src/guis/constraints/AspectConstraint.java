package guis.constraints;

import guis.constraints.GuiConstraintsManager.Constraints;
import guis.constraints.GuiConstraintsManager.ConstraintsType;

public class AspectConstraint extends GuiConstraints {

    public AspectConstraint(float aspectRatio) {
        super(ConstraintsType.DIMENSION, Constraints.ASPECT);

        this.constraint = aspectRatio;
    }
}
