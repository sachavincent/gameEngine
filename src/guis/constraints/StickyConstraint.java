package guis.constraints;

import guis.GuiInterface;
import guis.constraints.GuiConstraintsManager.Constraints;
import guis.constraints.GuiConstraintsManager.ConstraintsType;

public class StickyConstraint extends GuiConstraints {

    private final GuiInterface relativeTo;

    private final Side side;

    private final static float DISTANCE_FROM_SIDE = 0.01f;

    public StickyConstraint(Side side, GuiInterface relativeTo) {
        super(ConstraintsType.POSITION, Constraints.STICKY);

        this.relativeTo = relativeTo;
        this.side = side;
        this.constraint = DISTANCE_FROM_SIDE;
    }

    public StickyConstraint(Side side) {
        this(side, null);
    }

    public Side getSide() {
        return this.side;
    }

    public GuiInterface getRelativeTo() {
        return this.relativeTo;
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
