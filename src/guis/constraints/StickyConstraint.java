package guis.constraints;

import guis.GuiInterface;
import guis.constraints.GuiConstraintsManager.Constraints;
import guis.constraints.GuiConstraintsManager.ConstraintsType;

public class StickyConstraint extends GuiConstraints {

    private final Side side;

    private final static float DISTANCE_FROM_SIDE = 0.01f;

    private final Constraints distanceType; // Distance can be given in pixels or relative to parent

    private StickyConstraint(Side side, float distanceFromSide, Constraints type, GuiInterface relativeTo) {
        super(ConstraintsType.POSITION, Constraints.STICKY);

        this.relativeTo = relativeTo;
        this.side = side;
        this.constraint = distanceFromSide;
        this.distanceType = type;
    }

    public StickyConstraint(Side side) {
        this(side, null);
    }

    public StickyConstraint(Side side, GuiInterface relativeTo) {
        this(side, DISTANCE_FROM_SIDE, Constraints.RELATIVE, relativeTo);
    }

    public StickyConstraint(Side side, float distanceFromSide) {
        this(side, distanceFromSide, Constraints.RELATIVE, null);
    }

    public StickyConstraint(Side side, float distanceFromSide, GuiInterface relativeTo) {
        this(side, distanceFromSide, Constraints.RELATIVE, relativeTo);
    }

    public StickyConstraint(Side side, int distanceFromSide) {
        this(side, distanceFromSide, Constraints.PIXEL, null);
    }

    public StickyConstraint(Side side, int distanceFromSide, GuiInterface relativeTo) {
        this(side, distanceFromSide, Constraints.PIXEL, relativeTo);
    }

    public Side getSide() {
        return this.side;
    }

    public Constraints getDistanceType() {
        return this.distanceType;
    }

    public StickyConstraint setDistanceFromSide(float distance) {
        this.constraint = distance;

        return this;
    }

    public StickyConstraint setDistanceFromSide(double distance) {
        this.constraint = (float) distance;

        return this;
    }
}
