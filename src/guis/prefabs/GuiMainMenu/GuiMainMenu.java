package guis.prefabs.GuiMainMenu;

import engineTester.Game;
import fontMeshCreator.Text;
import guis.Gui;
import guis.basics.GuiRectangle;
import guis.constraints.CenterConstraint;
import guis.constraints.GuiConstraints;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RelativeConstraint;
import guis.constraints.Side;
import guis.constraints.SideConstraint;
import guis.constraints.layout.PatternLayout;
import guis.presets.Background;
import guis.presets.buttons.GuiRectangleButton;
import java.awt.Color;
import language.Words;
import org.lwjgl.glfw.GLFW;
import renderEngine.DisplayManager;

public class GuiMainMenu extends Gui {

    private final static GuiConstraints[] DEFAULT_COORDINATES = new GuiConstraints[]{
            new SideConstraint(Side.LEFT), new SideConstraint(Side.TOP)};

    private final static GuiConstraints[] DEFAULT_DIMENSIONS = new GuiConstraints[]{
            new RelativeConstraint(0.4f), new RelativeConstraint(.5f)};

    private static GuiMainMenu instance;

    private final GuiRectangle    menuButtonPanel;
    private final GuiSettingsMenu settingsMenu;
    private       State           state;
    public        Text            textSettings;

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
        this.menuButtonPanel.setLayout(new PatternLayout(1, 4, 0, 0.07f));

        createNewGameButton();
        createLoadGameButton();
        createSettingsButton();
        createQuitButton();

        this.settingsMenu = new GuiSettingsMenu(this);

        this.settingsMenu.setOnBackButtonPress(this::back);

        formLoad();
    }

    private void formLoad() {
        this.state = State.MAIN;

        this.settingsMenu.setDisplayed(false);
    }

    private void createNewGameButton() {
        Text text = new Text(Words.NEW_GAME, 1.2f, DEFAULT_FONT, Color.BLACK);
        GuiRectangleButton newGameButton = new GuiRectangleButton(this.menuButtonPanel,
                new Background<>(new Color(109, 109, 109, 100)), Color.BLACK, text);
        newGameButton.enableFilter();
        newGameButton.setOnMousePress(button -> {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
                setDisplayed(false);
                Game.getInstance().start();
                return true;
            }
            return false;
        });
    }

    private void createLoadGameButton() {
        Text text = new Text(Words.LOAD_GAME, 1.2f, DEFAULT_FONT, Color.BLACK);
        GuiRectangleButton loadGameButton = new GuiRectangleButton(this.menuButtonPanel,
                new Background<>(new Color(109, 109, 109, 100)), Color.BLACK, text);
        loadGameButton.enableFilter();
        loadGameButton.setOnMousePress(button -> {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1) {

            }
            return false;
        });
    }

    private void createSettingsButton() {
        textSettings = new Text(Words.SETTINGS, 1.2f, DEFAULT_FONT, Color.BLACK);

        GuiRectangleButton settingsButton = new GuiRectangleButton(this.menuButtonPanel,
                new Background<>(new Color(109, 109, 109, 100)), Color.BLACK, textSettings);
        settingsButton.enableFilter();
        settingsButton.setOnMousePress(button -> {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
                setDisplayed(false);
                showSettings();
                return true;
            }
            return false;
        });
    }

    public void showSettings() {
        state = State.SETTINGS;
        this.settingsMenu.setDisplayed(true);
    }

    private void createQuitButton() {
        Text text = new Text(Words.QUIT, 1.2f, DEFAULT_FONT, Color.BLACK);
        GuiRectangleButton quitButton = new GuiRectangleButton(this.menuButtonPanel,
                new Background<>(new Color(109, 109, 109, 100)), Color.BLACK, text);
        quitButton.enableFilter();
        quitButton.setOnMousePress(button -> {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
                DisplayManager.closeDisplay();
                return true;
            }
            return false;
        });
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
                this.settingsMenu.setDisplayed(false);
                setDisplayed(true);
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