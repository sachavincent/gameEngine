package guis.presets.sliders;

import static guis.Gui.DEFAULT_FONT;

import fontMeshCreator.Text;
import guis.GuiInterface;
import guis.basics.GuiRectangle;
import guis.basics.GuiShape;
import guis.basics.GuiText;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RelativeConstraint;
import guis.constraints.Side;
import guis.constraints.StickyConstraint;
import guis.presets.Background;
import guis.presets.GuiPreset;
import guis.presets.buttons.GuiAbstractButton;
import guis.presets.buttons.GuiRectangleButton;
import java.awt.Color;
import org.lwjgl.glfw.GLFW;

public abstract class GuiSlider extends GuiPreset {

    protected Interval interval;

    protected GuiShape          outline;
    protected GuiAbstractButton sliderCursor;

    protected final GuiRectangleButton sliderBase;
    protected       GuiText            valueText;

    protected boolean clicked;

    protected float value;

    protected float   position;
    protected float   maxPosition;
    protected float   minPosition;
    protected boolean slidingEnabled;

    protected ValueChangedCallback onValueChangedCallback;
    protected CursorMoveCallback   onCursorMoveCallback;

    // most extreme value aligns it to the edge of the base
    protected boolean alignCursorWithBase;

    public GuiSlider(GuiInterface parent, Interval interval, Color color, Color colorMovable,
            GuiConstraintsManager constraintsManager) {
        super(parent, Background.NO_BACKGROUND, constraintsManager);

        this.interval = interval;
        this.value = this.interval.defaultValue;

        this.sliderBase = new GuiRectangleButton(this, new Background<>(color), null,
                new GuiConstraintsManager.Builder()
                        .setDefault()
                        .setWidthConstraint(new RelativeConstraint(1))
                        .setHeightConstraint(new RelativeConstraint(1)).create());
        this.sliderBase.setCornerRadius(0);

        this.sliderCursor = new GuiRectangleButton(this, new Background<>(colorMovable), null, getCursorConstraints());
        this.sliderCursor.setCornerRadius(0);
        this.slidingEnabled = true;
        this.alignCursorWithBase = true;
        startCursorSetup();
    }

    public void setAlignCursorWithBase(boolean alignCursorWithBase) {
        this.alignCursorWithBase = alignCursorWithBase;
    }

    public boolean isAlignCursorWithBase() {
        return this.alignCursorWithBase;
    }

    protected abstract GuiConstraintsManager getCursorConstraints();

    private void addOutline() {
        this.outline = new GuiRectangle(this, Background.BLACK_BACKGROUND, new RelativeConstraint(1),
                new RelativeConstraint(1), false);
        this.outline.setOutlineWidth(2);
    }

    public GuiAbstractButton getSliderCursor() {
        return this.sliderCursor;
    }

    public GuiRectangleButton getSliderBase() {
        return this.sliderBase;
    }

    public void setOnCursorMoveCallback(CursorMoveCallback onCursorMoveCallback) {
        this.onCursorMoveCallback = onCursorMoveCallback;
    }

    private void startCursorSetup() {
        setValue(this.interval.defaultValue);

        this.sliderCursor.setReleaseInsideNeeded(false);
        setListeners();
    }

    private void setListeners() {
        this.sliderCursor.setOnMousePress(button -> {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1)
                this.clicked = true;
        });

        this.sliderCursor.setOnMouseRelease(button -> {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1)
                this.clicked = false;
        });

        this.sliderCursor.setOnHover(() -> {
            if (this.clicked) {
                startUpdateSliderValue();
            }
        });

        this.sliderCursor.setOnLeave(() -> {
            if (this.clicked) {
                startUpdateSliderValue();
            }
        });

        this.sliderCursor.setOnEnter(() -> {
            if (this.clicked) {
                startUpdateSliderValue();
            }
        });

        this.sliderBase.setOnEnter(() -> {
            if (this.clicked) {
                startUpdateSliderValue(); //TODO: Smooth anim???
            }
        });

        this.sliderBase.setOnLeave(() -> {
            if (this.clicked) {
                startUpdateSliderValue();
            }
        });

        this.sliderBase.setOnMousePress(button -> {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
                startUpdateSliderValue();
            }
        });
    }

    public void disableSliding() {
        this.slidingEnabled = false;
    }

    public void enableSliding() {
        this.slidingEnabled = true;
    }

    public boolean isSlidingEnabled() {
        return this.slidingEnabled;
    }

    protected abstract void updateSliderValue();

    private void startUpdateSliderValue() {
        if (!this.slidingEnabled)
            return;

        updateSliderValue();

        this.position = this.interval.step * Math.round(this.position / this.interval.step);
        if (this.onCursorMoveCallback != null)
            this.onCursorMoveCallback.onCursorMoving(this.position);

        this.value = this.interval.min + this.position * (this.interval.max - this.interval.min);

        if (this.valueText != null)
            this.valueText.getText().setTextString(String.valueOf((int) this.value));

        if (this.onValueChangedCallback != null)
            this.onValueChangedCallback.onValueChanged(this.value);
    }

    public abstract void setValue(float value);

    public float getValue() {
        return this.value;
    }

    public void setOnValueChanged(ValueChangedCallback valueChangedCallback) {
        this.onValueChangedCallback = valueChangedCallback;
    }

    @Override
    public String toString() {
        return "GuiSlider{" +
                "sliderCursor=" + this.sliderCursor +
                ", sliderBase=" + this.sliderBase +
                ", value=" + this.value +
                ", clicked=" + this.clicked +
                "}";
    }

    public void showValue(Side side) {
        Text text = new Text(Integer.toString((int) this.value), .7f, DEFAULT_FONT, Color.BLACK);

        this.valueText = new GuiText(this, text, new GuiConstraintsManager.Builder()
                .setDefault()
                .setWidthConstraint(new RelativeConstraint(.07f, getParent()))
                .setHeightConstraint(new RelativeConstraint(.3f, getParent()))
                .setxConstraint(new StickyConstraint(side, 0, this))
                .create());
    }

    public Interval getInterval() {
        return this.interval;
    }

    public void setInterval(Interval interval) {
        this.interval = interval;
    }

    @FunctionalInterface
    public interface ValueChangedCallback {

        void onValueChanged(float value);
    }

    @FunctionalInterface
    public interface CursorMoveCallback {

        void onCursorMoving(float position);
    }
}
