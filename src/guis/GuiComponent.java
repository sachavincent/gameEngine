package guis;

import static renderEngine.GuiRenderer.unfilledQuad;

import guis.constraints.GuiConstraintHandler;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.GuiGlobalConstraints;
import guis.presets.Background;
import guis.transitions.Transition;
import inputs.ClickType;
import inputs.MouseUtils;
import inputs.callbacks.EnterCallback;
import inputs.callbacks.HoverCallback;
import inputs.callbacks.LeaveCallback;
import inputs.callbacks.ScrollCallback;
import inputs.callbacks.UpdateCallback;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import models.RawModel;
import renderEngine.DisplayManager;
import util.math.Vector2f;

public abstract class GuiComponent implements GuiInterface {

    public static int maxID = 1;

    public int ID;

    private float x, y;

    private float width, height;

    protected final GuiInterface parent;

    private final List<GuiTexture> textures = new ArrayList<>();

    private int textureIndex;

    public final GuiTexture debugOutline;
    private      boolean    displayDebugOutline = true;

    private EnterCallback  onEnterCallback;
    private LeaveCallback  onLeaveCallback;
    private HoverCallback  onHoverCallback;
    private ScrollCallback onScrollCallback;
    private UpdateCallback onUpdateCallback;

    private boolean displayed, displayedByDefault;

    public ClickType clickType;

    protected float cornerRadius;

    protected GuiGlobalConstraints layout;

    public GuiComponent(GuiInterface parent) {
        if (parent == null)
            throw new NullPointerException("Parent null");

        this.ID = maxID++;

        this.parent = parent;
        this.width = parent.getWidth();
        this.height = parent.getHeight();
        this.x = parent.getX();
        this.y = parent.getY();

        this.clickType = ClickType.NONE;

        this.displayed = this.displayedByDefault = true;

        this.debugOutline = new GuiTexture(new Background<>(new Color((int) (Math.random() * 0x1000000))), this);

        parent.addComponent(this);
    }

    public GuiComponent(GuiInterface parent, Background<?> background) {
        this(parent);

        if (background != null)
            this.textures.add(new GuiTexture(background, new Vector2f(this.x, this.y),
                    new Vector2f(this.width, this.height)));
    }

    public float getCornerRadius() {
        return this.cornerRadius;
    }

    public void setDisplayedByDefault(boolean displayedByDefault) {
        this.displayedByDefault = displayedByDefault;
        if (!displayedByDefault)
            this.displayed = false;
//        getParentGui(this).getAllComponents().stream()
//                .filter(guiComponent -> guiComponent.getParent().equals(this))
//                .forEach(guiComponent -> guiComponent.setDisplayedByDefault(displayedByDefault));
    }

    public boolean isDisplayedByDefault() {
        return this.displayedByDefault;
    }

    public void setCornerRadius(float cornerRadius) {
        this.cornerRadius = cornerRadius;

        getChildrenComponents()
                .forEach(guiComponent -> guiComponent.setCornerRadius(cornerRadius));
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
        if (layout != null && this.equals(layout.getParent())) {
            layout.addComponent(guiComponent);
        }
    }

    public void setConstraints(GuiConstraintsManager constraints) {
        if (constraints == null)
            return;

        GuiConstraintHandler guiConstraintHandler = new GuiConstraintHandler(this.parent, this);
        guiConstraintHandler.setConstraints(constraints);

        updateTexturePosition();
    }

    public void setOnUpdate(UpdateCallback updateCallback) {
        this.onUpdateCallback = updateCallback;
    }

    @Override
    public boolean update() {
        if (this.onUpdateCallback == null)
            return false;

        this.onUpdateCallback.onUpdate();

        return true;
    }

