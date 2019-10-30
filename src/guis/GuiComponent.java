package guis;

import static util.Maths.isPosInBounds;

import guis.constraints.GuiConstraints;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RelativeConstraint;
import guis.constraints.SideConstraint;
import guis.exceptions.IllegalGuiConstraintException;
import inputs.callbacks.ClickCallback;
import inputs.callbacks.EnterCallback;
import inputs.callbacks.HoverCallback;
import inputs.callbacks.LeaveCallback;
import inputs.callbacks.ReleaseCallback;
import inputs.callbacks.ScrollCallback;
import java.awt.Color;
import java.util.Objects;
import renderEngine.DisplayManager;
import util.MouseUtils;
import util.vector.Vector2f;

public abstract class GuiComponent implements GuiInterface {

    private float x, y, finalWidth, width, finalHeight, height;

    private GuiInterface parent;

    private GuiTexture texture;

    private ClickCallback   onClickCallback;
    private ReleaseCallback onReleaseCallback;
    private EnterCallback   onEnterCallback;
    private LeaveCallback   onLeaveCallback;
    private HoverCallback   onHoverCallback;
    private ScrollCallback  onScrollCallback;

    private boolean displayed;

    private boolean clicked;

    public GuiComponent(GuiInterface parent) {
        this.parent = parent;
        this.width = parent.getWidth();
        this.finalWidth = parent.getWidth();
        this.height = parent.getHeight();
        this.finalHeight = parent.getHeight();
        this.x = parent.getX();
        this.y = parent.getY();

        this.displayed = false;

        if (parent instanceof Gui)
            ((Gui) parent).addComponent(this);
    }

    public GuiComponent(GuiInterface parent, String texture) {
        this(parent);

        this.texture = new GuiTexture(texture, new Vector2f(x, y), new Vector2f(width, height));
    }

    public GuiComponent(GuiInterface parent, Color color) {
        this(parent);

        this.texture = new GuiTexture(color, new Vector2f(x, y), new Vector2f(width, height));
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
                    if (widthConstraint == null)
                        break;

                    float constraint = widthConstraint.constraint();
                    switch (widthConstraint.getConstraint()) {
                        case RELATIVE:
                            width = constraint * parent.getWidth();
                            break;
                        case ASPECT:
                            width = constraint * DisplayManager.HEIGHT / DisplayManager.WIDTH * this.height;
                            break;
                        case PIXEL:
                            if (constraint > DisplayManager.WIDTH) // Nb of pixels > width
                                throw new IllegalGuiConstraintException("Width of component exceeded width of window");

                            width = constraint / DisplayManager.WIDTH;
                            break;
                        default:
                            throw new IllegalGuiConstraintException("This constraint cannot be handled");
                    }

                    if (width <= parent.getWidth())
                        this.width = width;
                    else
                        throw new IllegalGuiConstraintException("Width of component exceeded width of parent");

                    break;
                case "H":
                    if (heightConstraint == null)
                        break;

                    constraint = heightConstraint.constraint();
                    switch (heightConstraint.getConstraint()) {
                        case RELATIVE:
                            height = constraint * parent.getHeight();
                            break;
                        case ASPECT:
                            height = constraint * DisplayManager.WIDTH / DisplayManager.HEIGHT * this.width;
                            break;
                        case PIXEL:
                            if (constraint > DisplayManager.HEIGHT) // Nb of pixels > height
                                throw new IllegalGuiConstraintException(
                                        "Height of component exceeded height of window");

                            height = constraint / DisplayManager.HEIGHT;
                            break;
                        default:
                            throw new IllegalGuiConstraintException("This constraint cannot be handled");
                    }

                    if (height <= parent.getHeight())
                        this.height = height;
                    else
                        throw new IllegalGuiConstraintException("Height of component exceeded height of parent");

                    break;
                case "X":
                    if (xConstraint == null)
                        break;

                    constraint = xConstraint.constraint();
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
                            }
                            break;
                        case PIXEL:
                            if (constraint < 0 || constraint > DisplayManager.WIDTH)
                                throw new IllegalGuiConstraintException(
                                        "Component x coordinate doesn't belong in window");

                            this.x = constraint / DisplayManager.WIDTH;
                            break;
                        default:
                            throw new IllegalGuiConstraintException("This constraint cannot be handled");
                    }
