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

public class GuiVerticalSlider extends GuiSlider {

    public GuiVerticalSlider(GuiInterface parent, Interval interval, Color color,
            Color colorMovable,
            GuiConstraintsManager constraintsManager) {
        super(parent, interval, color, colorMovable, constraintsManager);
    }

    @Override
    public void setValue(float value) {
        value = Maths.clamp(value, -this.interval.max, -this.interval.min);

        this.minPosition = this.sliderBase.getY() + this.sliderBase.getHeight();
        this.maxPosition = this.sliderBase.getY() - this.sliderBase.getHeight();

        this.value = value;
        this.position = (this.value - this.interval.min) / (this.interval.max - this.interval.min);

        if (this.alignCursorWithBase) {
            this.maxPosition += this.sliderCursor.getHeight();
            this.minPosition -= this.sliderCursor.getHeight();

            this.position = this.position * (this.sliderBase.getHeight() * 2 - this.sliderCursor.getHeight() * 2);
            this.position =
                    this.position + (this.sliderBase.getY() + this.sliderBase.getHeight() -
                            this.sliderCursor.getHeight());
        } else {
            this.position = this.position * this.sliderBase.getHeight() * 2;
            this.position = this.position + this.sliderBase.getY() + this.sliderBase.getHeight();
        }

        this.sliderCursor.setY(this.position);

        if (this.onValueChangedCallback != null)
            this.onValueChangedCallback.onValueChanged(this.value);
    }

    @Override
    protected GuiConstraintsManager getCursorConstraints() {
        return new GuiConstraintsManager.Builder()
                .setDefault()
                .setWidthConstraint(new PixelConstraint(
                        (int) (Math.ceil(this.sliderBase.getWidth() * DisplayManager.WIDTH)) + 10))
                .setHeightConstraint(new PixelConstraint(8))
                .create();
    }

    /**
     * Height should probably be set in pixels
     */
    public void setCursorHeight(GuiConstraints heightConstraint) {
        GuiConstraintHandler handler = new GuiConstraintHandler(this, this.sliderCursor);
        this.sliderCursor.setHeight(handler.handleHeightConstraint(heightConstraint));
        setValue(this.value);
    }

    @Override
    protected void updateSliderValue() {
        if (!this.slidingEnabled)
            return;

        float cursorY = MouseUtils.getCursorPos().y;
        cursorY = Float.max(cursorY, this.maxPosition);
        cursorY = Float.min(cursorY, this.minPosition);

        this.sliderCursor.setY(cursorY);

        if (this.alignCursorWithBase) {
            this.position =
                    cursorY - (this.sliderBase.getY() + this.sliderBase.getHeight() - this.sliderCursor.getHeight());
            this.position = this.position * (1 / (this.sliderBase.getHeight() * 2 - this.sliderCursor.getHeight() * 2));
        } else {
            this.position = cursorY - this.sliderBase.getY() - this.sliderBase.getHeight();
            this.position = this.position * (1 / (this.sliderBase.getHeight() * 2));
        }
    }
}
