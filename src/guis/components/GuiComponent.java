package guis.components;

import guis.Gui;
import guis.GuiTexture;
import guis.constraints.GuiConstraints;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RelativeConstraint;
import guis.constraints.SideConstraint;
import inputs.callbacks.ClickCallback;
import inputs.callbacks.HoverCallback;
import inputs.callbacks.ScrollCallback;
import renderEngine.DisplayManager;
import util.vector.Vector2f;

public abstract class GuiComponent {

    private float x, y, width, height;

    private Gui parent;

    private GuiTexture texture;

    private ClickCallback  onClickCallback;
    private HoverCallback  onHoverCallback;
    private ScrollCallback onScrollCallback;

    private boolean displayed;

    public GuiComponent(Gui parent, String texture) {
        this.parent = parent;
        this.width = parent.getWidth();
        this.height = parent.getHeight();
        this.x = parent.getX();
        this.y = parent.getY();

        this.texture = new GuiTexture(texture, new Vector2f(x, y), new Vector2f(width,
                height));

        this.displayed = false;
    }

    public void setConstraints(GuiConstraintsManager constraints) {
        final GuiConstraints xConstraint = constraints.getxConstraint();
        final GuiConstraints yConstraint = constraints.getyConstraint();
        final GuiConstraints widthConstraint = constraints.getWidthConstraint();
        final GuiConstraints heightConstraint = constraints.getHeightConstraint();


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
                                width = constraint * parent.getWidth();
                                break;
                            case ASPECT:
                                width = constraint * DisplayManager.HEIGHT / DisplayManager.WIDTH * this.height;
                                break;
                        }
                    }

                    if (width <= parent.getWidth())
                        this.width = width;

                    break;
                case "H":
                    if (heightConstraint != null) {
                        float constraint = heightConstraint.constraint();
                        switch (heightConstraint.getConstraint()) {
                            case RELATIVE:
                                height = constraint * parent.getHeight();
                                break;
                            case ASPECT:
                                height = constraint * DisplayManager.WIDTH / DisplayManager.HEIGHT * this.width;
                                break;
                        }
                    }
                    if (height <= parent.getHeight())
                        this.height = height;

//                    System.out.println("H: " + this.height);

                    break;
                case "X":
                    if (xConstraint != null) {
                        float constraint = xConstraint.constraint();
                        switch (xConstraint.getConstraint()) {
                            case RELATIVE:
                                this.x = parent.getX() + parent.getWidth() - this.width +
                                        (this.width - parent.getWidth()) * (2 - constraint * 2);
                                break;
                            case SIDE:
                                switch (((SideConstraint) xConstraint).getSide()) {
                                    case LEFT:
                                        this.x = parent.getX() - parent.getWidth() + this.width +
                                                constraint * parent.getWidth();
                                        break;
                                    case RIGHT:
                                        this.x = parent.getX() + parent.getWidth() - this.width -
                                                constraint * parent.getWidth();
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
//                    System.out.println("X: " + x);
                    if ((x - this.width) < (parent.getX() - parent.getWidth()) ||
                            (x + this.width) > (parent.getX() + parent.getWidth()))
                        this.x = parent.getX();
                    break;
                case "Y":
                    if (yConstraint != null) {
                        float constraint = yConstraint.constraint();
                        switch (yConstraint.getConstraint()) {
                            case RELATIVE:
                                GuiComponent relativeTo = ((RelativeConstraint) yConstraint).getRelativeTo();
                                if (relativeTo != null) { // Relatif à un autre élément
//                                    System.out.println("RelativeTo: " + relativeTo.getY());

                                    this.y = relativeTo.getY() - this.height -
                                            (parent.getHeight() / 2 + this.height) * constraint * 2;
                                } else {
                                    // Cadre la position dans le parent avec 0 > constraint < 1 qui définit la position du composant dans le parent
                                    this.y = parent.getY() + parent.getHeight() - this.height -
                                            (parent.getHeight() - this.height) * constraint * 2;
                                }
//                                System.out.println("YY: " + y);
                                break;
                            case SIDE:
                                switch (((SideConstraint) yConstraint).getSide()) {
                                    case BOTTOM:
                                        this.y = parent.getY() - parent.getHeight() + this.height +
                                                constraint * parent.getHeight();
                                        break;
                                    case TOP:
                                        this.y = parent.getY() + parent.getHeight() - this.height -
                                                constraint * parent.getHeight();
                                        break;
                                    default: //TODO: Raise exception
                                        this.y = constraint;
                                        break;
                                }
                                System.out.println("Relative: " + parent.getY() + ", " + parent.getHeight());
                                System.out.println("Y: " + y);
                                break;
                            default:
                                this.y = constraint;
                                break;
                        }

                        if ((y - this.height) < (parent.getY() - parent.getHeight()) ||
                                ((y + this.height) > parent.getY() + parent.getHeight()))
                            this.y = parent.getY();

//                        System.out.println("Y=>" + y);
                    }
                    break;
                default:
                    System.err.println("Erreur GuiComponents.class");
                    break;
            }
        }

        updateTexturePosition();

    }

    private void updateTexturePosition() {
        this.texture.getScale().x = this.width;
        this.texture.getScale().y = this.height;
        this.texture.getPosition().x = this.x;
        this.texture.getPosition().y = this.y;
    }

    public void onClick() {
        if (onClickCallback == null)
            return;

        onClickCallback.onClick();
    }

    public void onHover() {
        if (onHoverCallback == null)
            return;

        onHoverCallback.onHover();
    }

    public void onScroll() {
        if (onScrollCallback == null)
            return;

        onScrollCallback.onScroll();
    }

    public void onType() {

    }

    public void setOnClick(ClickCallback onClickCallback) {
        this.onClickCallback = onClickCallback;
    }

    public void setOnHover(HoverCallback onHoverCallback) {
        this.onHoverCallback = onHoverCallback;
    }

    public void setOnScroll(ScrollCallback onScrollCallback) {
        this.onScrollCallback = onScrollCallback;
    }

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

    public void setDisplayed(boolean displayed) {
        this.displayed = displayed;
    }

    public boolean isDisplayed() {
        return this.displayed;
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
