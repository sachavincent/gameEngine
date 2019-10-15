package guis.constraints;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import guis.exceptions.IllegalGuiConstraintException;

public class GuiConstraintsManager {

    private GuiConstraints xConstraint, yConstraint, widthConstraint, heightConstraint;

    private List<String> order = new LinkedList<>();

    public void setDefault() {
        this.xConstraint = new CenterConstraint();
        this.yConstraint = new CenterConstraint();
        this.widthConstraint = new RelativeConstraint(0.1f);
        this.heightConstraint = new RelativeConstraint(0.1f);

        this.order = new LinkedList<>(Arrays.asList("X", "Y", "W", "H"));
    }

    public GuiConstraints getxConstraint() {
        return this.xConstraint;
    }

    public GuiConstraints getyConstraint() {
        return this.yConstraint;
    }

    public GuiConstraints getWidthConstraint() {
        return this.widthConstraint;
    }

    public GuiConstraints getHeightConstraint() {
        return this.heightConstraint;
    }

    public void setxConstraint(GuiConstraints xConstraint) {
        if (xConstraint.getConstraintType() == ConstraintsType.DIMENSION)
            throw new IllegalGuiConstraintException();

        this.xConstraint = xConstraint;

        this.order.remove("X");
        this.order.add("X");
    }

    public void setyConstraint(GuiConstraints yConstraint) {
        if (yConstraint.getConstraintType() == ConstraintsType.DIMENSION)
            throw new IllegalGuiConstraintException();

        this.yConstraint = yConstraint;

        this.order.remove("Y");
        this.order.add("Y");
    }

    public void setWidthConstraint(GuiConstraints widthConstraint) {
        if (widthConstraint.getConstraintType() == ConstraintsType.POSITION)
            throw new IllegalGuiConstraintException();

        this.widthConstraint = widthConstraint;

        this.order.remove("W");
        this.order.add("W");
    }

    public void setHeightConstraint(GuiConstraints heightConstraint) {
        if (heightConstraint.getConstraintType() == ConstraintsType.POSITION)
            throw new IllegalGuiConstraintException();

        this.heightConstraint = heightConstraint;

        this.order.remove("H");
        this.order.add("H");
    }

    public List<String> getOrder() {
        return this.order;
    }

    public enum Constraints {
        ASPECT,
        CENTER,
        RELATIVE,
        SIDE
    }

    public enum ConstraintsType {
        DIMENSION,
        POSITION,
        BOTH
    }
}
