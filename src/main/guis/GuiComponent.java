package main.guis;

import main.guis.constraints.GuiConstraints;
import main.guis.constraints.GuiConstraintsManager;
import main.guis.constraints.RelativeConstraint;
import main.guis.constraints.SideConstraint;
import main.renderEngine.DisplayManager;
import main.util.vector.Vector2f;

public abstract class GuiComponent {

    private float x, y, width, height;

    private Gui parent;

    private GuiTexture texture;

    private GuiConstraintsManager constraintsManager;

    public GuiComponent(Gui parent, String texture) {
        this.parent = parent;
        this.width = parent.getWidth();
        this.height = parent.getHeight();
        this.x = parent.getX();
        this.y = parent.getY();

        this.texture = new GuiTexture(texture, new Vector2f(x, y), new Vector2f(width,
                height));
    }

    public void setConstraints(GuiConstraintsManager constraints) {
        final GuiConstraints xConstraint = constraints.getxConstraint();
        final GuiConstraints yConstraint = constraints.getyConstraint();
        final GuiConstraints widthConstraint = constraints.getWidthConstraint();
        final GuiConstraints heightConstraint = constraints.getHeightConstraint();


        float relativeToWidth = parent.getWidth();
        float relativeToHeight = parent.getHeight();
        float relativeToX = parent.getX();
        float relativeToY = parent.getY();

//        System.out.println(constraints.getOrder());
        for (String s : constraints.getOrder()) {
            float width = 1.1f;
            float height = 1.1f;
            switch (s) {
                case "W":
                    if (widthConstraint != null) {
                        float constraint = widthConstraint.constraint();
                        switch (widthConstraint.getConstraint()) {
                            case RELATIVE:
                                GuiComponent relativeTo = ((RelativeConstraint) widthConstraint).getRelativeTo();
                                if (relativeTo != null)
                                    relativeToWidth = relativeTo.getWidth();

                                width = constraint * relativeToWidth;
                                break;
                            case ASPECT:
                                width = constraint * DisplayManager.HEIGHT / DisplayManager.WIDTH * this.height;
                                break;
                        }
                    }

                    if (width <= relativeToWidth)
                        this.width = width;

                    break;
                case "H":
                    if (heightConstraint != null) {
                        float constraint = heightConstraint.constraint();
                        switch (heightConstraint.getConstraint()) {
                            case RELATIVE:
                                GuiComponent relativeTo = ((RelativeConstraint) heightConstraint).getRelativeTo();
                                if (relativeTo != null)
                                    relativeToHeight = relativeTo.getHeight();

                                height = constraint * relativeToHeight;
                                break;
                            case ASPECT:
                                height = constraint * DisplayManager.WIDTH / DisplayManager.HEIGHT * this.width;
                                break;
                        }
                    }
                    if (height <= relativeToHeight)
                        this.height = height;

                    break;
                case "X":
                    if (xConstraint != null) {
                        float constraint = xConstraint.constraint();
                        switch (xConstraint.getConstraint()) {
                            case RELATIVE:
                                GuiComponent relativeTo = ((RelativeConstraint) xConstraint).getRelativeTo();
                                if (relativeTo != null) {
                                    relativeToX = relativeTo.getX();
                                    relativeToWidth = relativeTo.getWidth();
                                }

                                this.x = relativeToX + relativeToWidth - this.width +
                                        (this.width - relativeToWidth) * (2 - constraint * 2);
                                break;
                            case SIDE:
                                switch (((SideConstraint) xConstraint).getSide()) {
                                    case LEFT:
                                        this.x = relativeToX - relativeToWidth + this.width +
                                                constraint * relativeToWidth;
                                        break;
                                    case RIGHT:
                                        this.x = relativeToX + relativeToWidth - this.width -
                                                constraint * relativeToWidth;
                                        break;
                                    default: //TODO: Raise exception
                                        this.x = constraint;
                                        break;
                                }
                                break;
                            default:
                                this.x = constraint;
                                break;
                        }
                    }
                    System.out.println("X: " + x);
                    if ((x - this.width) < (relativeToX - relativeToWidth) ||
                            (x + this.width) > (relativeToX + relativeToWidth))
                        this.x = relativeToX;
                    break;
                case "Y":
                    if (yConstraint != null) {
                        float constraint = yConstraint.constraint();
                        switch (yConstraint.getConstraint()) {
                            case RELATIVE:
                                GuiComponent relativeTo = ((RelativeConstraint) yConstraint).getRelativeTo();
                                if (relativeTo != null) {
                                    relativeToY = relativeTo.getY();
                                    relativeToHeight = relativeTo.getHeight();
                                }
                                System.out.println(
                                        "Relative: " + relativeToY + ", " + relativeToHeight + ", constraint: " +
                                                constraint);

                                // Cadre la position dans le parent avec 0 > constraint < 1 qui définit la position du composant dans le parent
//                                this.y = relativeToY - relativeToHeight + this.height +
//                                        (relativeToHeight - this.height) * (2 - constraint * 2);
                                this.y = relativeToY - parent.getHeight() + this.height +
                                        (parent.getHeight() - this.height) * (2 - constraint * 2);
                                System.out.println("Y: " + y);
                                break;
                            case SIDE:
                                switch (((SideConstraint) yConstraint).getSide()) {
                                    case BOTTOM:
                                        this.y = relativeToY - relativeToHeight + this.height +
                                                constraint * relativeToHeight;
                                        break;
                                    case TOP:
                                        this.y = relativeToY + relativeToHeight - this.height -
                                                constraint * relativeToHeight;
                                        break;
                                    default: //TODO: Raise exception
                                        this.y = constraint;
                                        break;
                                }
                                System.out.println("Relative: " + relativeToY + ", " + relativeToHeight);
                                System.out.println("Y: " + y);
                                break;
                            default:
                                this.y = constraint;
                                break;
                        }

//                        if ((y - this.height) < (parent.getY() - parent.getHeight()) ||
//                                ((y + this.height) > parent.getY() + parent.getHeight()))
//                            this.y = relativeToY;

                        System.out.println(y);
                    }
                    break;
                default:
                    System.err.println("Erreur GuiComponents.class");
                    break;
            }
        }

        this.constraintsManager = constraints;

        updateTexturePosition();

    }

    private void updateTexturePosition() {
        this.texture.getScale().x = this.width;
        this.texture.getScale().y = this.height;
        this.texture.getPosition().x = this.x;
        this.texture.getPosition().y = this.y;
    }

    public abstract void onClick();

    public abstract void onHover();

    public abstract void onScroll();

    public abstract void onType();

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public GuiTexture getTexture() {
        return this.texture;
    }

    public Gui getParent() {
        return this.parent;
    }

    public GuiConstraintsManager getConstraintsManager() {
        return this.constraintsManager;
    }

    @Override
    public String toString() {
        return "GuiComponent{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
