package guis.presets;

import static guis.Gui.DEFAULT_FONT;

import fontMeshCreator.Text;
import guis.GuiInterface;
import guis.basics.GuiRectangle;
import guis.basics.GuiShape;
import guis.basics.GuiText;
import guis.constraints.GuiConstraintHandler;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RelativeConstraint;
import guis.constraints.Side;
import guis.constraints.StickyConstraint;
import guis.presets.buttons.GuiAbstractButton;
import guis.presets.buttons.GuiRectangleButton;
import inputs.MouseUtils;
import java.awt.Color;

public class GuiSlider extends GuiPreset {

    private final Interval interval;

    GuiShape          outline;
    GuiAbstractButton sliderCursor;
    private GuiRectangleButton sliderBase;
    private GuiText            valueText;

    private boolean clicked;

    private float value;

    private float position;
    private float maxPosition;
    private float minPosition;

    private ValueChangedCallback valueChangedCallback;

    public GuiSlider(GuiInterface parent, Interval interval, Color color, Color colorMovable,
            GuiConstraintsManager constraintsManager) {
        super(parent, Background.NO_BACKGROUND, constraintsManager);

        this.interval = interval;
        this.value = this.interval.defaultValue;

        addBase(color);
        addCursor(colorMovable);

        setupCursor();
    }

    private void addCursor(Color color) {
        GuiConstraintsManager guiConstraintsManager = new GuiConstraintsManager.Builder().setDefault()
                .setWidthConstraint(new RelativeConstraint(.05f))
                .setHeightConstraint(new RelativeConstraint(1)).create();
        this.sliderCursor = new GuiRectangleButton(this, new Background<>(color), guiConstraintsManager);
        this.sliderCursor.setCornerRadius(0);
    }

    private void addOutline() {
        this.outline = new GuiRectangle(this, Background.BLACK_BACKGROUND, new RelativeConstraint(1),
                new RelativeConstraint(1), false);
        this.outline.setOutlineWidth(0.2f);
    }

    public GuiAbstractButton getSliderCursor() {
        return this.sliderCursor;
    }

    public GuiRectangleButton getSliderBase() {
        return this.sliderBase;
    }

    private void addBase(Color unmovableColor) {
        this.sliderBase = new GuiRectangleButton(this, new Background<>(unmovableColor),
                new GuiConstraintsManager.Builder()
                        .setDefault()
                        .setWidthConstraint(new RelativeConstraint(1))
                        .setHeightConstraint(new RelativeConstraint(.85f)).create());
        this.sliderBase.setCornerRadius(0);
    }

    private void setupCursor() {
        this.maxPosition = this.sliderBase.getX() + this.sliderBase.getWidth();
        this.minPosition = this.sliderBase.getX() - this.sliderBase.getWidth();


        this.position = (this.value - this.interval.min) / (this.interval.max - this.interval.min);
        this.position = this.position * this.sliderBase.getWidth() * 2;
        this.position = this.position + this.sliderBase.getX() - this.sliderBase.getWidth();
        this.sliderCursor.setX(this.position);

        this.sliderCursor.setReleaseInsideNeeded(false);
        setListeners();
    }

    private void setListeners() {
        this.sliderCursor.setOnPress(() -> {
            this.clicked = true;
        });

        this.sliderCursor.setOnRelease(() -> {
            this.clicked = false;
        });

        this.sliderCursor.setOnHover(() -> {
            if (!this.clicked)
                return;

            updateSliderValue();
        });

        this.sliderCursor.setOnLeave(() -> {
            if (this.clicked) {
                updateSliderValue();
            }
        });

        this.sliderCursor.setOnEnter(() -> {
            if (this.clicked) {
                updateSliderValue();
            }
        });

        this.sliderBase.setOnEnter(() -> {
            if (this.clicked) {
                updateSliderValue(); //TODO: Smooth anim???
            }
        });

        this.sliderBase.setOnLeave(() -> {
            if (this.clicked) {
                updateSliderValue();
            }
        });

        this.sliderBase.setOnPress(() -> {
            updateSliderValue();
        });
    }

    private void updateSliderValue() {
        float cursorX = MouseUtils.getCursorPos().x;

        cursorX = Float.max(cursorX, this.minPosition);
        cursorX = Float.min(cursorX, this.maxPosition);

        this.sliderCursor.setX(cursorX);

        this.position = cursorX - (this.sliderBase.getX() - this.sliderBase.getWidth());
        this.position = this.position * 1 / this.sliderBase.getWidth() / 2;

        this.position = this.interval.step * Math.round(this.position / this.interval.step);

        this.value = this.interval.min + this.position * (this.interval.max - this.interval.min);
        this.valueText.getText().setTextString(String.valueOf((int) this.value));

        if (this.valueChangedCallback != null)
            this.valueChangedCallback.onValueChanged(this.value);
    }

    public float getValue() {
        return this.value;
    }

    public void setOnValueChanged(ValueChangedCallback valueChangedCallback) {
        this.valueChangedCallback = valueChangedCallback;
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
                .setWidthConstraint(new RelativeConstraint(.1f, getParent()))
                .setHeightConstraint(new RelativeConstraint(.3f, getParent()))
                .setxConstraint(new StickyConstraint(side, this, 0.05f))
                .create());
    }

    public static class Interval {

        float min, max, step;
        float defaultValue;

        public Interval(float defaultValue, float min, float max, float step) {
            this.defaultValue = defaultValue;
            this.min = min;
            this.max = max;
            this.step = step / (this.max - this.min);
        }
    }

    public interface ValueChangedCallback {

        void onValueChanged(float value);
    }
}
