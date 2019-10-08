package main.guis.constraints;

import main.guis.constraints.GuiConstraintsManager.Constraints;
import main.guis.constraints.GuiConstraintsManager.ConstraintsType;

public class AspectConstraint extends GuiConstraints {

    public AspectConstraint(float aspectRatio) {
        super(ConstraintsType.DIMENSION, Constraints.ASPECT);

        this.constraint = aspectRatio;
    }
}