//                    System.out.println("X: " + x);
                    if ((x - this.width) < (parent.getX() - parent.getWidth()) ||
                            (x + this.width) > (parent.getX() + parent.getWidth()))
                        throw new IllegalGuiConstraintException("Component x coordinate doesn't belong in parent");
                    break;
                case "Y":
                    if (yConstraint == null)
                        break;

                    constraint = yConstraint.constraint();
                    switch (yConstraint.getConstraint()) {
                        case RELATIVE:
                            GuiComponent relativeTo = ((RelativeConstraint) yConstraint).getRelativeTo();
                            if (relativeTo != null) { // Relatif à un autre élément
                                this.y = relativeTo.getY() - this.height -
                                        (parent.getHeight() / 2 + this.height) * constraint * 2;
                            } else {
                                // Cadre la position dans le parent avec 0 > constraint < 1 qui définit la position du composant dans le parent
                                this.y = parent.getY() + parent.getHeight() - this.height -
                                        (parent.getHeight() - this.height) * constraint * 2;
                            }
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
                            }
                            System.out.println("Relative: " + parent.getY() + ", " + parent.getHeight());
                            System.out.println("Y: " + y);
                            break;
                        case PIXEL:
                            if (constraint < 0 || constraint > DisplayManager.HEIGHT)
                                throw new IllegalGuiConstraintException(
                                        "Component y coordinate doesn't belong in window");

                            this.y = constraint / DisplayManager.HEIGHT;
                            break;
                        default:
                            throw new IllegalGuiConstraintException("This constraint cannot be handled");
                    }

                    if ((y - this.height) < (parent.getY() - parent.getHeight()) ||
                            ((y + this.height) > parent.getY() + parent.getHeight()))
                        throw new IllegalGuiConstraintException("Component y coordinate doesn't belong in parent");

//                        System.out.println("Y=>" + y);
                    break;
                default:
                    System.err.println("Erreur GuiComponents.class");
                    break;
            }
        }

        this.finalHeight = height;
        this.finalWidth = width;

        updateTexturePosition();
    }

    public void updateTexturePosition() {
        if (this.texture == null)
            return;

        this.texture.getScale().x = this.width;
        this.texture.getScale().y = this.height;
        this.texture.getPosition().x = this.x;
        this.texture.getPosition().y = this.y;
    }

    public void onClick() {
        if (onClickCallback == null)
            return;

        clicked = true;

        onClickCallback.onClick();
    }

    public void onRelease() {
        if (onReleaseCallback == null)
            return;

        clicked = false;

        onReleaseCallback.onRelease();
    }

    private boolean cursorInComponent;

    public void onEnter() {
        if (onEnterCallback == null || cursorInComponent)
            return;

        if (MouseUtils.isCursorInGuiComponent(this)) {
            cursorInComponent = true;

            onEnterCallback.onEnter();
        }
    }


    public void onLeave() {
        if (onLeaveCallback == null || !cursorInComponent)
            return;

        if (!MouseUtils.isCursorInGuiComponent(this)) {
            cursorInComponent = false;

            onLeaveCallback.onLeave();
        }
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

    public void setOnRelease(ReleaseCallback onReleaseCallback) {
        this.onReleaseCallback = onReleaseCallback;
    }

    public void setOnHover(HoverCallback onHoverCallback) {
        this.onHoverCallback = onHoverCallback;
    }

    public void setOnLeave(LeaveCallback onLeaveCallback) {
        this.onLeaveCallback = onLeaveCallback;
    }

    public void setOnEnter(EnterCallback onEnterCallback) {
        this.onEnterCallback = onEnterCallback;
    }

    public void setOnScroll(ScrollCallback onScrollCallback) {
        this.onScrollCallback = onScrollCallback;
    }

    public void setTexture(GuiTexture texture) {
        this.texture = texture;
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
        if (!isPosInBounds(new Vector2f(x, y), parent.getX(), parent.getY(), parent.getWidth(), parent.getHeight()))
            throw new IllegalArgumentException("New coordinates don't belong in parent");

        this.x = x;
        updateTexturePosition();
    }

    public void setY(float y) {
        if (!isPosInBounds(new Vector2f(x, y), parent.getX(), parent.getY(), parent.getWidth(), parent.getHeight()))
            throw new IllegalArgumentException("New coordinates don't belong in parent");

        this.y = y;

        updateTexturePosition();
    }

    public void setWidth(float width) {
        this.width = width;

        updateTexturePosition();
    }

    public void setHeight(float height) {
        this.height = height;

        updateTexturePosition();
    }

    public boolean isClicked() {
        return this.clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public GuiTexture getTexture() {
        return this.texture;
    }

    public void scale(float scale) {
        this.width = width * scale;
        this.height = height * scale;
    }

    public void resetScale() {
        this.width = finalWidth;
        this.height = finalHeight;
    }

    public GuiInterface getParent() {
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
                ", alpha=" + texture.getAlpha() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        GuiComponent that = (GuiComponent) o;
        return Float.compare(that.x, x) == 0 &&
                Float.compare(that.y, y) == 0 &&
                Float.compare(that.width, width) == 0 &&
                Float.compare(that.height, height) == 0 &&
                texture.equals(that.texture);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, width, height, texture);
    }
}
