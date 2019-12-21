package guis.presets.sliders;

import guis.GuiInterface;
import guis.basics.GuiRectangle;
import guis.basics.GuiShape;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RelativeConstraint;
import guis.presets.GuiBackground;
import guis.presets.GuiPreset;
import java.awt.Color;
import util.MouseUtils;

public abstract class GuiAbstractSlider extends GuiPreset {

    GuiShape sliderCursor;

    private GuiShape sliderBaseRightSide;
    private GuiShape sliderBaseLeftSide;

    private float   value;
    private boolean clicked;

//    GuiSlider(GuiInterface parent, String texture, GuiConstraintsManager constraintsManager) {
//        super(parent, constraintsManager);
//
//        addBase(texture);
//        addCursor(texture);
//
//        setupCursor();
//    }

    GuiAbstractSlider(GuiInterface parent, Color color, Color colorMovable, GuiConstraintsManager constraintsManager) {
        super(parent, constraintsManager);

        addBase(color);
        addCursor(colorMovable);

        setupCursor();
    }

    abstract void addCursor(Color color);

//    abstract void addCursor(String texture);

    /**
     * Set the color of the part of the slider base on the left of the cursor
     *
     * @param color - the color of the left part of the base
     */
    public void setColorOfLeftBase(Color color) {
        removeBasic(sliderBaseLeftSide);

        sliderBaseLeftSide = new GuiRectangle(this, new GuiBackground<>(color), new RelativeConstraint(value, this),
                new RelativeConstraint(.32f, this));

        addBasic(sliderBaseLeftSide);

        removeBasic(sliderCursor);
        addBasic(sliderCursor);
    }


    private void addBase(Color unmovableColor) {
        sliderBaseLeftSide = new GuiRectangle(this, new GuiBackground<>(unmovableColor), new RelativeConstraint(0, this),
                new RelativeConstraint(.32f, this));

        sliderBaseRightSide = new GuiRectangle(this, new GuiBackground<>(unmovableColor),
                new RelativeConstraint(1f, this),
                new RelativeConstraint(.32f, this));


        sliderBaseLeftSide.setX(sliderBaseRightSide.getX() - sliderBaseRightSide.getWidth());
    }

    private void setupCursor() {
        sliderCursor.setX(sliderBaseRightSide.getX() - sliderBaseRightSide.getWidth());

        setListeners();

        addBasic(sliderBaseRightSide);
        addBasic(sliderBaseLeftSide);
        addBasic(sliderCursor);
    }


    private void setListeners() {
        sliderCursor.setOnClick(() -> {
            clicked = true;
        });

        sliderCursor.setOnRelease(() -> {
            clicked = false;
        });

        sliderCursor.setOnHover(() -> {
            if (!clicked)
                return;

            setSliderValue(sliderCursor, sliderBaseLeftSide, sliderBaseRightSide);
        });

        sliderCursor.setOnLeave(() -> {
            if (clicked) {
                setSliderValue(sliderCursor, sliderBaseLeftSide, sliderBaseRightSide);
            }
        });

        sliderCursor.setOnEnter(() -> {
            if (clicked) {
                setSliderValue(sliderCursor, sliderBaseLeftSide, sliderBaseRightSide);
            }
        });

        sliderBaseRightSide.setOnEnter(() -> {
            if (clicked) {
                setSliderValue(sliderCursor, sliderBaseLeftSide, sliderBaseRightSide); //TODO: Smooth anim???
            }
        });

        sliderBaseRightSide.setOnLeave(() -> {
            if (clicked) {
                setSliderValue(sliderCursor, sliderBaseLeftSide, sliderBaseRightSide);
            }
        });

    }

    private void setSliderValue(GuiShape sliderCursor, GuiShape sliderBaseLeftSide, GuiShape sliderBaseRightSide) {
        float cursorX = MouseUtils.getCursorPos().x;

        float maxValue = sliderBaseRightSide.getX() + sliderBaseRightSide.getWidth();
        float minValue = sliderBaseRightSide.getX() - sliderBaseRightSide.getWidth();

        cursorX = Float.max(cursorX, minValue);
        cursorX = Float.min(cursorX, maxValue);

        sliderCursor.setX(cursorX);

        value = cursorX - (sliderBaseRightSide.getX() - sliderBaseRightSide.getWidth());
        value = value * 1 / sliderBaseRightSide.getWidth() / 2;

        sliderBaseLeftSide.setWidthConstraint(new RelativeConstraint(value));
        sliderBaseLeftSide.setX(cursorX - sliderBaseLeftSide.getWidth());

        this.sliderBaseLeftSide = sliderBaseLeftSide;
    }

    public float getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return "GuiSlider{" +
                "sliderCursor=" + sliderCursor +
                ", sliderBase=" + sliderBaseRightSide +
                ", value=" + value +
                ", clicked=" + clicked +
                "}";
    }
}
