package guis.constraints;

import guis.GuiInterface;
import guis.constraints.GuiConstraintsManager.Constraints;
import guis.constraints.GuiConstraintsManager.ConstraintsType;

public class StickyConstraint extends GuiConstraints {

    private final Side side;

    private final static float DISTANCE_FROM_SIDE = 0.01f;

    private final Constraints distanceType; // Distance can be given in pixels or relative to parent

    private StickyConstraint(Side side, GuiInterface relativeTo, float distance, Constraints type) {
        super(ConstraintsType.POSITION, Constraints.STICKY);

        this.relativeTo = relativeTo;
        this.side = side;
        this.constraint = distance;
        this.distanceType = type;
    }

    public StickyConstraint(Side side, GuiInterface relativeTo) {
        this(side, relativeTo, DISTANCE_FROM_SIDE);
    }

    public StickyConstraint(Side side, GuiInterface relativeTo, float distance) {
        this(side, relativeTo, distance, Constraints.RELATIVE);
    }

    public StickyConstraint(Side side, float distance) {
        this(side, null, distance);
    }

    public StickyConstraint(Side side) {
        this(side, null);
    }

    public StickyConstraint(Side side, GuiInterface relativeTo, int distance) {
        this(side, relativeTo, distance, Constraints.PIXEL);
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
