package guis.constraints;

import guis.constraints.GuiConstraintsManager.Constraints;
import guis.constraints.GuiConstraintsManager.ConstraintsType;

public class SideConstraint extends GuiConstraints {

    private final Side side;

    private final static float DISTANCE_FROM_SIDE = 0.01f;

    public SideConstraint(Side side) {
        super(ConstraintsType.POSITION, Constraints.SIDE);

        this.side = side;

        this.constraint = DISTANCE_FROM_SIDE;
    }

    public Side getSide() {
        return this.side;
    }

    public SideConstraint setDistanceFromSide(float distance) {
        assert distance >= 0;

        this.constraint = distance;

        return this;
    }
}
