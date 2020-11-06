package guis.constraints;

import guis.GuiComponent;
import guis.constraints.GuiConstraintsManager.Constraints;
import guis.constraints.GuiConstraintsManager.ConstraintsType;

public class StickyConstraint extends GuiConstraints {

    private final GuiComponent relativeTo;

    public StickyConstraint(StickySide side, GuiComponent relativeTo) {
        super(ConstraintsType.POSITION, Constraints.STICKY);

        if (relativeTo == null)
            throw new NullPointerException("Null relative interface");

        this.relativeTo = relativeTo;
        this.constraint = side.getNum();
    }

    public GuiComponent getRelativeTo() {
        return this.relativeTo;
    }

    public enum StickySide {
        UP,
        DOWN,
        LEFT,
        RIGHT;

        float getNum() {
            return ordinal();
        }

        public StickySide getSideFromNum(int num) {
            for (StickySide side : StickySide.values())
                if (side.ordinal() == num)
                    return side;

            return null;
        }
    }
}
