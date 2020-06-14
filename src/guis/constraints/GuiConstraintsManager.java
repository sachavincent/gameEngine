package guis.constraints;

import guis.exceptions.IllegalGuiConstraintException;
import java.util.ArrayList;
import java.util.List;

public class GuiConstraintsManager {

    private GuiConstraints xConstraint, yConstraint, widthConstraint, heightConstraint;

    private List<Character> order = new ArrayList<>();

    public GuiConstraintsManager setDefault() {
        setxConstraint(new CenterConstraint());
        setyConstraint(new CenterConstraint());
        setWidthConstraint(new RelativeConstraint(0.1f));
        setHeightConstraint(new RelativeConstraint(0.1f));

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

    public static class Builder {

        private GuiConstraintsManager guiConstraintsManager;

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
