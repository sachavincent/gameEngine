package guis;

import static util.math.Maths.isPointIn2DBounds;

import guis.constraints.GuiConstraints;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RelativeConstraint;
import guis.constraints.SideConstraint;
import guis.exceptions.IllegalGuiConstraintException;
import guis.presets.GuiBackground;
import guis.presets.GuiPreset;
import guis.presets.buttons.GuiAbstractButton;
import inputs.MouseUtils;
import inputs.callbacks.EnterCallback;
import inputs.callbacks.HoverCallback;
import inputs.callbacks.LeaveCallback;
import inputs.callbacks.PressCallback;
import inputs.callbacks.ReleaseCallback;
import inputs.callbacks.ScrollCallback;
import java.util.Objects;
import renderEngine.DisplayManager;
import util.math.Vector2f;

public abstract class GuiComponent<E> implements GuiInterface {

    private float x, y;

    private float width, height;

    private float startX, startY;
    private float finalX, finalY;

    private float finalWidth, finalHeight;

    private GuiInterface parent;

    private GuiTexture<?> texture;

    private ReleaseCallback onReleaseCallback;
    private EnterCallback   onEnterCallback;
    private LeaveCallback   onLeaveCallback;
    private HoverCallback   onHoverCallback;
    private ScrollCallback  onScrollCallback;
    private PressCallback   onPressCallback;

    private boolean displayed;

    private boolean               clicked;
    private GuiConstraintsManager constraints;

    private float cornerRadius = Gui.CORNER_RADIUS;

    public GuiComponent(GuiInterface parent) {
        this.parent = parent;
        this.width = parent.getWidth();
        this.finalWidth = parent.getWidth();
        this.height = parent.getHeight();
        this.finalHeight = parent.getHeight();
        this.x = parent.getX();
        this.y = parent.getY();

        this.displayed = true;

        if (parent instanceof Gui)
            ((Gui) parent).addComponent(this);
    }

    public GuiComponent(GuiInterface parent, GuiBackground<?> texture) {
        this(parent);

        this.texture = new GuiTexture<E>(texture, new Vector2f(x, y), new Vector2f(width, height));
    }

    public float getCornerRadius() {
        return this.cornerRadius;
    }

    public void setCornerRadius(float cornerRadius) {
        this.cornerRadius = cornerRadius;
    }

    /**
     * Parent's constraints have changed, children constraints change too
     */
    public void updateConstraints() {
        if (this.constraints != null)
            setConstraints(this.constraints);
    }

    public void setConstraints(GuiConstraintsManager constraints) {
        final GuiConstraints xConstraint = constraints.getxConstraint();
        final GuiConstraints yConstraint = constraints.getyConstraint();
        final GuiConstraints widthConstraint = constraints.getWidthConstraint();
        final GuiConstraints heightConstraint = constraints.getHeightConstraint();

        for (char s : constraints.getOrder()) {
            switch (s) {
                case 'W':
                    handleWidthConstraint(widthConstraint);
                    break;
                case 'H':
                    handleHeightConstraint(heightConstraint);
                    break;
                case 'X':
                    handleXConstraint(xConstraint);
                    break;
                case 'Y':
                    handleYConstraint(yConstraint);
                    break;
            }
        }

        this.constraints = constraints;

        updateTexturePosition();
    }

    private void handleYConstraint(GuiConstraints yConstraint) {
        if (yConstraint == null)
            return;

        float constraint = yConstraint.constraint();
        switch (yConstraint.getConstraint()) {
            case RELATIVE:
                GuiInterface relativeTo = ((RelativeConstraint) yConstraint).getRelativeTo();
                if (relativeTo != null)  // Relatif à un autre élément
                    this.y = relativeTo.getY() + (relativeTo.getHeight() - this.height) * constraint;
                else {
                    // Cadre la position dans le parent avec 0 > constraint < 1 qui définit la position du composant dans le parent : fonctionne.
                    this.y = (parent.getY() + parent.getHeight() - this.height) -
                            (parent.getHeight() - this.height) * 2 * constraint;
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
                    default:
                        throw new IllegalGuiConstraintException("Wrong side constraint for coordinate");
                }

                break;
            case PIXEL:
                if (constraint < 0 || constraint > DisplayManager.HEIGHT)
                    throw new IllegalGuiConstraintException(
                            "Component y coordinate doesn't belong in window");

                this.y = constraint / DisplayManager.HEIGHT;

                break;
            case CENTER:
                this.y = parent.getY();
                break;
            default:
                throw new IllegalGuiConstraintException("This constraint cannot be handled");
        }
    }

