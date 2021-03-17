package guis;

import static renderEngine.GuiRenderer.unfilledQuad;

import guis.constraints.GuiConstraintHandler;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.GuiGlobalConstraints;
import guis.presets.Background;
import guis.transitions.Transition;
import inputs.callbacks.EnterCallback;
import inputs.callbacks.HoverCallback;
import inputs.callbacks.LeaveCallback;
import inputs.callbacks.ScrollCallback;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import models.RawModel;
import util.math.Vector2f;

public abstract class GuiComponent implements GuiInterface {

    public static int maxID = 1;

    public int ID;

    private float x, y;

    private float width, height;

    protected final GuiInterface parent;

    private final List<GuiTexture> textures = new ArrayList<>();

    private int textureIndex;

    private final GuiTexture debugOutline;

    private EnterCallback  onEnterCallback;
    private LeaveCallback  onLeaveCallback;
    private HoverCallback  onHoverCallback;
    private ScrollCallback onScrollCallback;

    private boolean displayed;

    public boolean clicked;

    protected float cornerRadius = Gui.CORNER_RADIUS;

    protected GuiGlobalConstraints childrenConstraints;

    public GuiComponent(GuiInterface parent) {
        if (parent == null)
            throw new NullPointerException("Parent null");

        this.ID = maxID++;

        this.parent = parent;
        this.width = parent.getWidth();
        this.height = parent.getHeight();
        this.x = parent.getX();
        this.y = parent.getY();

        this.displayed = true;

        this.debugOutline = new GuiTexture(new Background<>(new Color((int) (Math.random() * 0x1000000))), this);


        parent.addComponent(this);
    }

    public GuiComponent(GuiInterface parent, Background<?> texture) {
        this(parent);

        this.textures.add(new GuiTexture(texture, new Vector2f(this.x, this.y), new Vector2f(this.width, this.height)));
    }

    public float getCornerRadius() {
        return this.cornerRadius;
    }

    public void setCornerRadius(float cornerRadius) {
        this.cornerRadius = cornerRadius;

        getParentGui(this).getAllComponents().stream()
                .filter(guiComponent -> guiComponent.getParent().equals(this))
                .forEach(guiComponent -> guiComponent.cornerRadius = cornerRadius);
    }

    public void removeComponent(GuiComponent guiComponent) {
        this.parent.removeComponent(guiComponent);
    }

    public void clearComponents() {
        Set<GuiComponent> collect = getParentGui(this).getComponents().keySet().stream()
                .filter(component -> component.getParent().equals(this))
                .collect(Collectors.toSet());

        collect.forEach(GuiComponent::clearComponents);
        collect.forEach(guiComponent -> getParentGui(this).removeComponent(guiComponent));
    }

    @Override
    public void addComponent(GuiComponent guiComponent, Transition... transitions) {
        addElementToChildrenConstraints(guiComponent);

        this.parent.addComponentToParent(guiComponent, transitions);
    }

    public void addComponentToParent(GuiComponent guiComponent, Transition... transitions) {
        this.parent.addComponentToParent(guiComponent, transitions);
    }

    public void addElementToChildrenConstraints(GuiComponent guiComponent) {
        if (childrenConstraints != null && this.equals(childrenConstraints.getParent())) {
            childrenConstraints.addComponent(guiComponent);
        }
    }

    public void setConstraints(GuiConstraintsManager constraints) {
        if (constraints == null)
            return;

        GuiConstraintHandler guiConstraintHandler = new GuiConstraintHandler(parent, this);
        guiConstraintHandler.setConstraints(constraints);

        updateTexturePosition();
    }

    public void updateTexturePosition() {
        for (GuiTexture guiTexture : this.textures) {
            guiTexture.getScale().x = this.width;
            guiTexture.getScale().y = this.height;
            guiTexture.getPosition().x = this.x;
            guiTexture.getPosition().y = this.y;
        }

        this.debugOutline.getScale().x = this.width;
        this.debugOutline.getScale().y = this.height;
        this.debugOutline.getPosition().x = this.x;
        this.debugOutline.getPosition().y = this.y;
    }

