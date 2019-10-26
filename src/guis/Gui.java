package guis;

import guis.constraints.GuiConstraints;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.GuiConstraintsManager.Constraints;
import guis.constraints.SideConstraint;
import guis.presets.GuiPreset;
import guis.transitions.Transition;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
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

        this.components = new ArrayList<>();
        this.transitions = new LinkedList<>();
    }


    public Gui(Color color) {
        this.background = new GuiTexture(color, new Vector2f(x, y), new Vector2f(width,
                height));

        this.components = new ArrayList<>();
        this.transitions = new LinkedList<>();
    }

    public Gui(int r, int g, int b) {
        this(new Color(r, g, b));
    }

    public Gui(float r, float g, float b) {
        this(new Color(r, g, b));
    }

    public void setTransitions(Transition... transitions) {
        this.transitions = new LinkedList<>(Arrays.asList(transitions));
    }

    public void setConstraints(GuiConstraintsManager constraints) {
        final GuiConstraints xConstraint = constraints.getxConstraint();
        final GuiConstraints yConstraint = constraints.getyConstraint();
        final GuiConstraints widthConstraint = constraints.getWidthConstraint();
        final GuiConstraints heightConstraint = constraints.getHeightConstraint();

        constraints.getOrder().forEach(s -> {
            float width = 1.1f;
            float height = 1.1f;
            switch (s) {
                case "W":
                    if (widthConstraint != null) {
                        switch (widthConstraint.getConstraint()) {
                            case RELATIVE:
                                width = widthConstraint.constraint();
                                break;
                            case ASPECT:
                                width = widthConstraint.constraint() * DisplayManager.HEIGHT / DisplayManager.WIDTH *
                                        this.height;
                                break;
                        }
                    }

                    if (width <= 2)
                        this.width = width;

//                    System.out.println("W: " + width);
                    break;
                case "H":
                    if (heightConstraint != null) {
                        switch (heightConstraint.getConstraint()) {
                            case RELATIVE:
                                height = heightConstraint.constraint();
                                break;
                            case ASPECT:
//TODO
                                break;
                        }
                    }
                    if (height <= 2)
                        this.height = height;

//                    System.out.println("H: " + height);
                    break;
                case "X":
                    if (xConstraint != null) {
                        float constraint = xConstraint.constraint();
                        if (xConstraint.getConstraint() == Constraints.RELATIVE) {
                            this.x = 2 - this.width + (this.width - 2) * (2 - constraint * 2);
                        } else if (xConstraint.getConstraint() == Constraints.SIDE) {
                            switch (((SideConstraint) xConstraint).getSide()) {
                                case LEFT:
                                    this.x = -1 + constraint * 2 + this.width;
                                    break;
                                case RIGHT:
                                    this.x = 1 - constraint * 2 - this.width;
                                    break;
                                default: //TODO: Raise exception
                                    this.x = constraint;
                                    break;
                            }
                        } else
                            this.x = constraint;
                    }
                    if (x < -1 || x > 1)
                        this.x = 0;

//                    System.out.println("X: " + x);
                    break;
                case "Y":
                    if (yConstraint != null) {
                        float constraint = yConstraint.constraint();
                        if (yConstraint.getConstraint() == Constraints.RELATIVE) {
                            // Cadre la position dans le parent avec 0 > constraint < 1 qui définit la position du composant dans le parent
                            this.y = -DisplayManager.HEIGHT + this.height +
                                    (DisplayManager.HEIGHT - this.height) * (2 - constraint * 2);
                        } else if (yConstraint.getConstraint() == Constraints.SIDE) {
                            switch (((SideConstraint) yConstraint).getSide()) {
                                case TOP:
                                    this.y = constraint * 2 - heightConstraint.constraint();
                                    break;
                                case BOTTOM:
                                    this.y = constraint * 2 + heightConstraint.constraint();
                                    break;
                                default: //TODO: Raise exception
                                    this.y = constraint;
                                    break;
                            }
                        } else
                            this.y = constraint;
                    }

                    if (y < -1 || y > 1)
                        this.y = 0;
//                    System.out.println("Y: " + y);
                    break;
                default:
                    System.err.println("Erreur Gui.class");
                    break;
            }
        });

        updateTexturePosition();
    }

    public void updateTexturePosition() {
        this.background.getScale().x = this.width;
        this.background.getScale().y = this.height;
        this.background.getPosition().x = this.x;
        this.background.getPosition().y = this.y;
    }

    public GuiTexture getBackground() {
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

    @Override
    public String toString() {
        return "Gui{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}';
    }

    public boolean isFocused() {
        return this.focused;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public boolean isDisplayed() {
        return this.displayed;
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

    public void addComponent(GuiComponent guiComponent) {
        if (guiComponent != null)
            this.components.add(guiComponent);
    }

    public void addComponent(GuiPreset guiPreset) {
        guiPreset.getBasics().forEach(this::addComponent);
    }

    void animate() {
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
        this.background.setAlpha(alpha);

        this.components.forEach(guiComponent -> guiComponent.getTexture().setAlpha(alpha));
    }

    public float getAlphaOfGui() {
        return this.background.getAlpha();
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

    public float getStartX() {
        return this.startX;
    }

    public void setStartX(float startX) {
        this.startX = startX;
    }

    public float getStartY() {
        return this.startY;
    }

    public void setStartY(float startY) {
        this.startY = startY;
    }

    public float getFinalX() {
        return this.finalX;
    }

    public void setFinalX(float finalX) {
        this.finalX = finalX;
    }

    public float getFinalY() {
        return this.finalY;
    }

    public void setFinalY(float finalY) {
        this.finalY = finalY;
    }

    public float getFinalWidth() {
        return this.finalWidth;
    }

    public void setFinalWidth(float finalWidth) {
        this.finalWidth = finalWidth;
    }

    public float getFinalHeight() {
        return this.finalHeight;
    }

    public void setFinalHeight(float finalHeight) {
        this.finalHeight = finalHeight;
    }

}
