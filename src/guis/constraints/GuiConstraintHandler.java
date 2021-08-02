package guis.constraints;

import guis.Gui;
import guis.GuiInterface;
import guis.constraints.GuiConstraintsManager.Constraints;
import guis.exceptions.IllegalGuiConstraintException;
import renderEngine.DisplayManager;

public class GuiConstraintHandler {

    private final GuiInterface parent;
    private final GuiInterface toHandle;

    public GuiConstraintHandler(GuiInterface parent, GuiInterface toHandle) {
        this.parent = parent;
        this.toHandle = toHandle;
    }

    public GuiConstraintHandler(Gui toHandle) {
        this.parent = toHandle;
        this.toHandle = toHandle;
    }

    public void setConstraints(GuiConstraintsManager guiConstraintsManager) {
        final GuiConstraints xConstraint = guiConstraintsManager.getxConstraint();
        final GuiConstraints yConstraint = guiConstraintsManager.getyConstraint();
        final GuiConstraints widthConstraint = guiConstraintsManager.getWidthConstraint();
        final GuiConstraints heightConstraint = guiConstraintsManager.getHeightConstraint();
        for (char s : guiConstraintsManager.getOrder()) {
            switch (s) {
                case 'W':
                    this.toHandle.setWidth(handleWidthConstraint(widthConstraint, xConstraint));
                    break;
                case 'H':
                    this.toHandle.setHeight(handleHeightConstraint(heightConstraint, yConstraint));
                    break;
                case 'X':
                    this.toHandle.setX(handleXConstraint(xConstraint));
                    break;
                case 'Y':
                    this.toHandle.setY(handleYConstraint(yConstraint));
                    break;
            }
        }
    }

    public float handleWidthConstraint(GuiConstraints widthConstraint) {
        return handleWidthConstraint(widthConstraint, null);
    }

    public float handleWidthConstraint(GuiConstraints widthConstraint, GuiConstraints xConstraint) {
        float width = 0;
        if (widthConstraint == null)
            return width;

        float constraint = widthConstraint.constraint();
        GuiInterface relativeTo = widthConstraint.getRelativeTo();

        switch (widthConstraint.getConstraint()) {
            case RELATIVE:
                if (relativeTo == null)
                    relativeTo = this.parent;

                float xConstraintValue = 0;
                if (xConstraint != null && (xConstraint.getConstraints() == Constraints.SIDE ||
                        xConstraint.getConstraints() == Constraints.STICKY))
                    xConstraintValue = xConstraint.constraint();
                width = (constraint - xConstraintValue) * relativeTo.getWidth();
                break;
            case ASPECT:
                width = constraint * DisplayManager.HEIGHT / DisplayManager.WIDTH * this.toHandle.getHeight();
                break;
            case PIXEL:
                width = constraint / DisplayManager.WIDTH;
                break;
            default:
                throw new IllegalGuiConstraintException("This constraint cannot be handled");
        }

        return width;
    }

    public float handleHeightConstraint(GuiConstraints heightConstraint) {
        return handleHeightConstraint(heightConstraint, null);
    }

    public float handleHeightConstraint(GuiConstraints heightConstraint, GuiConstraints yConstraint) {
        float height = 0;
        if (heightConstraint == null)
            return height;

        float constraint = heightConstraint.constraint();
        GuiInterface relativeTo = heightConstraint.getRelativeTo();

        switch (heightConstraint.getConstraint()) {
            case RELATIVE:
                if (relativeTo == null)
                    relativeTo = this.parent;

                float yConstraintValue = 0;
                if (yConstraint != null && (yConstraint.getConstraints() == Constraints.SIDE ||
                        yConstraint.getConstraints() == Constraints.STICKY))
                    yConstraintValue = yConstraint.constraint();
                height = (constraint - yConstraintValue) * relativeTo.getHeight();
                break;
            case ASPECT:
                height = constraint * DisplayManager.WIDTH / DisplayManager.HEIGHT * this.toHandle.getWidth();
                break;
            case PIXEL:
                height = constraint / DisplayManager.HEIGHT;
                break;
            default:
                throw new IllegalGuiConstraintException("This constraint cannot be handled");
        }

        return height;
    }