    private void handleXConstraint(GuiConstraints xConstraint) {
        if (xConstraint == null)
            return;

        float constraint = xConstraint.constraint();
        switch (xConstraint.getConstraint()) {
            case RELATIVE:
                GuiInterface relativeTo = ((RelativeConstraint) xConstraint).getRelativeTo();
                if (relativeTo != null)  // Relatif à un autre élément
                    this.x = relativeTo.getX() + (relativeTo.getWidth() - this.width) * constraint;
                else {
                    // Cadre la position dans le parent avec 0 > constraint < 1 qui définit la position du composant dans le parent
                    this.x = parent.getX() + parent.getWidth() - this.width +
                            (this.width - parent.getWidth()) * (2 - constraint * 2);
                }
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
                    default:
                        throw new IllegalGuiConstraintException("Wrong side constraint for coordinate");
                }
                break;
            case PIXEL:
                if (constraint < 0 || constraint > DisplayManager.WIDTH)
                    throw new IllegalGuiConstraintException(
                            "Component x coordinate doesn't belong in window");

                this.x = constraint / DisplayManager.WIDTH;
                break;
            case CENTER:
                this.x = parent.getX();
                break;
            default:
                throw new IllegalGuiConstraintException("This constraint cannot be handled");
        }
    }

    private void handleHeightConstraint(GuiConstraints heightConstraint) {
        if (heightConstraint == null)
            return;

        float constraint = heightConstraint.constraint();
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

        if (height > parent.getHeight() || this.height < 0)
            throw new IllegalGuiConstraintException("Height of component exceeded height of parent");

        this.finalHeight = height;
    }

    private void handleWidthConstraint(GuiConstraints widthConstraint) {
        if (widthConstraint == null)
            return;

        float constraint = widthConstraint.constraint();
        switch (widthConstraint.getConstraint()) {
            case RELATIVE:
                this.width = constraint * parent.getWidth();
                break;
            case ASPECT:
                this.width = constraint * DisplayManager.HEIGHT / DisplayManager.WIDTH * this.height;
                break;
            case PIXEL:
                if (constraint < 0 || constraint > DisplayManager.WIDTH) // Nb of pixels > width
                    throw new IllegalGuiConstraintException("Width of component exceeded width of window");

                this.width = constraint / DisplayManager.WIDTH;

                break;
            default:
                throw new IllegalGuiConstraintException("This constraint cannot be handled");
        }

        if (this.width > parent.getWidth() || this.width < 0)
            throw new IllegalGuiConstraintException("Width of component exceeded width of parent");

        this.finalWidth = width;
    }


    public void updateTexturePosition() {
        if (this.texture == null)
            return;

        this.texture.getScale().x = this.width;
        this.texture.getScale().y = this.height;
        this.texture.getPosition().x = this.x;
        this.texture.getPosition().y = this.y;
    }

    public void onRelease() {
        if (onReleaseCallback == null)
            return;

        clicked = false;

        onReleaseCallback.onRelease();
    }

    /**
     * Click + release within component
     */
    public void onPress() {
        if (onPressCallback == null) {
            System.out.println("null");
            return;
        }

        clicked = false;

        onPressCallback.onPress();
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

    public void setOnPress(PressCallback onPressCallback) {
        this.onPressCallback = onPressCallback;
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

    public void setTexture(GuiTexture<?> texture) {
        this.texture = texture;
    }

    @Override
    public float getX() {
        return this.x;
    }

    @Override
    public float getY() {
        return this.y;
    }

    @Override
    public float getWidth() {
        return this.width;
    }

    @Override
    public float getHeight() {
        return this.height;
    }

    @Override
    public void setX(float x) {
//        if (!isPosInBounds(new Vector2f(x, y), parent.getX(), parent.getY(), parent.getWidth(), parent.getHeight()) &&
//                getParentGui(this).areTransitionsOfComponentDone(this))
//            throw new IllegalArgumentException("New coordinates don't belong in parent");

        this.x = x;

        updateTexturePosition();
    }

    @Override
    public void setY(float y) {
        if (!getParentGui(this).areTransitionsOfComponentDone(this))
            return;

        if (!isPointIn2DBounds(new Vector2f(x, y), parent.getX(), parent.getY(), parent.getWidth(),
                parent.getHeight()) &&
                getParentGui(this).areTransitionsOfComponentDone(this))
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

    @Override
    public GuiTexture<?> getTexture() {
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

    public static Gui getParentGui(GuiComponent<?> guiComponent) {
        if (guiComponent.getParent() instanceof Gui)
            return (Gui) guiComponent.getParent();

        return getParentGui((GuiComponent<?>) guiComponent.getParent());
    }

    public GuiInterface getParent() {
        return this.parent;
    }

    @Override
    public void setDisplayed(boolean displayed) {
        this.displayed = displayed;

        if (this instanceof GuiAbstractButton) {
            GuiAbstractButton guiAbstractButton = (GuiAbstractButton) this;
            Gui tooltipGui = guiAbstractButton.getTooltipGui();
            if (tooltipGui != null) {
                if (!displayed)
                    Gui.hideGui(tooltipGui);
                else {
                    if (MouseUtils.isCursorInGuiComponent(this) && !tooltipGui.isDisplayed()) // Appears on mouse cursor
                        Gui.showGui(tooltipGui);
                }
            }
        }
    }

    @Override
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
                (texture == null ? "" : (", alpha=" + texture.getAlpha())) +
                '}';
    }

    @Override
    public float getStartX() {
        return 0;
    }

    @Override
    public float getStartY() {
        return 0;
    }

    @Override
    public float getFinalWidth() {
        return 0;
    }

    @Override
    public float getFinalHeight() {
        return 0;
    }

    @Override
    public float getFinalX() {
        return this.finalX;
    }

    @Override
    public float getFinalY() {
        return this.finalY;
    }

    @Override
    public void setStartX(float startX) {
        if ((startX < -1 || startX > 1) && getParentGui(this).areTransitionsOfComponentDone(this))
            throw new IllegalArgumentException("New coordinates don't belong in window");

        this.startX = startX;
    }

    @Override
    public void setStartY(float startY) {
        if ((startY < -1 || startY > 1) && getParentGui(this).areTransitionsOfComponentDone(this))
            throw new IllegalArgumentException("New coordinates don't belong in window");

        this.startY = startY;
    }

    @Override
    public void setFinalX(float finalX) {
        if ((finalX < -1 || finalX > 1) && getParentGui(this).areTransitionsOfComponentDone(this))
            throw new IllegalArgumentException("New coordinates don't belong in window");

        this.finalX = finalX;
    }

    @Override
    public void setFinalY(float finalY) {
        if ((finalY < -1 || finalY > 1) && getParentGui(this).areTransitionsOfComponentDone(this))
            throw new IllegalArgumentException("New coordinates don't belong in window");

        this.finalY = finalY;
    }

    @Override
    public void setFinalWidth(float finalWidth) {
        if ((finalWidth < 0 || finalWidth > 2) && getParentGui(this).areTransitionsOfComponentDone(this))
            throw new IllegalArgumentException("New width don't fit in window");

        this.finalWidth = finalWidth;
    }

    @Override
    public void setFinalHeight(float finalHeight) {
        if ((finalHeight < 0 || finalHeight > 2) && getParentGui(this).areTransitionsOfComponentDone(this))
            throw new IllegalArgumentException("New height don't fit in window");

        this.finalHeight = finalHeight;
    }

    @Override
    public void setAlpha(float alpha) {
        if (this.texture == null)
            return;

        this.texture.setAlpha(alpha);

        if (this instanceof GuiPreset) {
            ((GuiPreset) this).getBasics()
                    .stream()
                    .filter(Objects::nonNull)
                    .filter(guiBasics -> guiBasics.getTexture().getFinalAlpha() == 1)
                    // Ignore components which aren't going to be fully opaque
                    .forEach(guiBasics -> {
                        if (guiBasics.getTexture() != null)
                            guiBasics.getTexture().setAlpha(alpha);
                    });
        } else if (texture.getFinalAlpha() == 1)
            texture.setAlpha(alpha);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        GuiComponent<?> that = (GuiComponent<?>) o;
        return Float.compare(that.x, x) == 0 &&
                Float.compare(that.y, y) == 0 &&
                Float.compare(that.width, width) == 0 &&
                Float.compare(that.height, height) == 0 &&
                Float.compare(that.startX, startX) == 0 &&
                Float.compare(that.startY, startY) == 0 &&
                Float.compare(that.finalX, finalX) == 0 &&
                Float.compare(that.finalY, finalY) == 0 &&
                Float.compare(that.finalWidth, finalWidth) == 0 &&
                Float.compare(that.finalHeight, finalHeight) == 0;
    }
}
