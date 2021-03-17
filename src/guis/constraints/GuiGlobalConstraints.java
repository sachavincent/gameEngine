package guis.constraints;

import guis.GuiComponent;
import guis.GuiInterface;
import guis.constraints.GuiConstraintsManager.Constraints;
import guis.constraints.GuiConstraintsManager.ConstraintsType;
import java.util.ArrayList;
import java.util.List;

public abstract class GuiGlobalConstraints extends GuiConstraints {

    private final Object[] args;

    protected int nbElements;

    protected GuiInterface parent;

    protected List<GuiComponent> components;

    public void setParent(GuiInterface parent) {
        this.parent = parent;
    }

    public GuiGlobalConstraints(ConstraintsType constraintsType, Constraints constraints, Object... args) {
        super(constraintsType, constraints);

        this.args = args;
        this.components = new ArrayList<>();
    }

    public Object[] getArguments() {
        return this.args;
    }

    public GuiInterface getParent() {
        return this.parent;
    }

    public void addComponent(GuiComponent guiComponent) {
        guiComponent.setConstraints(addElement());

        this.components.add(guiComponent);
    }

    protected abstract GuiConstraintsManager addElement();
}
