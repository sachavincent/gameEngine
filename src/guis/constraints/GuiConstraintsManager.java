package guis.constraints;

import guis.exceptions.IllegalGuiConstraintException;
import java.util.LinkedHashSet;
import java.util.Set;

public class GuiConstraintsManager {

    private GuiConstraints xConstraint, yConstraint, widthConstraint, heightConstraint;

    private final Set<Character> order = new LinkedHashSet<>();

    public GuiConstraintsManager setDefault() {
        setWidthConstraint(new RelativeConstraint(0.1f));
        setHeightConstraint(new RelativeConstraint(0.1f));
        setxConstraint(new CenterConstraint());
        setyConstraint(new CenterConstraint());

        return this;
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
            throw new IllegalGuiConstraintException("Wrong constraint type");

        if ((!this.order.contains('W') || !this.order.contains('H')) && !this.order.contains('X'))
            throw new IllegalGuiConstraintException("Width and height should be set before X");

        this.xConstraint = xConstraint;

        this.order.remove('X');
        this.order.add('X');
    }

    public void setyConstraint(GuiConstraints yConstraint) {
        if (yConstraint == null || yConstraint.getConstraintType() == ConstraintsType.DIMENSION)
            throw new IllegalGuiConstraintException("Wrong constraint type");

        if ((!this.order.contains('W') || !this.order.contains('H')) && !this.order.contains('Y'))
            throw new IllegalGuiConstraintException("Width and height should be set before Y");

        this.yConstraint = yConstraint;

        this.order.remove('Y');
        this.order.add('Y');
    }

    public void setWidthConstraint(GuiConstraints widthConstraint) {
        if (widthConstraint == null || widthConstraint.getConstraintType() == ConstraintsType.POSITION)
            throw new IllegalGuiConstraintException("Wrong constraint type");

        if ((this.order.contains('X') || this.order.contains('Y')) && !this.order.contains('W'))
            throw new IllegalGuiConstraintException("Width should be set after X & Y");

        this.widthConstraint = widthConstraint;

        this.order.remove('W');
        this.order.add('W');
    }

    public void setHeightConstraint(GuiConstraints heightConstraint) {
        if (heightConstraint == null || heightConstraint.getConstraintType() == ConstraintsType.POSITION)
            throw new IllegalGuiConstraintException("Wrong constraint type");

        if ((this.order.contains('X') || this.order.contains('Y')) && !this.order.contains('H'))
            throw new IllegalGuiConstraintException("Height should be set after X & Y");

        this.heightConstraint = heightConstraint;

        this.order.remove('H');
        this.order.add('H');
    }

    public Set<Character> getOrder() {
        return this.order;
    }

    public enum Constraints {
        ASPECT,
        CENTER,
        RELATIVE,
        SIDE,
        PATTERN,
        RATIOED_PATTERN,
        PIXEL,
        STICKY
    }

    public enum ConstraintsType {
        DIMENSION,
        POSITION,
        BOTH
    }

    public static class Builder {

        private final GuiConstraintsManager guiConstraintsManager;

        public Builder() {
            guiConstraintsManager = new GuiConstraintsManager();
        }

        public Builder setDefault() {
            guiConstraintsManager.setDefault();

            return this;
        }

        public Builder setHeightConstraint(GuiConstraints heightConstraint) {
            guiConstraintsManager.setHeightConstraint(heightConstraint);

            return this;
        }

        public Builder setWidthConstraint(GuiConstraints widthConstraint) {
            guiConstraintsManager.setWidthConstraint(widthConstraint);

            return this;
        }

        public Builder setxConstraint(GuiConstraints xConstraint) {
            guiConstraintsManager.setxConstraint(xConstraint);

            return this;
        }

        public Builder setyConstraint(GuiConstraints yConstraint) {
            guiConstraintsManager.setyConstraint(yConstraint);

            return this;
        }

        public GuiConstraintsManager create() {
            return guiConstraintsManager;
        }
    }
}
