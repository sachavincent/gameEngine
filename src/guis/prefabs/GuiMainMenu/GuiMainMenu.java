package guis.prefabs.GuiMainMenu;

import engineTester.Game;
import fontMeshCreator.FontType;
import fontMeshCreator.Text;
import guis.Gui;
import guis.basics.GuiRectangle;
import guis.constraints.*;
import guis.presets.Background;
import guis.presets.buttons.GuiRectangleButton;
import java.awt.Color;
import java.io.File;
import renderEngine.DisplayManager;
import textures.FontTexture;

public class GuiMainMenu extends Gui {

    private final static FontType DEFAULT_FONT = new FontType(
            new FontTexture("roboto.png").getTextureID(), new File("res/roboto.fnt")); //TODO System-wide font

    private final static GuiConstraints[] DEFAULT_COORDINATES = new GuiConstraints[]{
            new SideConstraint(Side.LEFT), new SideConstraint(Side.TOP)};

    private final static GuiConstraints[] DEFAULT_DIMENSIONS = new GuiConstraints[]{
            new RelativeConstraint(0.4f), new RelativeConstraint(.5f)};

    private static GuiMainMenu instance;

    private final GuiRectangle    menuButtonPanel;
    private final GuiSettingsMenu settingsMenu;
    private       State           state;

    private GuiMainMenu() {
        super(Background.NO_BACKGROUND);

        GuiConstraintsManager menuConstraints = new GuiConstraintsManager.Builder()
                .setWidthConstraint(DEFAULT_DIMENSIONS[0])
                .setHeightConstraint(DEFAULT_DIMENSIONS[1])
                .setxConstraint(DEFAULT_COORDINATES[0])
                .setyConstraint(DEFAULT_COORDINATES[1])
                .create();
        setConstraints(menuConstraints);

        setCornerRadius(0);

        this.menuButtonPanel = new GuiRectangle(this, Background.NO_BACKGROUND, new GuiConstraintsManager.Builder()
                .setWidthConstraint(new RelativeConstraint(.93f))
                .setHeightConstraint(new RelativeConstraint(1))
                .setxConstraint(new CenterConstraint())
                .setyConstraint(new CenterConstraint())
                .create());
        this.menuButtonPanel.setChildrenConstraints(new PatternGlobalConstraint(1, 4, 0, 0.07f));

        createNewGameButton();
        createLoadGameButton();
        createSettingsButton();
        createQuitButton();

        this.settingsMenu = new GuiSettingsMenu(this);
        this.settingsMenu.onBackButtonPress(this::back);

        formLoad();
    }

    private void formLoad() {
        this.state = State.MAIN;

        this.settingsMenu.setDisplayed(false);
    }

    private void createNewGameButton() {
        Text text = new Text("New Game", .7f, DEFAULT_FONT, Color.BLACK);
        GuiRectangleButton newGameButton = new GuiRectangleButton(this.menuButtonPanel,
                new Background<>(new Color(109, 109, 109, 100)), text);
        newGameButton.enableFilter();
        newGameButton.setOnPress(() -> {
            Gui.hideGui(this);
            Game.getInstance().start();
        });
    }

    private void createLoadGameButton() {
        Text text = new Text("Load Game", .7f, DEFAULT_FONT, Color.BLACK);
        GuiRectangleButton loadGameButton = new GuiRectangleButton(this.menuButtonPanel,
                new Background<>(new Color(109, 109, 109, 100)), text);
        loadGameButton.enableFilter();
        loadGameButton.setOnPress(() -> {

        });
    }

    private void createSettingsButton() {
        Text text = new Text("Settings", .7f, DEFAULT_FONT, Color.BLACK);
        GuiRectangleButton settingsButton = new GuiRectangleButton(this.menuButtonPanel,
                new Background<>(new Color(109, 109, 109, 100)), text);
        settingsButton.enableFilter();
        settingsButton.setOnPress(() -> {
            Gui.hideGui(this);
            showSettings();
        });
    }

    public void showSettings() {
        state = State.SETTINGS;
        Gui.showGui(this.settingsMenu);
    }

    private void createQuitButton() {
        Text text = new Text("Quit", .7f, DEFAULT_FONT, Color.BLACK);
        GuiRectangleButton quitButton = new GuiRectangleButton(this.menuButtonPanel,
                new Background<>(new Color(109, 109, 109, 100)), text);
        quitButton.enableFilter();
        quitButton.setOnPress(DisplayManager::closeDisplay);
    }

    public static GuiMainMenu getInstance() {
        return instance == null ? (instance = new GuiMainMenu()) : instance;
    }

    /**
     * Goes back in the menu tree until you're at the main menu
     */
    public void back() {
        switch (state) {
            case MAIN:
                // null
                break;
            case SETTINGS:
                Gui.hideGui(this.settingsMenu);
                Gui.showGui(this);
                state = State.MAIN;
                break;
            default:
                break;
        }
    }

    enum State {
        MAIN,
        SETTINGS;
    }
}