package guis.constraints;

import guis.GuiInterface;
import guis.constraints.GuiConstraintsManager.Constraints;
import guis.constraints.GuiConstraintsManager.ConstraintsType;

public class SideConstraint extends GuiConstraints {

    private final Side side;

    public final static float DISTANCE_FROM_SIDE = 0.01f;

    private final Constraints distanceType; // Distance can be given in pixels or relative to parent

    private SideConstraint(Side side, float distanceFromSide, Constraints type, GuiInterface relativeTo) {
        super(ConstraintsType.POSITION, Constraints.SIDE);

        this.side = side;
        this.distanceType = type;
        this.constraint = distanceFromSide;
        this.relativeTo = relativeTo;
    }

    public SideConstraint(Side side) {
        this(side, null);
    }

    public SideConstraint(Side side, GuiInterface relativeTo) {
        this(side, DISTANCE_FROM_SIDE, Constraints.RELATIVE, relativeTo);
    }

    public SideConstraint(Side side, float distanceFromSide) {
        this(side, distanceFromSide, Constraints.RELATIVE, null);
    }

    public SideConstraint(Side side, float distanceFromSide, GuiInterface relativeTo) {
        this(side, distanceFromSide, Constraints.RELATIVE, relativeTo);
    }

    public SideConstraint(Side side, int distanceFromSide) {
        this(side, distanceFromSide, Constraints.PIXEL, null);
    }

    public SideConstraint(Side side, int distanceFromSide, GuiInterface relativeTo) {
        this(side, distanceFromSide, Constraints.PIXEL, relativeTo);
    }

    public Constraints getDistanceType() {
        return this.distanceType;
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
