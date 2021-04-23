package guis;

import static renderEngine.GuiRenderer.filledQuad;

import engineTester.Game;
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
import inputs.MouseUtils;
import inputs.callbacks.UpdateCallback;
import java.awt.Color;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import renderEngine.DisplayManager;
import renderEngine.GuiRenderer;
import textures.FontTexture;
import util.math.Vector2f;

public class Gui implements GuiInterface {

    public final static float CORNER_RADIUS = 8f;

    public final static FontType DEFAULT_FONT = new FontType(
            new FontTexture("roboto.png").getTextureID(), new File("res/roboto.fnt")); //TODO System-wide font

    private final Map<GuiComponent, Set<Transition>> components;

    private       GuiTexture background;
    private final GuiTexture debugOutline;

    private float x, y;
    private float width, height;

    private float cornerRadius;

    protected boolean focused, displayed;

    private UpdateCallback updateCallback;

    private GuiGlobalConstraints childrenConstraints;

    public Gui(Background<?> background) {
        setBackground(background);

        this.debugOutline = new GuiTexture(new Background<>(new Color((int) (Math.random() * 0x1000000))), this);

        this.components = new LinkedHashMap<>();

        Game.getInstance().addGui(this);
    }

    public void setBackground(Background<?> background) {
        this.background = new GuiTexture(background, new Vector2f(this.x, this.y),
                new Vector2f(this.width, this.height));
    }

    public Gui(int r, int g, int b) {
        this(new Background<>(new Color(r, g, b)));
    }

    public Gui(float r, float g, float b) {
        this(new Background<>(new Color(r, g, b)));
    }

    public void setConstraints(GuiConstraintsManager constraints) {
        this.width = 1;
        this.height = 1;
        this.x = 0;
        this.y = 0;

        GuiConstraintHandler guiConstraintHandler = new GuiConstraintHandler(this);
        guiConstraintHandler.setConstraints(constraints);

        updateTexturePosition();
    }

    public void updateTexturePosition() {
        this.background.getPosition().x = this.x;
        this.background.getPosition().y = this.y;

        this.background.getScale().x = this.width;
        float height = this.height;
        if (this.background.dokeepAspectRatio())
            height = Math.min(this.height,
                    this.width * ((float) DisplayManager.WIDTH / (float) DisplayManager.HEIGHT) *
                            ((float) this.background.getHeight() / (float) this.background.getWidth()));

        this.background.getScale().y = height;

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
        return new GuiText(guiInterface, text);
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

//    public void animate() {
//        Set<Transition> transitions;
//        if (isDisplayed())
//            transitions = getShowTransitions().stream()
//                    .filter(transition -> !transition.isDone() && transition.isStarted())
//                    .collect(Collectors.toSet());
//        else
//            transitions = getHideTransitions().stream()
//                    .filter(transition -> !transition.isDone() && transition.isStarted())
//                    .collect(Collectors.toSet());
//
//
//        transitions.forEach(transition -> {
//            boolean done = transition.animate(this);
//            if (done) {
//                transition.setDone(true);
//            }
//        });
//
//        Map<GuiComponent, Set<Transition>> map =
//                isDisplayed() ? getComponentsShowTransitions() : getComponentsHideTransitions();
//
//        map.forEach((guiComponent, lTransitions) -> lTransitions.stream().filter(transition -> !transition.isDone())
//                .forEach(transition -> {
//                            boolean done = transition.animate(guiComponent);
//                            if (done)
//                                transition.setDone(true);
//
//                            guiComponent.updateTexturePosition();
//                        }
//                ));
//
//        updateTexturePosition();
//    }

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


//    public Map<GuiComponent, Set<Transition>> getComponentsHideTransitions() {
//        Map<GuiComponent, Set<Transition>> transitions = new HashMap<>();
//
//        components.forEach((guiComponent, transitionSet) ->
//                transitions.put(guiComponent,
//                        transitionSet.stream().filter(transition -> transition.getTrigger() == Trigger.HIDE)
//                                .collect(Collectors.toSet())));
//
//        return transitions;
//    }
//
//    public Map<GuiComponent, Set<Transition>> getComponentsShowTransitions() {
//        Map<GuiComponent, Set<Transition>> transitions = new HashMap<>();
//
//        components.forEach((guiComponent, transitionSet) ->
//                transitions.put(guiComponent,
//                        transitionSet.stream().filter(transition -> transition.getTrigger() == Trigger.SHOW)
//                                .collect(Collectors.toSet())));
//
//        return transitions;
//    }

    public void setX(float x) {
        if (x < -1 || x > 1)
            throw new IllegalArgumentException("New coordinates don't belong in window");

        this.x = x;

        updateTexturePosition();
    }

    public void setY(float y) {
        if (y < -1 || y > 1)
            throw new IllegalArgumentException("New coordinates don't belong in window");

        this.y = y;

        updateTexturePosition();
    }

    public void setWidth(float width) {
        if (width < 0 || width > 2)
            throw new IllegalArgumentException("New width don't fit in window");

        this.width = width;

        updateTexturePosition();
    }

    public void setHeight(float height) {
        if (height < 0 || height > 2)
            throw new IllegalArgumentException("New height don't fit in window");

        this.height = height;

        updateTexturePosition();
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
        if (this.displayed == displayed)
            return;

        this.displayed = displayed;

        Game gameInstance = Game.getInstance();
        if (displayed)
            gameInstance.getDisplayedGuis().add(this);
        else
            gameInstance.getDisplayedGuis().remove(this);

        List<GuiComponent> list = getAllComponents().stream()
                .filter(GuiComponent::isDisplayedByDefault)
                .filter(guiComponent -> guiComponent.getParent().equals(this)).collect(Collectors.toList());
        list.forEach(guiC -> {
            guiC.setDisplayed(displayed);
            if (MouseUtils.isCursorInGuiComponent(guiC))
                guiC.onEnter();
        });
    }

    public Map<GuiComponent, Set<Transition>> getComponents() {
        return this.components;
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


    public void setChildrenConstraints(GuiGlobalConstraints guiConstraints) {
        guiConstraints.setParent(this);

        this.childrenConstraints = guiConstraints;
    }

    public static void toggleGui(Gui gui) {
        if (gui == null)
            return;

        gui.setDisplayed(!gui.isDisplayed());
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