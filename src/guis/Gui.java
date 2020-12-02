package guis;

import fontMeshCreator.FontType;
import fontMeshCreator.Text;
import guis.basics.GuiBasics;
import guis.constraints.GuiConstraints;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RelativeConstraint;
import guis.constraints.SideConstraint;
import guis.exceptions.IllegalGuiConstraintException;
import guis.presets.Background;
import guis.presets.GuiPreset;
import guis.presets.buttons.GuiAbstractButton;
import guis.presets.buttons.GuiAbstractButton.ButtonType;
import guis.presets.buttons.GuiCircularButton;
import guis.presets.buttons.GuiRectangleButton;
import guis.transitions.Transition;
import guis.transitions.Transition.Trigger;
import java.awt.Color;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import renderEngine.DisplayManager;
import textures.FontTexture;
import util.Timer;
import util.math.Vector2f;

public class Gui implements GuiInterface {

    public final static float CORNER_RADIUS = 8f;

    protected final static FontType DEFAULT_FONT = new FontType(
            new FontTexture("roboto.png").getTextureID(), new File("res/roboto.fnt")); //TODO System-wide font

    private final Map<GuiComponent, Set<Transition>> components;

    private GuiTexture background;

    private Set<Transition> transitions;

    private float x, y;
    private float width, height;

    private float startX, startY;
    private float finalX, finalY;

    private float finalWidth, finalHeight;

    private float cornerRadius = Gui.CORNER_RADIUS;

    private boolean focused, displayed;

    public Gui(Background<?> background) {
        setBackground(background);

        this.components = new LinkedHashMap<>();
        this.transitions = new HashSet<>();
    }

    public void setBackground(Background<?> background) {
        this.background = new GuiTexture(background, new Vector2f(x, y), new Vector2f(width,
                height));
    }

    public Gui(int r, int g, int b) {
        this(new Background<>(new Color(r, g, b)));
    }

    public Gui(float r, float g, float b) {
        this(new Background<>(new Color(r, g, b)));
    }

    public void setConstraints(GuiConstraintsManager constraints) {
        final GuiConstraints xConstraint = constraints.getxConstraint();
        final GuiConstraints yConstraint = constraints.getyConstraint();
        final GuiConstraints widthConstraint = constraints.getWidthConstraint();
        final GuiConstraints heightConstraint = constraints.getHeightConstraint();

        handleWidthConstraint(widthConstraint);
        handleHeightConstraint(heightConstraint);
        handleXConstraint(xConstraint);
        handleYConstraint(yConstraint);
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

        updateTexturePosition();
    }

