package guis.presets;

import guis.GuiInterface;
import guis.basics.GuiRectangle;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RelativeConstraint;
import java.awt.Color;
import util.MouseUtils;

public class GuiSlider extends GuiPreset {

    private GuiRectangle sliderCursor;
    private GuiRectangle sliderBase;

    private float   value;
    private boolean clicked;

    public GuiSlider(GuiInterface parent, String texture,
            GuiConstraintsManager constraintsManager) { //TODO: give name to access
        super(parent, constraintsManager);

        addComponents(texture, texture);
    }

    public GuiSlider(GuiInterface parent, Color color, Color colorMovable, GuiConstraintsManager constraintsManager) {
        super(parent, constraintsManager);

        addComponents(colorMovable, color);
    }

    private void addComponents(String movableTexture, String unmovableTexture) {

        sliderCursor = new GuiRectangle(this, unmovableTexture, new RelativeConstraint(.08f, this),
                new RelativeConstraint(1, this));

        sliderBase = new GuiRectangle(this, movableTexture, new RelativeConstraint(1f, this),
                new RelativeConstraint(.32f, this));

        setupCursor();
    }


    private void addComponents(Color movableTexture, Color unmovableTexture) {
        sliderCursor = new GuiRectangle(this, unmovableTexture, new RelativeConstraint(.08f, this),
                new RelativeConstraint(1, this));

        sliderBase = new GuiRectangle(this, movableTexture, new RelativeConstraint(1f, this),
                new RelativeConstraint(.32f, this));

        setupCursor();
    }

    private void setupCursor() {
        sliderCursor.setX(sliderBase.getX() - sliderBase.getWidth());

        setListeners(sliderCursor, sliderBase);

        addBasic(sliderBase);
        addBasic(sliderCursor);
    }


    private void setListeners(GuiRectangle movable, GuiRectangle unmovable) {
        movable.setOnClick(() -> {
            clicked = true;
        });

        movable.setOnRelease(() -> {
            clicked = false;
        });

        movable.setOnHover(() -> {
            if (!clicked)
                return;

            setSliderValue(movable, unmovable);
        });

        movable.setOnLeave(() -> {
            if (clicked) {
                setSliderValue(movable, unmovable);
            }
        });

        movable.setOnEnter(() -> {
            if (clicked) {
                setSliderValue(movable, unmovable);
            }
        });

        unmovable.setOnEnter(() -> {
            if (clicked) {
                setSliderValue(movable, unmovable); //TODO: Smooth anim???
            }
        });

        unmovable.setOnLeave(() -> {
            if (clicked) {
                setSliderValue(movable, unmovable);
            }
        });

    }

    private void setSliderValue(GuiRectangle sliderCursor, GuiRectangle sliderBase) {
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
                ", sliderBase=" + sliderBase +
                ", value=" + value +
                ", clicked=" + clicked +
                '}';
    }
}
