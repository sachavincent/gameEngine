package guis.prefabs;

import fontMeshCreator.FontType;
import fontMeshCreator.Text;
import guis.Gui;
import guis.constraints.*;
import guis.presets.Background;
import guis.presets.buttons.GuiRectangleButton;
import inputs.MouseUtils;
import items.GameObjectPreviews;
import java.awt.Color;
import java.io.File;
import language.Words;
import org.lwjgl.glfw.GLFW;
import scene.gameObjects.DirtRoad;
import scene.gameObjects.GameObject;
import scene.gameObjects.Insula;
import scene.gameObjects.Market;
import textures.FontTexture;

public class GuiItemSelection extends Gui {

    private final static FontType DEFAULT_FONT = new FontType(
            new FontTexture("roboto.png").getTextureID(), new File("res/roboto.fnt")); //TODO System-wide font

    private final static GuiConstraints[] DEFAULT_COORDINATES = new GuiConstraints[]{
            new SideConstraint(Side.RIGHT), new CenterConstraint()};

    private final static GuiConstraints[] DEFAULT_DIMENSIONS = new GuiConstraints[]{
            new RelativeConstraint(0.2f), new RelativeConstraint(0.25f)};

    private static GuiItemSelection instance;

    public static GuiItemSelection getInstance() {
        return instance == null ? (instance = new GuiItemSelection()) : instance;
    }

    private GuiItemSelection() {
        super(new Background<>(new Color(109, 109, 109, 80)));

        GuiConstraintsManager menuConstraints = new GuiConstraintsManager.Builder()
                .setWidthConstraint(DEFAULT_DIMENSIONS[0])
                .setHeightConstraint(DEFAULT_DIMENSIONS[1])
                .setxConstraint(DEFAULT_COORDINATES[0])
                .setyConstraint(DEFAULT_COORDINATES[1])
                .create();

        setConstraints(menuConstraints);

        setLayout(new PatternGlobalConstraint(5, 3, .02f));

        createDirtRoadButton();
        createInsulaButton();
        createMarketButton();

        setDisplayed(false);
    }

    private void createMarketButton() {
        Text text = new Text(Words.MARKET, .55f, DEFAULT_FONT, Color.DARK_GRAY);
        GuiRectangleButton marketButton = new GuiRectangleButton(this, GameObjectPreviews.MARKET, null, null, text);
        marketButton.setOnMousePress(button -> {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
                System.out.println("Market selected");

                selectOrUnselect(Market.class, MouseUtils::SelectBuilding);
            }
        });
    }

    private void createInsulaButton() {
        Text text = new Text(Words.INSULA, .55f, DEFAULT_FONT, Color.DARK_GRAY);
        GuiRectangleButton insulaButton = new GuiRectangleButton(this, GameObjectPreviews.INSULA, null, null, text);
        insulaButton.enableFilter();
        insulaButton.setOnMousePress(button -> {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
                System.out.println("Insula selected");


                selectOrUnselect(Insula.class, MouseUtils::SelectBuilding);
            }
        });
    }

    private void createDirtRoadButton() {
        Text text = new Text(Words.DIRT_ROAD, .55f, DEFAULT_FONT, Color.DARK_GRAY);
        GuiRectangleButton dirtRoadBtn = new GuiRectangleButton(this, GameObjectPreviews.DIRT_ROAD, null, null, text);
        dirtRoadBtn.enableFilter();

        dirtRoadBtn.setOnMousePress(button -> {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
                System.out.println("DirtRoad selected");

                selectOrUnselect(DirtRoad.class, MouseUtils::SelectRoad);
            }
        });
    }

    private void selectOrUnselect(Class<? extends GameObject> gameObjectClass, IfSelectedCallback ifSelectedCallback) {
        GuiSelectedItem selectedItemGui = GuiSelectedItem.getInstance();
        Class<? extends GameObject> selectedItemClass = selectedItemGui.getSelectedItem();

        boolean select = selectedItemClass == null || selectedItemClass != gameObjectClass;

        if (select) {
            ifSelectedCallback.onSelection();
            selectedItemGui.updatePosition();
            selectedItemGui.setSelectedItem(gameObjectClass);
        } else {
            selectedItemGui.removeSelectedItem();

            MouseUtils.Deselect();
        }
    }

    public static GuiItemSelection getItemSelectionGui() {
        return instance;
    }

    @FunctionalInterface
    interface IfSelectedCallback {

        void onSelection();
    }
}
