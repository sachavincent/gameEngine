package guis.prefabs;

import guis.Gui;
import guis.constraints.GuiConstraintHandler;
import guis.constraints.GuiConstraints;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.PixelConstraint;
import guis.presets.Background;
import inputs.MouseUtils;
import java.awt.Color;
import renderEngine.DisplayManager;
import scene.components.IconComponent;
import scene.gameObjects.GameObject;
import scene.gameObjects.Player;
import util.math.Vector2f;

public class GuiSelectedItem extends Gui {

    private final static GuiConstraints[] DEFAULT_DIMENSIONS = new GuiConstraints[]{
            new PixelConstraint(100), new PixelConstraint(100)};

    private final static Background<?> DEFAULT_BACKGROUND = new Background<>(new Color(255, 255, 255));

    private static GuiSelectedItem instance;

    private Class<? extends GameObject> currentSelectedItem;

    private GuiSelectedItem() {
        super(DEFAULT_BACKGROUND);

        setConstraints(new GuiConstraintsManager.Builder()
                .setDefault()
                .setWidthConstraint(DEFAULT_DIMENSIONS[0])
                .setHeightConstraint(DEFAULT_DIMENSIONS[1])
                .create());

        setDisplayed(false);
    }

    public static GuiSelectedItem getInstance() {
        return instance == null ? (instance = new GuiSelectedItem()) : instance;
    }

    public void setSelectedItem(Class<? extends GameObject> gameObjectClass) {
        this.currentSelectedItem = gameObjectClass;
        GameObject objectFromClass = GameObject.getObjectFromClass(gameObjectClass);
        if (objectFromClass == null)
            return;
        IconComponent iconComponent = objectFromClass.getComponent(IconComponent.class);
        if (iconComponent == null)
            return;
        setBackground(iconComponent.getBackground());
        objectFromClass.destroy();
        Player.setSelectedGameObject(gameObjectClass);

        setDisplayed(true);
    }

    public Class<? extends GameObject> getSelectedItem() {
        return this.currentSelectedItem;
    }

    public void removeSelectedItem() {
        Player.removeSelectedGameObject();
        currentSelectedItem = null;

        setDisplayed(false);
    }

    public void updatePosition() {
        Vector2f cursorPos = MouseUtils.getCursorPos();
        GuiConstraintHandler guiConstraintHandler = new GuiConstraintHandler(this);
        int xPixels = (int) (cursorPos.x * DisplayManager.WIDTH) - 70;
        int yPixels = (int) (cursorPos.y * DisplayManager.HEIGHT) - 70;
        if (xPixels < -DisplayManager.WIDTH || yPixels < -DisplayManager.HEIGHT) {
            setDisplayed(false); // GUI is too far left and/or too far low to be rendered fully
        } else {
            setX(guiConstraintHandler.handleXConstraint(new PixelConstraint(xPixels)));
            setY(guiConstraintHandler.handleYConstraint(new PixelConstraint(yPixels)));
        }
    }
}
