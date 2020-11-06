package guis.constraints;

import guis.GuiComponent;
import guis.GuiInterface;
import guis.constraints.GuiConstraintsManager.Constraints;
import guis.constraints.GuiConstraintsManager.ConstraintsType;

public abstract class GuiGlobalConstraints extends GuiConstraints {

    private final Object[] args;

    protected int nbElements;

    protected GuiInterface parent;

    public void setParent(GuiInterface parent) {
        this.parent = parent;
    }

    public GuiGlobalConstraints(ConstraintsType constraintsType, Constraints constraints, Object... args) {
        super(constraintsType, constraints);

        this.args = args;
    }

    public Object[] getArguments() {
        return this.args;
    }

    public abstract GuiConstraintsManager addElement(GuiComponent guiComponent);
}
