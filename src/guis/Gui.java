package guis;

import static renderEngine.GuiRenderer.filledQuad;
import static util.Utils.RES_PATH;

import engineTester.Game;
import fontMeshCreator.FontType;
import guis.constraints.GuiConstraintHandler;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.layout.GuiLayout;
import guis.prefabs.GuiSelectedItem;
import guis.presets.Background;
import guis.transitions.Transition;
import inputs.MouseUtils;
import inputs.callbacks.CloseCallback;
import inputs.callbacks.OpenCallback;
import inputs.callbacks.UpdateCallback;
import java.awt.Color;
import java.io.File;
import java.util.*;
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
            new FontTexture(new File(RES_PATH + "/roboto.png")).getTextureID(), new File(RES_PATH + "/roboto.fnt")); //TODO System-wide font

    private final Map<GuiComponent, Set<Transition>> components;

    private       GuiTexture background;
    private final GuiTexture debugOutline;

    private float x, y;
    private float width, height;

    private float cornerRadius;

    protected boolean focused, displayed;

    private UpdateCallback onUpdateCallback = () -> {
    };
    private OpenCallback   onOpenCallback   = () -> {
    };
    private CloseCallback  onCloseCallback  = () -> {
    };

    private boolean displayDebugOutline = true;

    private GuiLayout layout;

    public Gui(Background<?> background) {
        setBackground(background);

        this.debugOutline = new GuiTexture(new Background<>(new Color((int) (Math.random() * 0x1000000))), this);

        this.components = new LinkedHashMap<>();
        this.displayed = true;
        Game.getInstance().addGui(this);
    }

    public void setBackground(Background<?> background) {
        this.background = new GuiTexture(background, new Vector2f(this.x, this.y),
                new Vector2f(this.width, this.height));
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

    public int getType() {
        return GuiRenderer.DEFAULT;
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

    @Override
    public void addComponentToParent(GuiComponent guiComponent, Transition... transitions) {
        if (guiComponent == null)
            return;

        this.components.remove(guiComponent);

        if (layout != null && guiComponent.getParent().equals(layout.getParent())) {
            layout.addComponent(guiComponent);
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

    @Override
    public boolean displayDebugOutline() {
        return this.displayDebugOutline;
    }

    @Override
    public final boolean update() {
        if (this.onUpdateCallback == null)
            return false;

        this.onUpdateCallback.onUpdate();

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
    public final void setDisplayed(boolean displayed) {
        if (this.displayed == displayed)
            return;

        this.displayed = displayed;

        Game gameInstance = Game.getInstance();
        if (displayed) {
            gameInstance.getDisplayedGuis().add(this);

            this.onOpenCallback.onOpen();
        } else {
            gameInstance.getDisplayedGuis().remove(this);

            this.onCloseCallback.onClose();
        }
//        List<GuiComponent> list = getAllComponents().stream()
//                .filter(GuiComponent::isDisplayedByDefault)
//                .filter(guiComponent -> guiComponent.getParent().equals(this)).collect(Collectors.toList());

        List<GuiComponent> enteredGuiComponents = MouseUtils.ENTERED_GUI_COMPONENTS;
        enteredGuiComponents.forEach(GuiComponent::onLeave);
        enteredGuiComponents.clear();
        if (!(this instanceof GuiSelectedItem))
            MouseUtils.OnMouseMove(MouseUtils.getCursorPos());

        getAllComponents().stream()
                .filter(guiComponent -> guiComponent.isDisplayed() == displayed)
                .filter(guiComponent -> guiComponent.isDisplayedByDefault() || !displayed)
                .forEach(guiComponent -> guiComponent.setDisplayed(displayed));
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

    @Override
    public void focus() {
        this.focused = true;
    }

    @Override
    public void unfocus() {
        this.focused = false;
    }

    @Override
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

    public void setLayout(GuiLayout guiConstraints) {
        guiConstraints.setParent(this);

        this.layout = guiConstraints;
    }

    public static void toggleGui(Gui gui) {
        if (gui == null)
            return;

        gui.setDisplayed(!gui.isDisplayed());
    }

    @Override
    public float getCornerRadius() {
        return this.cornerRadius;
    }

    public void setCornerRadius(float cornerRadius) {
        this.cornerRadius = cornerRadius;
    }

    public void setOnUpdate(UpdateCallback updateCallback) {
        this.onUpdateCallback = updateCallback;
    }

    public void setOnOpen(OpenCallback openCallback) {
        this.onOpenCallback = openCallback;
    }

    public void setOnClose(CloseCallback closeCallback) {
        this.onCloseCallback = closeCallback;
    }

    public void render() {
        GL30.glBindVertexArray(filledQuad.getVaoID());
        GL20.glEnableVertexAttribArray(0);

        GuiRenderer.loadGui(this);

        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, filledQuad.getVertexCount());
    }
}