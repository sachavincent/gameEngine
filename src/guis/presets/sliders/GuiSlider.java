package guis.presets.sliders;

import guis.GuiInterface;
import guis.basics.GuiBasics;
import guis.basics.GuiRectangle;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RelativeConstraint;
import guis.presets.GuiPreset;
import java.awt.Color;
import util.MouseUtils;

public abstract class GuiSlider extends GuiPreset {

    GuiBasics sliderCursor;

    private GuiRectangle sliderBaseRightSide;
    private GuiRectangle sliderBaseLeftSide;

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

    GuiSlider(GuiInterface parent, Color color, Color colorMovable, GuiConstraintsManager constraintsManager) {
        super(parent, constraintsManager);

        addBase(color);
        addCursor(colorMovable);

        setupCursor();
    }

    abstract void addCursor(Color color);

//    abstract void addCursor(String texture);

    /**
     * Set the color of the part of the slider base on the left of the cursor
     * @param color - the color of the left part of the base
     */
    public void setColorOfLeftBase(Color color) {
        sliderBaseLeftSide = new GuiRectangle(this, color, new RelativeConstraint(1f, this),
                new RelativeConstraint(.32f, this));
    }//TODO OW like sliders (colored left side of cursor)
//
//    private void addBase(String unmovableTexture) {
//        sliderBase = new GuiRectangle(this, unmovableTexture, new RelativeConstraint(1f, this),
//                new RelativeConstraint(.32f, this));
//    }


    private void addBase(Color unmovableColor) {
        sliderBaseLeftSide = new GuiRectangle(this, unmovableColor, new RelativeConstraint(1f, this),
                new RelativeConstraint(.32f, this));
        
        sliderBaseRightSide = new GuiRectangle(this, unmovableColor, new RelativeConstraint(1f, this),
                new RelativeConstraint(.32f, this));
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

            setSliderValue(sliderCursor, sliderBaseRightSide);
        });

        sliderCursor.setOnLeave(() -> {
            if (clicked) {
                setSliderValue(sliderCursor, sliderBaseRightSide);
            }
        });

        sliderCursor.setOnEnter(() -> {
            if (clicked) {
                setSliderValue(sliderCursor, sliderBaseRightSide);
            }
        });

        sliderBaseRightSide.setOnEnter(() -> {
            if (clicked) {
                setSliderValue(sliderCursor, sliderBaseRightSide); //TODO: Smooth anim???
            }
        });

        sliderBaseRightSide.setOnLeave(() -> {
            if (clicked) {
                setSliderValue(sliderCursor, sliderBaseRightSide);
            }
        });

    }

    private void setSliderValue(GuiBasics sliderCursor, GuiRectangle sliderBase) {
        float cursorX = MouseUtils.getCursorPos().x;

        float maxValue = sliderBase.getX() + sliderBase.getWidth();
        float minValue = sliderBase.getX() - sliderBase.getWidth();

        cursorX = Float.max(cursorX, minValue);
        cursorX = Float.min(cursorX, maxValue);
        sliderCursor.setX(cursorX);

        value = cursorX - (sliderBase.getX() - sliderBase.getWidth());
        value = value * 1 / sliderBase.getWidth() / 2;
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