    public float handleYConstraint(GuiConstraints yConstraint) {
        float y = 0;
        if (yConstraint == null)
            return y;

        float height = this.toHandle.getHeight();

        float constraint = yConstraint.constraint();
        GuiInterface relativeTo = yConstraint.getRelativeTo();

        switch (yConstraint.getConstraint()) {
            case RELATIVE:
                if (relativeTo != null) { // Relative to another element
                    if (this.toHandle instanceof Gui) {
                        if (constraint == 0)
                            y = relativeTo.getY();
                        else
                            y = relativeTo.getY() - height +
                                    (1f / 2f + height) * constraint * 2;
                    } else
                        y = relativeTo.getY() - (relativeTo.getHeight() - height) * constraint;
                } else {
                    if (this.toHandle instanceof Gui)
                        y = (1 - height) - (1 - height) * 2 * constraint;
                    else
                        y = (this.parent.getY() + this.parent.getHeight() - height) -
                                (this.parent.getHeight() - height) * 2 * constraint;
                }
                break;
            case SIDE:
                if (relativeTo == null)
                    relativeTo = this.parent;

                SideConstraint sideConstraint = (SideConstraint) yConstraint;
                Constraints distanceType = sideConstraint.getDistanceType();
                if (distanceType == Constraints.PIXEL)
                    constraint /= DisplayManager.HEIGHT;

                switch (sideConstraint.getSide()) {
                    case BOTTOM:
                        if (this.toHandle instanceof Gui)
                            y = -1 + constraint * 2 + height;
                        else
                            y = relativeTo.getY() - relativeTo.getHeight() + height +
                                    constraint * relativeTo.getHeight() * 2;
                        break;
                    case TOP:
                        if (this.toHandle instanceof Gui)
                            y = 1 - constraint * 2 - height;
                        else
                            y = relativeTo.getY() + relativeTo.getHeight() - height -
                                    constraint * relativeTo.getHeight() * 2;
                        break;
                    default:
                        throw new IllegalGuiConstraintException("Wrong side constraint for coordinate");
                }

                break;
            case PIXEL:
                if (constraint < -DisplayManager.HEIGHT || constraint > DisplayManager.HEIGHT)
                    throw new IllegalGuiConstraintException(
                            "Component y = " + constraint + " coordinate doesn't belong in window");

                y = constraint / DisplayManager.HEIGHT;
                break;
            case CENTER:
                if (relativeTo == null)
                    relativeTo = this.parent;

                y = relativeTo.getY();
                break;
            case STICKY:
                if (relativeTo == null)
                    relativeTo = this.parent;

                StickyConstraint stickyConstraint = (StickyConstraint) yConstraint;
                if (stickyConstraint.getDistanceType() == Constraints.PIXEL)
                    constraint /= DisplayManager.HEIGHT;

                switch (stickyConstraint.getSide()) {
                    case TOP:
                        y = relativeTo.getY() + relativeTo.getHeight() + height +
                                constraint * this.parent.getHeight() * 2;
                        break;
                    case BOTTOM:
                        y = relativeTo.getY() - relativeTo.getHeight() - height -
                                constraint * this.parent.getHeight() * 2;
                        break;
                    default:
                        y = relativeTo.getY(); // error?
                        break;
                }
                break;
            default:
                throw new IllegalGuiConstraintException("This constraint cannot be handled");
        }

        return y;
    }

    public float handleXConstraint(GuiConstraints xConstraint) {
        float x = 0;
        if (xConstraint == null)
            return x;

        float width = this.toHandle.getWidth();

        Constraints distanceType;
        float constraint = xConstraint.constraint();
        GuiInterface relativeTo = xConstraint.getRelativeTo();
        switch (xConstraint.getConstraint()) {
            case RELATIVE:
                if (relativeTo != null) { // Relatif à un autre élément
                    if (this.toHandle instanceof Gui) {
                        if (constraint == 0)
                            x = relativeTo.getX();
                        else
                            x = relativeTo.getX() - width + (1f / 2f + width) * constraint * 2;
                    } else
                        x = relativeTo.getX() + (relativeTo.getWidth() - width) * constraint;
                } else {
                    // Cadre la position dans le parent avec 0 > constraint < 1 qui définit la position du composant dans le parent
                    x = this.parent.getX() + this.parent.getWidth() - width +
                            (width - this.parent.getWidth()) * (2 - constraint * 2);
                }
                break;
            case SIDE:
                if (relativeTo == null)
                    relativeTo = this.parent;

                SideConstraint sideConstraint = (SideConstraint) xConstraint;
                distanceType = sideConstraint.getDistanceType();
                if (distanceType == Constraints.PIXEL)
                    constraint /= DisplayManager.WIDTH;

                switch (sideConstraint.getSide()) {
                    case LEFT:
                        if (this.toHandle instanceof Gui)
                            x = -1 + constraint * 2 + width;
                        else
                            x = relativeTo.getX() - relativeTo.getWidth() + width +
                                    constraint * relativeTo.getWidth() * 2;
                        break;
                    case RIGHT:
                        if (this.toHandle instanceof Gui)
                            x = 1 - constraint * 2 - width;
                        else
                            x = relativeTo.getX() + relativeTo.getWidth() - width -
                                    constraint * relativeTo.getWidth() * 2;
                        break;
                    default:
                        throw new IllegalGuiConstraintException("Wrong side constraint for coordinate");
                }
                break;
            case PIXEL:
                if (constraint < -DisplayManager.WIDTH || constraint > DisplayManager.WIDTH)
                    throw new IllegalGuiConstraintException("Component x coordinate doesn't belong in window");

                x = constraint / DisplayManager.WIDTH;
                break;
            case CENTER:
                if (relativeTo == null)
                    relativeTo = this.parent;

                x = relativeTo.getX();
                break;
            case STICKY:
                if (relativeTo == null)
                    relativeTo = this.parent;

                StickyConstraint stickyConstraint = (StickyConstraint) xConstraint;
                distanceType = stickyConstraint.getDistanceType();
                if (distanceType == Constraints.PIXEL)
                    constraint /= DisplayManager.WIDTH;
                switch (stickyConstraint.getSide()) {
                    case LEFT:
                        x = relativeTo.getX() - relativeTo.getWidth() - width -
                                constraint * this.parent.getWidth() * 2;
                        break;
                    case RIGHT:
                        x = relativeTo.getX() + relativeTo.getWidth() + width +
                                constraint * this.parent.getWidth() * 2;
                        break;
                    default:
                        x = relativeTo.getX(); // error?
                        break;
                }
                break;
            default:
                throw new IllegalGuiConstraintException("This constraint cannot be handled");
        }

        return x;
    }
}