    public void updateTexturePosition() {
        for (GuiTexture guiTexture : this.textures) {
            guiTexture.getScale().x = this.width;
            float height = this.height;
            if (guiTexture.dokeepAspectRatio())
                height = Math.min(this.height,
                        this.width * ((float) DisplayManager.WIDTH / (float) DisplayManager.HEIGHT) *
                                ((float) guiTexture.getHeight() / (float) guiTexture.getWidth()));

            guiTexture.getScale().y = height;
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

    @Override
    public boolean displayDebugOutline() {
        return this.displayDebugOutline;
    }

    public void setDisplayDebugOutline(boolean displayDebugOutline) {
        this.displayDebugOutline = displayDebugOutline;
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

        getChildrenComponents()
                .forEach(guiComponent -> {
                    guiComponent.x = x;
                    guiComponent.updateTexturePosition();
                });

        updateTexturePosition();
    }

    @Override
    public void setY(float y) {
        this.y = y;

        getChildrenComponents()
                .forEach(guiComponent -> {
                    guiComponent.y = y;
                    guiComponent.updateTexturePosition();
                });

        updateTexturePosition();
    }

    public void setWidth(float width) {
        this.width = width;

        getChildrenComponents()
                .forEach(guiComponent -> {
                    guiComponent.width = width;
                    guiComponent.updateTexturePosition();
                });

        updateTexturePosition();
    }

    public void setHeight(float height) {
        this.height = height;

        getChildrenComponents()
                .forEach(guiComponent -> {
                    guiComponent.height = height;
                    guiComponent.updateTexturePosition();
                });

        updateTexturePosition();
    }

    public boolean isClicked() {
        return this.clickType != ClickType.NONE;
    }

    public ClickType getClickType() {
        return this.clickType;
    }

    public void setClickType(ClickType clickType) {
        this.clickType = clickType;
    }

    @Override
    public GuiTexture getTexture() {
        if (this.textures.size() > textureIndex)
            return this.textures.get(textureIndex);

        return new GuiTexture(Background.BLACK_BACKGROUND, this);
    }

    public void setTexture(Background<?> background) {
        if (this.textures.size() == 1) {
            this.textures.clear();
            this.textures.add(new GuiTexture(background, this));
        }
    }

    public void scale(float scale) {
        this.width = this.width * scale;
        this.height = this.height * scale;

        getChildrenComponents()
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
        if (this.displayed == displayed)
            return;

        this.displayed = displayed;

        List<GuiComponent> list = GuiComponent.getParentGui(this).getAllComponents().stream()
                .filter(GuiComponent::isDisplayedByDefault)
                .filter(guiComponent -> guiComponent.getParent().equals(this)).collect(Collectors.toList());
        list.forEach(guiC -> {
            guiC.setDisplayed(displayed);
            if (MouseUtils.isCursorInGuiComponent(guiC)) // happens if shown when cursor is there
                guiC.onEnter();
        });
    }

    public void setLayout(GuiGlobalConstraints guiConstraints) {
        guiConstraints.setParent(this);

        boolean addAgain = this.layout != null;

        this.layout = guiConstraints;

        if (addAgain) {
            List<GuiComponent> components = getAllComponentsBelow();
            components.forEach(guiComponent -> guiComponent.getParent().removeComponent(guiComponent));
            components.forEach(guiComponent -> guiComponent.getParent().addComponent(guiComponent));
        }
    }

    public List<GuiComponent> getAllComponentsBelow() {
        List<GuiComponent> childrenComponents = getChildrenComponents();
        List<GuiComponent> components = childrenComponents.stream().map(GuiComponent::getAllComponentsBelow)
                .flatMap(Collection::stream).collect(Collectors.toList());
        childrenComponents.addAll(components);

        return childrenComponents;
    }

    public List<GuiComponent> getChildrenComponents() {
        return GuiComponent.getParentGui(this).getAllComponents().stream()
                .filter(guiComponent -> guiComponent.getParent().equals(this))
                .collect(Collectors.toList());
    }

    public void setTextureIndex(int textureIndex) {
        this.textureIndex = textureIndex;
    }

    public int getTextureIndex() {
        return this.textureIndex;
    }

    @Override
    public boolean isDisplayed() {
        return this.displayed;
    }

    @Override
    public void setAlpha(float alpha) {
        if (this.textures.size() > textureIndex) {
            GuiTexture guiTexture = this.textures.get(textureIndex);

            guiTexture.setAlpha(alpha);
        }
    }

    public void addBackground(Background<?> background) {
        if (background != null)
            this.textures.add(new GuiTexture(background, this));

        updateTexturePosition();
    }

    public void addTexture(GuiTexture texture) {
        if (texture != null)
            this.textures.add(texture);

        updateTexturePosition();
    }

    public int getNbTextures() {
        return this.textures.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        GuiComponent that = (GuiComponent) o;
        return this.ID == that.ID;
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
    public int hashCode() {
        return Objects.hash(this.ID);
    }

    public abstract void render();

    public RawModel getTemplate() {
        return unfilledQuad;
    }
}
