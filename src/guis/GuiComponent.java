package guis;

import static renderEngine.GuiRenderer.DEFAULT;
import static renderEngine.GuiRenderer.unfilledQuad;

import display.Display;
import guis.constraints.GuiConstraintHandler;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.layout.GuiLayout;
import guis.presets.Background;
import guis.transitions.Transition;
import inputs.MouseUtils;
import inputs.callbacks.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import models.RawModel;
import util.math.Vector2f;

public abstract class GuiComponent implements GuiInterface {

    public static int maxID = 1;

    protected int type = DEFAULT;

    public int ID;

    protected float x, y;

    protected float width, height;

    protected final GuiInterface parent;

    private final List<GuiTexture> textures = new ArrayList<>();

    private int textureIndex;

    public final GuiTexture debugOutline;
    private      boolean    displayDebugOutline = true;

    private EnterCallback   onEnterCallback   = () -> {
    };
    private LeaveCallback   onLeaveCallback   = () -> {
    };
    private HoverCallback   onHoverCallback   = () -> {
    };
    private ScrollCallback  onScrollCallback  = (xOffset, yOffset) -> {
    };
    private UpdateCallback  onUpdateCallback  = () -> {
    };
    private OpenCallback    onOpenCallback    = () -> {
    };
    private CloseCallback   onCloseCallback   = () -> {
    };
    private UnfocusCallback onUnfocusCallback = () -> {
    };
    private FocusCallback   onFocusCallback   = () -> {
    };

    // Unfocus if click outside of text input
    private boolean unfocusOnClick = true;

    private boolean displayed, displayedByDefault, focused;

    protected float cornerRadius;

    protected GuiLayout layout;

    public GuiComponent(GuiInterface parent, Background<?> background) {
        if (parent == null)
            throw new NullPointerException("Parent null");

        this.ID = maxID++;

        this.parent = parent;
        this.width = parent.getWidth();
        this.height = parent.getHeight();
        this.x = parent.getX();
        this.y = parent.getY();

        this.displayed = this.displayedByDefault = true;

        this.debugOutline = new GuiTexture(new Background<>(new Color((int) (Math.random() * 0x1000000))), this);

        if (background == null)
            background = Background.NO_BACKGROUND;

        this.textures.add(new GuiTexture(background, new Vector2f(this.x, this.y),
                new Vector2f(this.width, this.height)));

        parent.addComponent(this);
    }

    public GuiComponent(GuiInterface parent) {
        this(parent, null);
    }

    @Override
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
        addElementToLayout(guiComponent);

