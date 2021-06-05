package guis.presets.sliders;

import guis.GuiInterface;
import guis.constraints.GuiConstraintHandler;
import guis.constraints.GuiConstraints;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.PixelConstraint;
import inputs.MouseUtils;
import java.awt.Color;
import renderEngine.DisplayManager;
import util.math.Maths;

public class GuiHorizontalSlider extends GuiSlider {

    public GuiHorizontalSlider(GuiInterface parent, Interval interval, Color color,
            Color colorMovable,
            GuiConstraintsManager constraintsManager) {
        super(parent, interval, color, colorMovable, constraintsManager);
    }

    @Override
    public void setValue(float value) {
        value = Maths.clamp(value, this.interval.min, this.interval.max);

        this.maxPosition = this.sliderBase.getX() + this.sliderBase.getWidth();
        this.minPosition = this.sliderBase.getX() - this.sliderBase.getWidth();

        this.value = value;
        this.position = (this.value - this.interval.min) / (this.interval.max - this.interval.min);

        if (this.alignCursorWithBase) {
            this.maxPosition -= this.sliderCursor.getWidth();
            this.minPosition += this.sliderCursor.getWidth();

            this.position = this.position * (this.sliderBase.getWidth() * 2 - this.sliderCursor.getWidth() * 2);
            this.position =
                    this.position + this.sliderBase.getX() - this.sliderBase.getWidth() + this.sliderCursor.getWidth();
        } else {
            this.position = this.position * this.sliderBase.getWidth() * 2;
            this.position = this.position + this.sliderBase.getX() - this.sliderBase.getWidth();
        }

        this.sliderCursor.setX(this.position);

        if (this.onValueChangedCallback != null)
            this.onValueChangedCallback.onValueChanged(this.value);
    }

    @Override
    protected GuiConstraintsManager getCursorConstraints() {
        return new GuiConstraintsManager.Builder()
                .setDefault()
                .setWidthConstraint(new PixelConstraint(8))
                .setHeightConstraint(new PixelConstraint(
                        (int) (Math.ceil(this.sliderBase.getHeight() * DisplayManager.HEIGHT)) + 10))
                .create();
    }

    /**
     * Width should probably be set in pixels
     */
    public void setCursorWidth(GuiConstraints widthConstraints) {
        GuiConstraintHandler handler = new GuiConstraintHandler(this, this.sliderCursor);
        this.sliderCursor.setWidth(handler.handleWidthConstraint(widthConstraints));
        setValue(this.value);
    }

    @Override
    protected void updateSliderValue() {
        if (!this.slidingEnabled)
            return;

        float cursorX = MouseUtils.getCursorPos().x;

        cursorX = Float.max(cursorX, this.minPosition);
        cursorX = Float.min(cursorX, this.maxPosition);

        this.sliderCursor.setX(cursorX);

        if (this.alignCursorWithBase) {
            this.position =
                    cursorX - this.sliderBase.getX() + this.sliderBase.getWidth() - this.sliderCursor.getWidth();
            this.position = this.position * (1 / (this.sliderBase.getWidth() * 2 - this.sliderCursor.getWidth() * 2));
        } else {
            this.position = cursorX - this.sliderBase.getX() + this.sliderBase.getWidth();
            this.position = this.position / this.sliderBase.getWidth() / 2;
        }
    }
}
