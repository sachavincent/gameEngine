package guis.constraints;

import guis.exceptions.IllegalGuiConstraintException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GuiConstraintsManager {

    private GuiConstraints xConstraint, yConstraint, widthConstraint, heightConstraint;

    private List<Character> order = new ArrayList<>();

    public void setDefault() {
        this.xConstraint = new CenterConstraint();
        this.yConstraint = new CenterConstraint();
        this.widthConstraint = new RelativeConstraint(0.1f);
        this.heightConstraint = new RelativeConstraint(0.1f);

        this.order = new ArrayList<>(Arrays.asList('X', 'Y', 'W', 'H'));
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
        if (xConstraint == null || xConstraint.getConstraintType() == ConstraintsType.DIMENSION)
            throw new IllegalGuiConstraintException();

        this.xConstraint = xConstraint;

        this.order.remove(Character.valueOf('X'));
        this.order.add('X');
    }

    public void setyConstraint(GuiConstraints yConstraint) {
        if (yConstraint == null || yConstraint.getConstraintType() == ConstraintsType.DIMENSION)
            throw new IllegalGuiConstraintException();

        this.yConstraint = yConstraint;

        this.order.remove(Character.valueOf('Y'));
        this.order.add('Y');
    }

    public void setWidthConstraint(GuiConstraints widthConstraint) {
        if (widthConstraint == null || widthConstraint.getConstraintType() == ConstraintsType.POSITION)
            throw new IllegalGuiConstraintException();

        this.widthConstraint = widthConstraint;

        this.order.remove(Character.valueOf('W'));
        this.order.add('W');
    }

    public void setHeightConstraint(GuiConstraints heightConstraint) {
        if (heightConstraint == null || heightConstraint.getConstraintType() == ConstraintsType.POSITION)
            throw new IllegalGuiConstraintException();

        this.heightConstraint = heightConstraint;

        this.order.remove(Character.valueOf('H'));
        this.order.add('H');
    }

    public List<Character> getOrder() {
        return this.order;
    }

    public enum Constraints {
        ASPECT,
        CENTER,
        RELATIVE,
        SIDE,
        PIXEL
    }

    public enum ConstraintsType {
        DIMENSION,
        POSITION,
        BOTH
    }
}