        this.parent.addComponentToParent(guiComponent, transitions);
    }

    public void addComponentToParent(GuiComponent guiComponent, Transition... transitions) {
        this.parent.addComponentToParent(guiComponent, transitions);
    }

    public void addElementToLayout(GuiComponent guiComponent) {
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

    @Override
    public final void setOnUpdate(UpdateCallback updateCallback) {
        this.onUpdateCallback = updateCallback;
    }

    @Override
    public final void setOnClose(CloseCallback closeCallback) {
        this.onCloseCallback = closeCallback;
    }

    @Override
    public final void setOnOpen(OpenCallback openCallback) {
        this.onOpenCallback = openCallback;
    }

    @Override
    public boolean update() {
        this.onUpdateCallback.onUpdate();

        return true;
    }

    public final int getType() {
        return this.type;
    }

    public final void setType(int type) {
        this.type = type;
    }

    public final void updateTexturePosition() {
        for (GuiTexture guiTexture : this.textures) {
            guiTexture.getScale().setX(this.width);
            float height = this.height;
            if (guiTexture.dokeepAspectRatio())
                height = Math.min(this.height,
                        this.width *
                                ((float) Display.getWindow().getWidth() / (float) Display.getWindow().getHeight()) *
                                ((float) guiTexture.getHeight() / (float) guiTexture.getWidth()));

            guiTexture.getScale().setY(height);
            guiTexture.getPosition().setX(this.x);
            guiTexture.getPosition().setY(this.y);
        }

        this.debugOutline.getScale().setX(this.width);
        this.debugOutline.getScale().setY(this.height);
        this.debugOutline.getPosition().setX(this.x);
        this.debugOutline.getPosition().setY(this.y);
    }

    @Override
    public final GuiTexture getDebugOutline() {
        return this.debugOutline;
    }

    @Override
    public final boolean displayDebugOutline() {
        return this.displayDebugOutline;
    }

    public void setDisplayDebugOutline(boolean displayDebugOutline) {
        this.displayDebugOutline = displayDebugOutline;
    }

    public final void onEnter() {
        this.onEnterCallback.onEnter();
    }

    public final void onLeave() {
        this.onLeaveCallback.onLeave();
    }

    public final void onHover() {
        this.onHoverCallback.onHover();
    }

    public final void onScroll(double xOffset, double yOffset) {
        this.onScrollCallback.onScroll(xOffset, yOffset);
    }

    public final void setOnHover(HoverCallback onHoverCallback) {
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
    public final float getX() {
        return this.x;
    }

    @Override
    public final float getY() {
        return this.y;
    }

    @Override
    public final float getWidth() {
        return this.width;
    }

    @Override
    public final float getHeight() {
        return this.height;
    }

    @Override
    public void setX(float x) {
        this.x = x;

        getChildrenComponents().forEach(guiComponent -> {
            guiComponent.setX(x);
            guiComponent.updateTexturePosition();
        });

        updateTexturePosition();
    }

    @Override
    public void setY(float y) {
        this.y = y;

        getChildrenComponents().forEach(guiComponent -> {
            guiComponent.setY(y);
            guiComponent.updateTexturePosition();
        });

        updateTexturePosition();
    }

    public void setWidth(float width) {
        this.width = width;

        getChildrenComponents().forEach(guiComponent -> {
            guiComponent.setWidth(width);
            guiComponent.updateTexturePosition();
        });

        updateTexturePosition();
    }

    public void setHeight(float height) {
        this.height = height;

        getChildrenComponents().forEach(guiComponent -> {
            guiComponent.setHeight(height);
            guiComponent.updateTexturePosition();
        });

        updateTexturePosition();
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
            updateTexturePosition();
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
    public final void setDisplayed(boolean displayed) {
        if (this.displayed == displayed)
            return;

        this.displayed = displayed;

        if (displayed)
            this.onOpenCallback.onOpen();
        else {
            unfocus();
            this.onCloseCallback.onClose();
        }

        List<GuiComponent> list = GuiComponent.getParentGui(this).getAllComponents().stream()
                .filter(GuiComponent::isDisplayedByDefault)
                .filter(guiComponent -> guiComponent.getParent().equals(this)).collect(Collectors.toList());
        list.forEach(guiC -> {
            guiC.setDisplayed(displayed);
            if (MouseUtils.isCursorInGuiComponent(guiC)) // happens if shown when cursor is there
                guiC.onEnter();
        });
    }

    public void setLayout(GuiLayout guiConstraints) {
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
    public void focus() {
        this.focused = true;
        this.onFocusCallback.onFocus();
    }

    @Override
    public void unfocus() {
        this.focused = false;
        this.onUnfocusCallback.onUnfocus();
    }

    @Override
    public boolean isFocused() {
        return this.focused;
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

    public boolean isUnfocusOnClick() {
        return this.unfocusOnClick;
    }

    public void setOnUnfocusCallback(UnfocusCallback onUnfocusCallback) {
        this.onUnfocusCallback = onUnfocusCallback;
    }

    public void setUnfocusOnClick(boolean unfocusOnClick) {
        this.unfocusOnClick = unfocusOnClick;
    }

    public void setOnFocusCallback(FocusCallback onFocusCallback) {
        this.onFocusCallback = onFocusCallback;
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
