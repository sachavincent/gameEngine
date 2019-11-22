package guis;

import guis.constraints.GuiConstraints;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.SideConstraint;
import guis.exceptions.IllegalGuiConstraintException;
import guis.presets.GuiPreset;
import guis.transitions.Transition;
import java.awt.Color;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import renderEngine.DisplayManager;
import util.vector.Vector2f;

public class Gui implements GuiInterface {

    public final static float CORNER_RADIUS = 8f;

    private GuiTexture         background;
    private List<GuiComponent> components;

    private List<Transition> transitions;

    private float x, y;
    private float width, height;

    private float startX, startY;
    private float finalX, finalY;

    private float finalWidth, finalHeight;

    private boolean focused, displayed;

    public Gui(String texture) {
        this.background = new GuiTexture(texture, new Vector2f(x, y), new Vector2f(width,
                height));

        this.components = new LinkedList<>();
        this.transitions = new LinkedList<>();
    }


    public Gui(Color color) {
        this.background = new GuiTexture(color, new Vector2f(x, y), new Vector2f(width,
                height));

        this.components = new LinkedList<>();
        this.transitions = new LinkedList<>();
    }

    public Gui(int r, int g, int b) {
        this(new Color(r, g, b));
    }

    public Gui(float r, float g, float b) {
        this(new Color(r, g, b));
    }

    public void setConstraints(GuiConstraintsManager constraints) {
        final GuiConstraints xConstraint = constraints.getxConstraint();
        final GuiConstraints yConstraint = constraints.getyConstraint();
        final GuiConstraints widthConstraint = constraints.getWidthConstraint();
        final GuiConstraints heightConstraint = constraints.getHeightConstraint();

        for (char s : constraints.getOrder()) {
            switch (s) {
                case 'W':
                    if (widthConstraint == null)
                        break;
                    float constraint = widthConstraint.constraint();

                    switch (widthConstraint.getConstraint()) {
                        case RELATIVE:
                            this.width = constraint;
                            break;
                        case ASPECT:
                            this.width = constraint * DisplayManager.HEIGHT / DisplayManager.WIDTH * this.height;
                            break;
                        case PIXEL:
                            this.width = constraint / DisplayManager.WIDTH;

                            break;
                        default:
                            throw new IllegalGuiConstraintException("This constraint cannot be handled");

                    }

                    if (this.width > 2 || this.width < 0)
                        throw new IllegalGuiConstraintException("Width of component exceeded width of window");

                    break;
                case 'H':
                    if (heightConstraint == null)
                        break;

                    constraint = heightConstraint.constraint();

                    switch (heightConstraint.getConstraint()) {
                        case RELATIVE:
                            this.height = constraint;
                            break;
                        case ASPECT:
                            this.height = constraint * DisplayManager.WIDTH / DisplayManager.HEIGHT * this.width;
                            break;
                        case PIXEL:
                            this.height = constraint / DisplayManager.HEIGHT;

                            break;
                        default:
                            throw new IllegalGuiConstraintException("This constraint cannot be handled");
                    }

                    if (this.height > 2 || this.height < 0)
                        throw new IllegalGuiConstraintException("Height of component exceeded height of parent");

                    break;
                case 'X':
                    if (xConstraint == null)
                        break;

                    constraint = xConstraint.constraint();

                    switch (xConstraint.getConstraint()) {
                        case RELATIVE:
                            this.x = 2 - this.width + (this.width - 2) * (2 - constraint * 2);
                            break;
                        case SIDE:
                            switch (((SideConstraint) xConstraint).getSide()) {
                                case LEFT:
                                    this.x = -1 + constraint * 2 + this.width;
                                    break;
                                case RIGHT:
                                    this.x = 1 - constraint * 2 - this.width;
                                    break;
                                default:
                                    throw new IllegalGuiConstraintException("Wrong side constraint for coordinate");
                            }
                            break;
                        case PIXEL:
                            this.x = constraint / DisplayManager.WIDTH;
                            break;
                        case CENTER:
                            this.x = constraint;
                            break;
                        default:
                            throw new IllegalGuiConstraintException("This constraint cannot be handled");
                    }

                    if (x < -1 || x > 1)
                        throw new IllegalGuiConstraintException("Component x coordinate doesn't belong in parent");

                    break;
                case 'Y':
                    if (yConstraint == null)
                        break;

                    constraint = yConstraint.constraint();

                    switch (yConstraint.getConstraint()) {
                        case RELATIVE:
                            this.y = 2 - this.height + (this.height - 2) * (2 - constraint * 2);
                            break;
                        case SIDE:
                            switch (((SideConstraint) yConstraint).getSide()) {
                                case BOTTOM:
                                    this.y = -1 + constraint * 2 + this.height;
                                    break;
                                case TOP:
                                    this.y = 1 - constraint * 2 - this.height;
                                    break;
                                default:
                                    throw new IllegalGuiConstraintException("Wrong side constraint for coordinate");
                            }

                            break;
                        case PIXEL:
                            this.y = constraint / DisplayManager.HEIGHT;
                            break;
                        case CENTER:
                            this.y = constraint;
                            break;
                        default:
                            throw new IllegalGuiConstraintException("This constraint cannot be handled");
                    }

                    if (y < -1 || y > 1)
                        throw new IllegalGuiConstraintException("Component y coordinate doesn't belong in parent");

                    break;
            }

        }

        updateTexturePosition();
    }

