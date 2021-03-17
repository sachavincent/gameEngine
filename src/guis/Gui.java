package guis;

import static renderEngine.GuiRenderer.filledQuad;

import fontMeshCreator.FontType;
import fontMeshCreator.Line;
import fontMeshCreator.Text;
import guis.basics.GuiText;
import guis.constraints.GuiConstraintHandler;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.GuiGlobalConstraints;
import guis.presets.Background;
import guis.presets.buttons.GuiAbstractButton;
import guis.presets.buttons.GuiAbstractButton.ButtonType;
import guis.presets.buttons.GuiCircularButton;
import guis.presets.buttons.GuiRectangleButton;
import guis.transitions.Transition;
import guis.transitions.Transition.Trigger;
import inputs.callbacks.UpdateCallback;
import java.awt.Color;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import renderEngine.GuiRenderer;
import textures.FontTexture;
import util.Timer;
import util.math.Vector2f;

public class Gui implements GuiInterface {

    public final static float CORNER_RADIUS = 8f;

    public final static FontType DEFAULT_FONT = new FontType(
            new FontTexture("roboto.png").getTextureID(), new File("res/roboto.fnt")); //TODO System-wide font

    private final Map<GuiComponent, Set<Transition>> components;

    private       GuiTexture background;
    private final GuiTexture debugOutline;

    private Set<Transition> transitions;

    private float x, y;
    private float width, height;

    private float cornerRadius = Gui.CORNER_RADIUS;

    protected boolean focused, displayed;

    private UpdateCallback updateCallback;

    private GuiGlobalConstraints childrenConstraints;

    public Gui(Background<?> background) {
        setBackground(background);

        this.debugOutline = new GuiTexture(new Background<>(new Color((int) (Math.random() * 0x1000000))), this);

        this.components = new LinkedHashMap<>();
        this.transitions = new HashSet<>();

        GuiRenderer.addGui(this);
    }

    public void setBackground(Background<?> background) {
        this.background = new GuiTexture(background, new Vector2f(x, y), new Vector2f(width, height));
    }

    public Gui(int r, int g, int b) {
        this(new Background<>(new Color(r, g, b)));
    }

    public Gui(float r, float g, float b) {
        this(new Background<>(new Color(r, g, b)));
    }

    public void setConstraints(GuiConstraintsManager constraints) {
        width = 1;
        height = 1;
        x = 0;
        y = 0;

        GuiConstraintHandler guiConstraintHandler = new GuiConstraintHandler(this);
        guiConstraintHandler.setConstraints(constraints);

        updateTexturePosition();
    }

    public void updateTexturePosition() {
        this.background.getScale().x = this.width;
        this.background.getScale().y = this.height;
        this.background.getPosition().x = this.x;
        this.background.getPosition().y = this.y;

        this.debugOutline.getScale().x = this.width;
        this.debugOutline.getScale().y = this.height;
        this.debugOutline.getPosition().x = this.x;
        this.debugOutline.getPosition().y = this.y;
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
        if (getShowTransitions().isEmpty())
            setDisplayed(true);
        else
            getShowTransitions().forEach(transition -> {
                transition.setStarted(false);
                Timer.scheduleTransition(transition, this);
            });
//
//        getComponentsShowTransitions().
//                forEach((component, lTransitions) -> {
//                    if (lTransitions.isEmpty())
//                        component.setDisplayed(true);
//                    else
//                        lTransitions.forEach(transition -> {
//                            transition.setStarted(false);
//                            // Started from previous iteration, set to false before it begins
//
//                            Timer.scheduleTransition(transition, component);
//                        });
//                });
//
//        getHideTransitions().forEach(transition -> transition.setDone(false));
//
//        getComponentsHideTransitions()
//                .forEach((guiComponent, lTransitions) -> lTransitions
//                        .forEach(transition -> transition.setDone(false)));
    }


    protected void hide() {
//        System.out.println("hiding");

        if (getHideTransitions().isEmpty())
            setDisplayed(false);
        else
            getHideTransitions().forEach(transition -> {
                transition.setStarted(false);

                Timer.scheduleTransition(transition, this);
            });
//
//
//        getComponentsHideTransitions().forEach(
//                (component, lTransitions) -> {
//                    if (lTransitions.isEmpty())
//                        component.setDisplayed(false);
//                    else
//                        lTransitions.forEach(transition -> Timer.scheduleTransition(transition, component));
//                });
//
//        getShowTransitions().forEach(transition -> transition.setDone(false));
//
//        getComponentsShowTransitions()
//                .forEach((guiComponent, lTransitions) -> lTransitions
//                        .forEach(transition -> transition.setDone(false)));
    }

