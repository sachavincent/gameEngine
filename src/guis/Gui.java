package guis;

import guis.constraints.GuiConstraints;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.GuiConstraintsManager.Constraints;
import guis.constraints.SideConstraint;
import guis.presets.GuiPreset;
import java.util.ArrayList;
import java.util.List;
import renderEngine.DisplayManager;
import util.vector.Vector2f;

public class Gui {

    public final static float CORNER_RADIUS = 9f;

    private GuiTexture         background;
    private List<GuiComponent> components;

    private Animation        animation;
    private AnimationDetails animationDetails;

    private float x, y;
    private float width, height;

    public Gui(String texture) {
        this.background = new GuiTexture(texture, new Vector2f(x, y), new Vector2f(width,
                height));

        this.components = new ArrayList<>();
    }

    public void setAnimation(Animation animation, AnimationDetails animationDetails) {
        this.animation = animation;
        this.animationDetails = animationDetails;
    }

    public void setConstraints(GuiConstraintsManager constraints) {
        final GuiConstraints xConstraint = constraints.getxConstraint();
        final GuiConstraints yConstraint = constraints.getyConstraint();
        final GuiConstraints widthConstraint = constraints.getWidthConstraint();
        final GuiConstraints heightConstraint = constraints.getHeightConstraint();

//        System.out.println(constraints.getOrder());
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

    private void updateTexturePosition() {
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

    public Animation getAnimation() {
        return this.animation;
    }

    public AnimationDetails getAnimationDetails() {
        return this.animationDetails;
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

    public void addComponent(GuiComponent guiComponent) {
        this.components.add(guiComponent);
    }

    public void addComponent(GuiPreset guiPreset) {
        this.components.add(guiPreset);
    }
}