    private void updateTexturePosition() {
        this.background.getScale().x = this.width;
        this.background.getScale().y = this.height;
        this.background.getPosition().x = this.x;
        this.background.getPosition().y = this.y;
    }

    @Override
    public String toString() {
        return "Gui{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}';
    }


    public void show() {
        if (isDisplayed())
            return;

        this.transitions.forEach(transition -> transition.showTransition(this));
        updateTexturePosition();

        this.displayed = true;
    }

    public void hide() {
        if (!isDisplayed())
            return;

        this.transitions.forEach(transition -> transition.setDone(false));

        this.displayed = false;
    }

    private boolean areTransitionsDone() {
        return this.transitions.stream().allMatch(Transition::isDone);
    }

    public void addComponent(GuiComponent guiComponent) {
        if (guiComponent == null)
            return;

        this.components.remove(guiComponent);

        this.components.add(guiComponent);
    }

    public void removeComponent(GuiComponent guiComponent) {
        if (guiComponent == null)
            return;

        this.components.remove(guiComponent);
    }

    @Deprecated
    public void addComponent(GuiPreset guiPreset) {
        if (guiPreset == null || guiPreset.getBasics() == null)
            return;

        guiPreset.getBasics().forEach(this::addComponent);
    }

    public void animate() {
        if (!isDisplayed())
            return;

        this.transitions.stream().filter(transition -> !transition.isDone()).forEach(transition -> {
            boolean done = transition.animate(this);
            if (done)
                transition.setDone(true);
        });

        updateTexturePosition();
    }

    public void setAlphaToGui(float alpha) {
        if (this.background == null || this.components == null)
            return;

        this.background.setAlpha(alpha);

        this.components.forEach(guiComponent -> {
            if (guiComponent instanceof GuiPreset) {
                ((GuiPreset) guiComponent).getBasics().
                        stream()
                        .filter(Objects::nonNull)
                        .filter(guiBasics -> guiBasics.getTexture().getFinalAlpha() == 1)
                        // Ignore components which aren't going to be fully opaque
                        .forEach(guiBasics -> {
                            if (guiBasics.getTexture() != null)
                                guiBasics.getTexture().setAlpha(alpha);
                        });
            } else if (guiComponent.getTexture().getFinalAlpha() == 1)
                guiComponent.getTexture().setAlpha(alpha);
        });
    }

    public void setTransitions(Transition... transitions) {
        this.transitions = new LinkedList<>(Arrays.asList(transitions));
    }


    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public void setX(float x) {
        if ((x < -1 || x > 1) && areTransitionsDone())
            throw new IllegalArgumentException("New coordinates don't belong in window");

        this.x = x;
    }

    public void setY(float y) {
        if ((y < -1 || y > 1) && areTransitionsDone())
            throw new IllegalArgumentException("New coordinates don't belong in window");

        this.y = y;
    }

    public void setWidth(float width) {
        if ((width < 0 || width > 2) && areTransitionsDone())
            throw new IllegalArgumentException("New width don't fit in window");

        this.width = width;
    }

    public void setHeight(float height) {
        if ((height < 0 || height > 2) && areTransitionsDone())
            throw new IllegalArgumentException("New height don't fit in window");

        this.height = height;
    }

    @Override
    public GuiTexture getTexture() {
        return this.background;
    }

    public List<GuiComponent> getComponents() {
        return this.components;
    }

    public List<Transition> getTransitions() {
        return this.transitions;
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

    public boolean isFocused() {
        return this.focused;
    }

    public boolean isDisplayed() {
        return this.displayed;
    }

    public float getAlphaOfGui() {
        return this.background.getAlpha();
    }

    public float getStartX() {
        return this.startX;
    }

    public float getStartY() {
        return this.startY;
    }

    public float getFinalX() {
        return this.finalX;
    }

    public float getFinalY() {
        return this.finalY;
    }

    public float getFinalWidth() {
        return this.finalWidth;
    }

    public float getFinalHeight() {
        return this.finalHeight;
    }

    public void setStartX(float startX) {
        if ((startX < -1 || startX > 1) && areTransitionsDone())
            throw new IllegalArgumentException("New coordinates don't belong in window");

        this.startX = startX;
    }


    public void setStartY(float startY) {
        if ((startY < -1 || startY > 1) && areTransitionsDone())
            throw new IllegalArgumentException("New coordinates don't belong in window");

        this.startY = startY;
    }


    public void setFinalX(float finalX) {
        if ((finalX < -1 || finalX > 1) && areTransitionsDone())
            throw new IllegalArgumentException("New coordinates don't belong in window");

        this.finalX = finalX;
    }


    public void setFinalY(float finalY) {
        if ((finalY < -1 || finalY > 1) && areTransitionsDone())
            throw new IllegalArgumentException("New coordinates don't belong in window");

        this.finalY = finalY;
    }

    public void setFinalWidth(float finalWidth) {
        if ((finalWidth < 0 || finalWidth > 2) && areTransitionsDone())
            throw new IllegalArgumentException("New height don't fit in window");

        this.finalWidth = finalWidth;
    }

    public void setFinalHeight(float finalHeight) {
        if ((finalHeight < 0 || finalHeight > 2) && areTransitionsDone())
            throw new IllegalArgumentException("New height don't fit in window");

        this.finalHeight = finalHeight;
    }

    public boolean hasTransitions() {
        return !this.transitions.isEmpty();
    }
}