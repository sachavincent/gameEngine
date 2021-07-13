package guis.presets.checkbox;

import guis.GuiInterface;
import guis.basics.GuiShape;
import guis.constraints.GuiConstraintsManager;
import guis.presets.Background;
import guis.presets.GuiAbstractShapePreset;
import guis.presets.GuiClickablePreset;
import guis.presets.GuiPreset;
import inputs.ClickType;
import inputs.callbacks.MousePressCallback;
import inputs.callbacks.MouseReleaseCallback;
import java.awt.Color;

public abstract class GuiAbstractCheckbox extends GuiPreset implements GuiAbstractShapePreset, GuiClickablePreset {

    protected GuiShape borderLayout;
    protected GuiShape shape;

    private CheckCallback        onCheckCallback;
    private UncheckCallback      onUncheckCallback;
    private MousePressCallback   onMousePressCallback;
    private MouseReleaseCallback onMouseReleaseCallback;

    public ClickType clickType;

    private boolean releaseInsideNeeded = true; // If checkbox needs to be released inside of this component

    GuiAbstractCheckbox(GuiInterface parent, Background<?> background, Color borderColor,
            GuiConstraintsManager constraintsManager) {
        super(parent, Background.NO_BACKGROUND, constraintsManager);

        this.shape = createShape(background, borderColor);

        setupComponents();
        this.clickType = ClickType.NONE;
    }

    GuiAbstractCheckbox(GuiInterface parent, Background<?> background, Color borderColor) {
        this(parent, background, borderColor, null);
    }

    private void setupComponents() {
        setListeners();
    }

    private void setListeners() {
        setOnMouseRelease(button -> {
            return false;
        });

        setOnMousePress(button -> {
            return false;
        });

        setOnHover(() -> {
        });

        setOnLeave(() -> {
        });

        setOnEnter(() -> {
        });
    }

    /**
     * Click + release within component
     */
    @Override
    public boolean onMousePress(int button) {
        if (this.onMousePressCallback == null)
            return false;

        return this.onMousePressCallback.onPress(button);
    }

    @Override
    public boolean isReleaseInsideNeeded() {
        return this.releaseInsideNeeded;
    }

    @Override
    public void setReleaseInsideNeeded(boolean releaseInsideNeeded) {
        this.releaseInsideNeeded = releaseInsideNeeded;
    }

    @Override
    public GuiShape getShape() {
        return this.shape;
    }

    @Override
    public void reset() {

    }

    public GuiShape getBorderLayout() {
        return this.borderLayout;
    }

    @Override
    public boolean isClicked() {
        return this.clickType != ClickType.NONE;
    }

    @Override
    public ClickType getClickType() {
        return this.clickType;
    }

    public void setOnCheckCallback(CheckCallback onCheckCallback) {
        this.onCheckCallback = onCheckCallback;
    }

    public void setOnUncheckCallback(UncheckCallback onUncheckCallback) {
        this.onUncheckCallback = onUncheckCallback;
    }

    @Override
    public boolean onMouseRelease(int button) {
        if (this.onMouseReleaseCallback == null)
            return false;

        return this.onMouseReleaseCallback.onRelease(button);
    }

    @Override
    public void setOnMouseRelease(MouseReleaseCallback onMouseReleaseCallback) {
        this.onMouseReleaseCallback = onMouseReleaseCallback;
    }

    @Override
    public void setOnMousePress(MousePressCallback onMousePressCallback) {
        this.onMousePressCallback = button -> {
            if (button == this.clickType.getButton()) {
                setClickType(ClickType.NONE);

                if (this.onCheckCallback != null)
                    this.onUncheckCallback.onUncheck();
            } else {
                setClickType(ClickType.getClickTypeFromButton(button));

                if (this.onCheckCallback != null)
                    this.onCheckCallback.onCheck();
            }

            updateTexturePosition();
           return onMousePressCallback.onPress(button);
        };
    }

    @Override
    public void setClickType(ClickType clickType) {
        this.clickType = clickType;
    }

    @FunctionalInterface
    public interface CheckCallback {

        void onCheck();
    }

    @FunctionalInterface
    public interface UncheckCallback {

        void onUncheck();
    }

}
