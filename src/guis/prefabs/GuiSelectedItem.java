package guis.prefabs;

import display.Display;
import guis.Gui;
import guis.constraints.GuiConstraintHandler;
import guis.constraints.GuiConstraints;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.PixelConstraint;
import guis.presets.Background;
import inputs.MouseUtils;
import java.awt.Color;
import scene.Scene;
import scene.components.IconComponent;
import scene.components.TerrainComponent;
import scene.gameObjects.GameObject;
import scene.gameObjects.Player;
import util.math.Vector2f;

public class GuiSelectedItem extends Gui {

    private static final GuiConstraints[] DEFAULT_DIMENSIONS = new GuiConstraints[]{
            new PixelConstraint(100), new PixelConstraint(100)};

    private static final Background<?> DEFAULT_BACKGROUND = new Background<>(new Color(255, 255, 255));

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
        Scene.getInstance().getGameObjectsForComponent(TerrainComponent.class, false)
                .forEach(gameObject -> gameObject.getComponent(TerrainComponent.class).update());

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
        Scene.getInstance().getGameObjectsForComponent(TerrainComponent.class, false)
                .forEach(gameObject -> gameObject.getComponent(TerrainComponent.class).update());

        Player.removeSelectedGameObject();
        currentSelectedItem = null;

        setDisplayed(false);
    }

    public void updatePosition() {
        Vector2f cursorPos = MouseUtils.getCursorPos();
        GuiConstraintHandler guiConstraintHandler = new GuiConstraintHandler(this);
        int xPixels = (int) (cursorPos.getX() * Display.getWindow().getWidth()) - 70;
        int yPixels = (int) (cursorPos.getY() * Display.getWindow().getHeight()) - 70;
        if (xPixels < -Display.getWindow().getWidth() || yPixels < -Display.getWindow().getHeight()) {
            setDisplayed(false); // GUI is too far left and/or too far low to be rendered fully
        } else {
            setX(guiConstraintHandler.handleXConstraint(new PixelConstraint(xPixels)));
            setY(guiConstraintHandler.handleYConstraint(new PixelConstraint(yPixels)));
        }
    }
}