package guis.prefabs;

import guis.Gui;
import guis.constraints.GuiConstraintHandler;
import guis.constraints.GuiConstraints;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.PixelConstraint;
import guis.presets.Background;
import inputs.MouseUtils;
import items.abstractItem.AbstractItem;
import java.awt.Color;
import renderEngine.DisplayManager;
import terrains.Terrain;
import util.math.Vector2f;

public class GuiSelectedItem extends Gui {

    private final static GuiConstraints[] DEFAULT_DIMENSIONS = new GuiConstraints[]{
            new PixelConstraint(100), new PixelConstraint(100)};

    private final static Background<?> DEFAULT_BACKGROUND = new Background<>(new Color(255, 255, 255));

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

        Terrain.getInstance().setPreviewedItem(abstractItem);

        if (!isDisplayed())
            showGui(this);
    }

    public AbstractItem getSelectedItem() {
        return this.currentSelectedItem;
    }

    public void removeSelectedItem() {
        Terrain.getInstance().setPreviewedItem(null);
        currentSelectedItem = null;
        setBackground(DEFAULT_BACKGROUND);

        hideGui(this);
    }

    public static GuiSelectedItem getSelectedItemGui() {
        return instance;
    }

    public void updatePosition() {
        Vector2f cursorPos = MouseUtils.getCursorPos();

        GuiConstraintHandler guiConstraintHandler = new GuiConstraintHandler(this);
        setX(guiConstraintHandler
                .handleXConstraint(new PixelConstraint(Math.max(0, (int) (cursorPos.x * DisplayManager.WIDTH) - 70))));
        setY(guiConstraintHandler
                .handleYConstraint(new PixelConstraint(Math.max(0, (int) (cursorPos.y * DisplayManager.HEIGHT) - 70))));
    }

    public static class Builder {

        private final GuiSelectedItem guiSelectedItem;

        public Builder() {
            guiSelectedItem = new GuiSelectedItem();
        }

        public Builder setBackground(Background<?> background) {
            guiSelectedItem.setBackground(background);

            return this;
        }

        public Builder setConstraints(GuiConstraintsManager constraintsManager) {
            guiSelectedItem.setConstraints(constraintsManager);

            return this;
        }

        public GuiSelectedItem create() {
            instance = guiSelectedItem;

            Gui.hideGui(instance);
            return instance;
        }
    }
}
