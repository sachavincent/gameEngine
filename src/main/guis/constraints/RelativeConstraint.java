package main.guis.constraints;

import main.guis.GuiComponent;
import main.guis.constraints.GuiConstraintsManager.Constraints;
import main.guis.constraints.GuiConstraintsManager.ConstraintsType;

public class RelativeConstraint extends GuiConstraints {

    private GuiComponent relativeTo;

    public RelativeConstraint(float relative, GuiComponent relativeTo) {
        super(ConstraintsType.POSITION, Constraints.RELATIVE);

        if (relative < 0 || relative > 1)
            return;

        this.relativeTo = relativeTo;
        this.constraint = relative;
    }

    public RelativeConstraint(float relative) {
        super(ConstraintsType.POSITION, Constraints.RELATIVE);

        if (relative < 0 || relative > 1)
            return;

        this.constraint = relative;
    }

    public GuiComponent getRelativeTo() {
        return this.relativeTo;
    }
}