    protected void handleYConstraint(GuiConstraints yConstraint) {
        float constraint;
        if (yConstraint == null)
            return;

        constraint = yConstraint.constraint();

        switch (yConstraint.getConstraint()) {
            case RELATIVE:
                GuiInterface relativeTo = ((RelativeConstraint) yConstraint).getRelativeTo();
                if (relativeTo != null) { // Relatif à un autre élément
                    if (constraint == 0)
                        this.y = relativeTo.getY();
                    else
                        this.y = relativeTo.getY() - this.height + (1f / 2f + this.height) * constraint * 2;
                } else {
                    // Cadre la position dans le parent avec 0 > constraint < 1 qui définit la position du composant dans le parent : fonctionne.
                    this.y = (1 - this.height) - (1 - this.height) * 2 * constraint;
                }
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

//                    if (y < -1 || y > 1)
//                        throw new IllegalGuiConstraintException("Component y coordinate doesn't belong in parent");
//TODO En commentaire parce qu'une partie du gui peut être hors écran (ex: GuiSelectedItem)
//        if (y < -1 || y > 1)
//            System.err.println("Warning: Component y coordinate doesn't belong in parent");

        setFinalY(this.y);
    }

    protected void handleXConstraint(GuiConstraints xConstraint) {
        float constraint;
        if (xConstraint == null)
            return;

        constraint = xConstraint.constraint();

        switch (xConstraint.getConstraint()) {
            case RELATIVE:
                GuiInterface relativeTo = ((RelativeConstraint) xConstraint).getRelativeTo();
                if (relativeTo != null) { // Relatif à un autre élément
                    if (constraint == 0)
                        this.x = relativeTo.getX();
                    else
                        this.x = relativeTo.getX() - this.width + (1f / 2f + this.width) * constraint * 2;
                } else {
                    // Cadre la position dans le parent avec 0 > constraint < 1 qui définit la position du composant dans le parent
                    this.x = 1 - this.width + (this.width - 1) * (2 - constraint * 2);
                }
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

//                    if (x < -1 || x > 1)
//                        throw new IllegalGuiConstraintException("Component x coordinate doesn't belong in parent");
//        if (x < -1 || x > 1)
//            System.err.println("Warning: Component x coordinate doesn't belong in parent");

        setFinalX(this.x);
    }

    void handleHeightConstraint(GuiConstraints heightConstraint) {
        float constraint;
        if (heightConstraint == null)
            return;

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

        setFinalHeight(this.height);
    }

    void handleWidthConstraint(GuiConstraints widthConstraint) {
        if (widthConstraint == null)
            return;
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

        setFinalWidth(this.width);
    }

    public void updateChildrenConstraints() {
        getAllComponents().forEach(GuiComponent::updateConstraints);
    }

    public void updateTexturePosition() {
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


    protected void show() {
        if (isDisplayed())
            return;

        if (getShowTransitions().isEmpty())
            setDisplayed(true);
        else
            getShowTransitions().forEach(transition -> {
                transition.setStarted(false);
                Timer.scheduleTransition(transition, this);
            });

        getComponentsShowTransitions().
                forEach((component, lTransitions) -> {
                    if (lTransitions.isEmpty())
                        component.setDisplayed(true);
                    else
                        lTransitions.forEach(transition -> {
                            transition.setStarted(false);
                            // Started from previous iteration, set to false before it begins

                            Timer.scheduleTransition(transition, component);
                        });
                });

        getHideTransitions().forEach(transition -> transition.setDone(false));

        getComponentsHideTransitions()
                .forEach((guiComponent, lTransitions) -> lTransitions
                        .forEach(transition -> transition.setDone(false)));
    }


    protected void hide() {
        if (!isDisplayed())
            return;

//        System.out.println("hiding");

        if (getHideTransitions().isEmpty())
            setDisplayed(false);
        else
            getHideTransitions().forEach(transition -> {
                transition.setStarted(false);

                Timer.scheduleTransition(transition, this);
            });


        getComponentsHideTransitions().forEach(
                (component, lTransitions) -> {
                    if (lTransitions.isEmpty())
                        component.setDisplayed(false);
                    else
                        lTransitions.forEach(transition -> Timer.scheduleTransition(transition, component));
                });

        getShowTransitions().forEach(transition -> transition.setDone(false));

        getComponentsShowTransitions()
                .forEach((guiComponent, lTransitions) -> lTransitions
                        .forEach(transition -> transition.setDone(false)));
    }

    public boolean areTransitionsDone() {
        if (isDisplayed())
            return this.transitions.stream().filter(transition -> transition.getTrigger() == Trigger.SHOW)
                    .allMatch(Transition::isDone);
        else
            return this.transitions.stream().filter(transition -> transition.getTrigger() == Trigger.HIDE)
                    .allMatch(Transition::isDone);
    }

    public boolean areTransitionsOfComponentDone(GuiComponent guiComponent) {
        if (!this.components.containsKey(guiComponent) && guiComponent instanceof GuiBasics)
            guiComponent = (GuiComponent) guiComponent.getParent();

        if (!this.components.containsKey(guiComponent))
            return false;

        if (this.components.get(guiComponent).isEmpty())
            return true;

        return this.components.get(guiComponent).stream().allMatch(Transition::isDone);
    }

    public void addComponent(GuiComponent guiComponent, Transition... transitions) {
        if (guiComponent == null)
            return;

        this.components.remove(guiComponent);

        if (transitions.length == 0)
            this.components.put(guiComponent, new HashSet<>());
        else
            this.components.put(guiComponent, new HashSet<>(Arrays.asList(transitions)));
    }

    public void removeComponent(GuiComponent guiComponent) {
        if (guiComponent == null)
            return;

        this.components.remove(guiComponent);
    }
//
//    @Deprecated
//    public void addComponent(GuiPreset guiPreset) {
//        if (guiPreset == null || guiPreset.getBasics() == null)
//            return;
//
//        guiPreset.getBasics().forEach(this::addComponent);
//    }

    public void animate() {
        Set<Transition> transitions;
        if (isDisplayed())
            transitions = getShowTransitions().stream()
                    .filter(transition -> !transition.isDone() && transition.isStarted())
                    .collect(Collectors.toSet());
        else
            transitions = getHideTransitions().stream()
                    .filter(transition -> !transition.isDone() && transition.isStarted())
                    .collect(Collectors.toSet());


        transitions.forEach(transition -> {
            boolean done = transition.animate(this);
            if (done) {
                transition.setDone(true);
            }
        });

        Map<GuiComponent, Set<Transition>> map =
                isDisplayed() ? getComponentsShowTransitions() : getComponentsHideTransitions();

        map.forEach((guiComponent, lTransitions) -> lTransitions.stream().filter(transition -> !transition.isDone())
                .forEach(transition -> {
                            boolean done = transition.animate(guiComponent);
                            if (done)
                                transition.setDone(true);

                            guiComponent.updateTexturePosition();
                        }
                ));

        updateTexturePosition();
    }

    public void setAlpha(float alpha) {
        if (this.background == null || this.components == null)
            return;

        this.background.setAlpha(alpha);

//        this.components.keySet().forEach(guiComponent -> {
//            if (guiComponent instanceof GuiPreset) {
//                ((GuiPreset) guiComponent).getBasics()
//                        .stream()
//                        .filter(Objects::nonNull)
//                        .filter(guiBasics -> guiBasics.getTexture().getFinalAlpha() == 1)
//                        // Ignore components which aren't going to be fully opaque
//                        .forEach(guiBasics -> {
//                            if (guiBasics.getTexture() != null)
//                                guiBasics.getTexture().setAlpha(alpha);
//                        });
//            } else if (guiComponent.getTexture().getFinalAlpha() == 1)
//                guiComponent.getTexture().setAlpha(alpha);
//        });
    }

    public void setTransitions(Transition... transitions) {
        this.transitions = new HashSet<>(Arrays.asList(transitions));
    }

    public void setComponentTransitions(GuiComponent guiComponent, Transition... transitions) {
        if (!components.containsKey(guiComponent))
            throw new IllegalArgumentException("Component does not belong to parent gui");

        components.get(guiComponent).addAll(Arrays.asList(transitions));
    }

    public void setTransitionsToAllComponents(Transition... transitions) {
        components.keySet().forEach(guiComponent -> setComponentTransitions(guiComponent, transitions));
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public Set<Transition> getShowTransitions() {
        return transitions.stream().filter(transition -> transition.getTrigger() == Trigger.SHOW)
                .collect(Collectors.toSet());
    }

    public Set<Transition> getHideTransitions() {
        return transitions.stream().filter(transition -> transition.getTrigger() == Trigger.HIDE)
                .collect(Collectors.toSet());
    }

    public Map<GuiComponent, Set<Transition>> getComponentsHideTransitions() {
        Map<GuiComponent, Set<Transition>> transitions = new HashMap<>();

        components.forEach((guiComponent, transitionSet) ->
                transitions.put(guiComponent,
                        transitionSet.stream().filter(transition -> transition.getTrigger() == Trigger.HIDE)
                                .collect(Collectors.toSet())));

        return transitions;
    }

    public Map<GuiComponent, Set<Transition>> getComponentsShowTransitions() {
        Map<GuiComponent, Set<Transition>> transitions = new HashMap<>();

        components.forEach((guiComponent, transitionSet) ->
                transitions.put(guiComponent,
                        transitionSet.stream().filter(transition -> transition.getTrigger() == Trigger.SHOW)
                                .collect(Collectors.toSet())));

        return transitions;
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

    public List<GuiComponent> getAllComponents() {
        final List<GuiComponent> guiComponents = new ArrayList<>();
        getComponents().keySet().forEach(guiComponent -> {
            if (guiComponent instanceof GuiPreset) {
                guiComponents.addAll(((GuiPreset) guiComponent).getBasics());
            }

            guiComponents.add(guiComponent);
        });

        return guiComponents;
    }

    @Override
    public GuiTexture getTexture() {
        return this.background;
    }

    @Override
    public void setDisplayed(boolean displayed) {
        this.displayed = displayed;
    }

    public Map<GuiComponent, Set<Transition>> getComponents() {
        return this.components;
    }

    public Set<Transition> getTransitions() {
        return this.transitions;
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

    public boolean isFocused() {
        return this.focused;
    }

    @Override
    public boolean isDisplayed() {
        return this.displayed;
    }

    public float getAlphaOfGui() {
        return this.background.getAlpha();
    }

    @Override
    public float getStartX() {
        return this.startX;
    }

    @Override
    public float getStartY() {
        return this.startY;
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
    public float getFinalWidth() {
        return this.finalWidth;
    }

    @Override
    public float getFinalHeight() {
        return this.finalHeight;
    }

    @Override
    public void setStartX(float startX) {
//        if ((startX < -1 || startX > 1) && areTransitionsDone())
//            throw new IllegalArgumentException("New coordinates don't belong in window");

        this.startX = startX;
    }


    @Override
    public void setStartY(float startY) {
//        if ((startY < -1 || startY > 1) && areTransitionsDone())
//            throw new IllegalArgumentException("New coordinates don't belong in window");

        this.startY = startY;
    }


    @Override
    public void setFinalX(float finalX) {
//        if ((finalX < -1 || finalX > 1) && areTransitionsDone())
//            throw new IllegalArgumentException("New coordinates don't belong in window");

        this.finalX = finalX;
    }


    @Override
    public void setFinalY(float finalY) {
//        if ((finalY < -1 || finalY > 1) && areTransitionsDone())
//            throw new IllegalArgumentException("New coordinates don't belong in window");

        this.finalY = finalY;
    }

    @Override
    public void setFinalWidth(float finalWidth) {
//        if ((finalWidth < 0 || finalWidth > 2) && areTransitionsDone())
//            throw new IllegalArgumentException("New width don't fit in window");

        this.finalWidth = finalWidth;
    }

    @Override
    public void setFinalHeight(float finalHeight) {
//        if ((finalHeight < 0 || finalHeight > 2) && areTransitionsDone())
//            throw new IllegalArgumentException("New height don't fit in window");

        this.finalHeight = finalHeight;
    }

    public boolean hasTransitions() {
        return !this.transitions.isEmpty();
    }


    public static void toggleGui(Gui gui) {
        if (gui == null)
            return;

        if (gui.isDisplayed())
            gui.hide();
        else//TODO: boolean attribute transitioning to make sure
            gui.show();
    }

    public static void showGui(Gui gui) {
        if (gui == null)
            return;

//        System.out.println("Showing gui");
//        final List<Transition> lTransitions = gui.getAllTransitions();
//        final Set<Transition> hideTransitions = gui.getHideTransitions();
//        final Set<Transition> showTransitions = gui.getShowTransitions();

        gui.show();
    }

    public static void hideGui(Gui gui) {
        if (gui == null)
            return;

//        System.out.println("Hiding gui");
//        final List<Transition> lTranshiitions = gui.getAllTransitions();
//        final Set<Transition> hideTransitions = gui.getHideTransitions();
//        final Set<Transition> showTransitions = gui.getShowTransitions();

        gui.hide();
    }

    protected List<Transition> getAllTransitions() {
        final List<Transition> lTransitions = new ArrayList<>();
        this.components.values().forEach(lTransitions::addAll);
        lTransitions.addAll(getTransitions());

        return lTransitions;
    }

    public List<Gui> getTooltipGuis() {
        return getAllComponents().stream().filter(GuiAbstractButton.class::isInstance)
                .map(GuiAbstractButton.class::cast).map(GuiAbstractButton::getTooltipGui).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public float getCornerRadius() {
        return this.cornerRadius;
    }

    public void setCornerRadius(float cornerRadius) {
        this.cornerRadius = cornerRadius;
    }

    protected GuiAbstractButton createButton(ButtonType buttonType, Background<?> background, Text text,
            Text tooltipText, GuiConstraintsManager constraints) {
        switch (buttonType) {
            case CIRCULAR:
                return new GuiCircularButton(this, background, text, tooltipText,
                        constraints); //TODO: Handle fontSize automatically (text length with button width)
            // TODO: Add color parameter
            case RECTANGLE:
                return new GuiRectangleButton(this, background, text, tooltipText, constraints);
        }
        return null;
    }
}