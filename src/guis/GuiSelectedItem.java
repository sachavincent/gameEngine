package guis;

import abstractItem.AbstractItem;
import guis.constraints.GuiConstraints;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.PixelConstraint;
import guis.presets.GuiBackground;
import inputs.MouseUtils;
import java.awt.Color;
import renderEngine.DisplayManager;
import util.math.Vector2f;

public class GuiSelectedItem extends Gui {

    private final static GuiConstraints[] DEFAULT_DIMENSIONS = new GuiConstraints[]{
            new PixelConstraint(100), new PixelConstraint(100)};

    private final static GuiBackground<?> DEFAULT_BACKGROUND = new GuiBackground<>(new Color(255, 255, 255));

    private static GuiSelectedItem instance;

    private AbstractItem currentSelectedItem;

    private GuiSelectedItem() {
        super(DEFAULT_BACKGROUND);

        GuiConstraintsManager menuConstraints = new GuiConstraintsManager.Builder()
                .setDefault()
                .setWidthConstraint(DEFAULT_DIMENSIONS[0])
                .setHeightConstraint(DEFAULT_DIMENSIONS[1])
                .create();

        setConstraints(menuConstraints);
    }

    public void setSelectedItem(AbstractItem abstractItem) {
        currentSelectedItem = abstractItem;
        setBackground(currentSelectedItem.getBackground());

        if (!isDisplayed())
            showGui(this);
    }

    public AbstractItem getSelectedItem() {
        return this.currentSelectedItem;
    }

    public void removeSelectedItem() {
        currentSelectedItem = null;
        setBackground(DEFAULT_BACKGROUND);

        hideGui(this);
    }

    public static GuiSelectedItem getSelectedItemGui() {
        return instance;
    }

    public void updatePosition() {
        Vector2f cursorPos = MouseUtils.getCursorPos();
        handleXConstraint(new PixelConstraint((int) (cursorPos.x * DisplayManager.WIDTH) - 70));
        handleYConstraint(new PixelConstraint((int) (cursorPos.y * DisplayManager.HEIGHT) - 70));
    }


    public static class Builder {

        private GuiSelectedItem guiSelectedItem;

        public Builder() {
            guiSelectedItem = new GuiSelectedItem();
        }

        public Builder setBackground(GuiBackground<?> guiBackground) {
            guiSelectedItem.setBackground(guiBackground);

            return this;
        }

        public Builder setConstraints(GuiConstraintsManager constraintsManager) {
            guiSelectedItem.setConstraints(constraintsManager);

            return this;
        }

        public GuiSelectedItem create() {
            return (instance = guiSelectedItem);
        }
    }
}