    public boolean areTransitionsDone() {
        if (isDisplayed())
            return this.transitions.stream().filter(transition -> transition.getTrigger() == Trigger.SHOW)
                    .allMatch(Transition::isDone);
        else
            return this.transitions.stream().filter(transition -> transition.getTrigger() == Trigger.HIDE)
                    .allMatch(Transition::isDone);
    }

    public static GuiText setupText(GuiInterface guiInterface, Text text) {
        if (text == null)
            return null;

        List<Line> lines = text.getFont().getLoader().getLines(text);

        Line line = lines.get(0);

        if (line == null) {
            try {
                throw new IllegalArgumentException("Invalid text.");
            } catch (IllegalArgumentException e) {
                e.printStackTrace();

                return null;
            }
        }

        text.setLineMaxSize(guiInterface.getWidth());
        text.setCentered(true);
        text.setPosition(new Vector2f(guiInterface.getX() - guiInterface.getWidth() + line.getLineLength(),
                -guiInterface.getY() - text.getTextHeight() / 2));
        GuiText guiText = new GuiText(guiInterface, text);
        guiText.setDisplayed(false);

        return guiText;
    }

    public boolean areTransitionsOfComponentDone(GuiComponent guiComponent) {
//        if (!this.components.containsKey(guiComponent) && guiComponent instanceof GuiBasics &&
//                guiComponent.getParent() instanceof GuiComponent)
//            guiComponent = (GuiComponent) guiComponent.getParent();

//        if (!this.components.containsKey(guiComponent))
//            return false;

        while (guiComponent.getParent() instanceof GuiComponent)
            guiComponent = (GuiComponent) guiComponent.getParent();

        if (!this.components.containsKey(guiComponent))
            return false;

        if (this.components.get(guiComponent).isEmpty())
            return true;


        return this.components.get(guiComponent).stream().allMatch(Transition::isDone);
    }

    @Override
    public void addComponentToParent(GuiComponent guiComponent, Transition... transitions) {
        if (guiComponent == null)
            return;

        this.components.remove(guiComponent);

        if (childrenConstraints != null && guiComponent.getParent().equals(childrenConstraints.getParent())) {
            childrenConstraints.addComponent(guiComponent);
            guiComponent.updateTexturePosition();
        }

        if (transitions.length == 0)
            this.components.put(guiComponent, new HashSet<>());
        else
            this.components.put(guiComponent, new HashSet<>(Arrays.asList(transitions)));
    }

    public void addComponent(GuiComponent guiComponent, Transition... transitions) {
        addComponentToParent(guiComponent);
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

    @Override
    public GuiTexture getDebugOutline() {
        return this.debugOutline;
    }

    public void setOnUpdate(UpdateCallback updateCallback) {
        this.updateCallback = updateCallback;
    }

    @Override
    public boolean update() {
        if (this.updateCallback == null)
            return false;

        this.updateCallback.onUpdate();

        return true;
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
        return new ArrayList<>(getComponents().keySet());
    }

    @Override
    public GuiTexture getTexture() {
        return this.background;
    }

    @Override
    public void setDisplayed(boolean displayed) {
        this.displayed = displayed;

        List<GuiComponent> list = getAllComponents().stream()
                .filter(guiComponent -> guiComponent.getParent().equals(this)).collect(Collectors.toList());
        list.forEach(guiC -> guiC.setDisplayed(displayed));
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


    public boolean hasTransitions() {
        return !this.transitions.isEmpty();
    }


    public void setChildrenConstraints(GuiGlobalConstraints guiConstraints) {
        guiConstraints.setParent(this);

        this.childrenConstraints = guiConstraints;
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

    public float getCornerRadius() {
        return this.cornerRadius;
    }

    public void setCornerRadius(float cornerRadius) {
        this.cornerRadius = cornerRadius;
    }

    protected GuiAbstractButton createButton(ButtonType buttonType, Background<?> background, Text text,
            Text tooltipText, GuiConstraintsManager constraints) {
        return createButton(buttonType, background, text, tooltipText, constraints, this);
    }

    protected GuiAbstractButton createButton(ButtonType buttonType, Background<?> background, Text text,
            Text tooltipText, GuiConstraintsManager constraints, GuiInterface parent) {
        switch (buttonType) {
            case CIRCULAR:
                return new GuiCircularButton(parent, background, text, tooltipText,
                        constraints); //TODO: Handle fontSize automatically (text length with button width)
            // TODO: Add color parameter
            case RECTANGLE:
                return new GuiRectangleButton(parent, background, text, tooltipText, constraints);
        }
        return null;
    }

    public void render() {
        GL30.glBindVertexArray(filledQuad.getVaoID());
        GL20.glEnableVertexAttribArray(0);

        GuiRenderer.loadTexture(getTexture(), cornerRadius);

        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, filledQuad.getVertexCount());
    }
}