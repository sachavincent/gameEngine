package guis.presets;

import guis.GuiInterface;
import guis.basics.GuiRectangle;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RelativeConstraint;
import java.awt.Color;
import util.MouseUtils;

public class GuiSlider<E> extends GuiPreset {

    private GuiRectangle sliderCursor;
    private GuiRectangle sliderBase;

    private float   value;
    private boolean clicked;

    public GuiSlider(GuiInterface parent, String texture,
            GuiConstraintsManager constraintsManager) { //TODO: give name to access
        super(parent, constraintsManager);

        addComponents((E) texture, (E) texture);
    }

    public GuiSlider(GuiInterface parent, Color color, Color colorMovable, GuiConstraintsManager constraintsManager) {
        super(parent, constraintsManager);

        addComponents((E) colorMovable, (E) color);
    }

    private void addComponents(E movableTexture, E unmovableTexture) {
        if (unmovableTexture instanceof Color)
            sliderCursor = new GuiRectangle(this, (Color) unmovableTexture, new RelativeConstraint(.08f, this),
                    new RelativeConstraint(1, this));
        else
            sliderCursor = new GuiRectangle(this, (String) unmovableTexture, new RelativeConstraint(.08f, this),
                    new RelativeConstraint(1, this));

        if (movableTexture instanceof Color)
            sliderBase = new GuiRectangle(this, (Color) movableTexture, new RelativeConstraint(1f, this),
                    new RelativeConstraint(.32f, this));
        else
            sliderBase = new GuiRectangle(this, (String) movableTexture, new RelativeConstraint(1f, this),
                    new RelativeConstraint(.32f, this));

        sliderCursor.setX(sliderBase.getX() - sliderBase.getWidth());
        setListeners(sliderCursor, sliderBase);

        this.getBasics().add(sliderBase);
        this.getBasics().add(sliderCursor);
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
}