    @Override
    public GuiTexture getDebugOutline() {
        return this.debugOutline;
    }

    public void onEnter() {
        if (this.onEnterCallback == null)
            return;

        this.onEnterCallback.onEnter();
    }

    public void onLeave() {
        if (this.onLeaveCallback == null)
            return;

        this.onLeaveCallback.onLeave();
    }


    public void onHover() {
        if (this.onHoverCallback == null)
            return;

        this.onHoverCallback.onHover();
    }

    public void onScroll() {
        if (this.onScrollCallback == null)
            return;

        this.onScrollCallback.onScroll();
    }

    public void onType() {

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
        this.x = x;

        getParentGui(this).getAllComponents().stream()
                .filter(guiComponent -> guiComponent.getParent().equals(this))
                .forEach(guiComponent -> {
                    guiComponent.x = x;
                    guiComponent.updateTexturePosition();
                });

        updateTexturePosition();
    }

    @Override
    public void setY(float y) {
        this.y = y;

        getParentGui(this).getAllComponents().stream()
                .filter(guiComponent -> guiComponent.getParent().equals(this))
                .forEach(guiComponent -> {
                    guiComponent.y = y;
                    guiComponent.updateTexturePosition();
                });

        updateTexturePosition();
    }

    public void setWidth(float width) {
        this.width = width;

        getParentGui(this).getAllComponents().stream()
                .filter(guiComponent -> guiComponent.getParent().equals(this))
                .forEach(guiComponent -> {
                    guiComponent.width = width;
                    guiComponent.updateTexturePosition();
                });

        updateTexturePosition();
    }

    public void setHeight(float height) {
        this.height = height;

        getParentGui(this).getAllComponents().stream()
                .filter(guiComponent -> guiComponent.getParent().equals(this))
                .forEach(guiComponent -> {
                    guiComponent.height = height;
                    guiComponent.updateTexturePosition();
                });

        updateTexturePosition();
    }

    public boolean isClicked() {
        return this.clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    @Override
    public GuiTexture getTexture() {
        if (this.textures.size() > textureIndex)
            return this.textures.get(textureIndex);

        return new GuiTexture(Background.BLACK_BACKGROUND, this);
    }

    public void scale(float scale) {
        this.width = this.width * scale;
        this.height = this.height * scale;

        getParentGui(this).getAllComponents().stream()
                .filter(guiComponent -> guiComponent.getParent().equals(this))
                .forEach(guiComponent -> {
                    guiComponent.width = guiComponent.width * scale;
                    guiComponent.height = guiComponent.height * scale;
                    guiComponent.updateTexturePosition();
                });

        updateTexturePosition();
    }

    public static Gui getParentGui(GuiComponent guiComponent) {
        if (guiComponent.getParent() instanceof Gui)
            return (Gui) guiComponent.getParent();

        return getParentGui((GuiComponent) guiComponent.getParent());
    }

    public GuiInterface getParent() {
        return this.parent;
    }

    @Override
    public void setDisplayed(boolean displayed) {
        this.displayed = displayed;

        Gui parent = GuiComponent.getParentGui(this);
        List<GuiComponent> list = parent.getAllComponents().stream()
                .filter(guiComponent -> guiComponent.getParent().equals(this)).collect(Collectors.toList());
        list.forEach(guiC -> guiC.setDisplayed(displayed));
    }

    public void setChildrenConstraints(GuiGlobalConstraints guiConstraints) {
        guiConstraints.setParent(this);

        this.childrenConstraints = guiConstraints;
    }

    public void setTextureIndex(int textureIndex) {
        this.textureIndex = textureIndex;
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
                '}';
    }


    @Override
    public void setAlpha(float alpha) {
        if (this.textures.size() > textureIndex) {
            GuiTexture guiTexture = this.textures.get(textureIndex);

            guiTexture.setAlpha(alpha);
        }
    }

    public void addTexture(GuiTexture guiTexture) {
        this.textures.add(guiTexture);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        GuiComponent that = (GuiComponent) o;
        return ID == that.ID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID);
    }

    public abstract void render();

    public RawModel getTemplate() {
        return unfilledQuad;
    }
}
