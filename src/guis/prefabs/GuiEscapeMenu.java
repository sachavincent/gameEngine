package guis.prefabs;

import display.Display;
import engineTester.Game;
import fontMeshCreator.Text;
import guis.Gui;
import guis.constraints.GuiConstraints;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RelativeConstraint;
import guis.constraints.layout.PatternLayout;
import guis.prefabs.GuiMainMenu.GuiMainMenu;
import guis.prefabs.GuiMainMenu.GuiSettingsMenu;
import guis.presets.Background;
import guis.presets.buttons.GuiRectangleButton;
import java.awt.Color;
import language.Words;
import org.lwjgl.glfw.GLFW;

public class GuiEscapeMenu extends Gui {

    private static final GuiConstraints[] DEFAULT_DIMENSIONS = new GuiConstraints[]{
            new RelativeConstraint(0.2f), new RelativeConstraint(0.9f)};

    private static GuiEscapeMenu instance;

    private final Background<Color> buttonBackground;

    private GuiEscapeMenu() {
        super(new Background<>(new Color(109, 109, 109, 80)));

        this.buttonBackground = new Background<>(new Color(109, 109, 109, 100));

        setConstraints(new GuiConstraintsManager.Builder()
                .setDefault()
                .setWidthConstraint(DEFAULT_DIMENSIONS[0])
                .setHeightConstraint(DEFAULT_DIMENSIONS[1])
                .create());

        setLayout(new PatternLayout(1, 5, 0.15f, 0.1f));

        addResumeButton();
        addSaveAndQuitButton();
        addQuickSaveButton();
        addSettingsButton();
        addQuitButton();

        setDisplayed(false);
    }

    private void addResumeButton() {
        Text text = new Text(Words.RESUME, .7f, DEFAULT_FONT, Color.BLACK);
        GuiRectangleButton resumeButton = new GuiRectangleButton(this, this.buttonBackground, null, text);
        resumeButton.enableFilter();
        resumeButton.setOnMousePress(button -> {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
                setDisplayed(false);
                Game.getInstance().resume();
                return true;
            }
            return false;
        });
    }

    private void addSaveAndQuitButton() {
        Text text = new Text(Words.SAVE_AND_QUIT, .7f, DEFAULT_FONT, Color.BLACK);
        GuiRectangleButton saveAndQuitButton = new GuiRectangleButton(this, this.buttonBackground, null, text);
        saveAndQuitButton.enableFilter();
        saveAndQuitButton.setOnMousePress(button -> {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1) {

            }
            return false;
        });
    }

    private void addQuickSaveButton() {
        Text text = new Text(Words.QUICK_SAVE, .7f, DEFAULT_FONT, Color.BLACK);
        GuiRectangleButton saveAndQuitButton = new GuiRectangleButton(this, this.buttonBackground, null, text);
        saveAndQuitButton.enableFilter();
        saveAndQuitButton.setOnMousePress(button -> {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1) {

            }
            return false;
        });
    }

    private void addSettingsButton() {
        GuiSettingsMenu guiSettingsMenu = new GuiSettingsMenu(GuiMainMenu.getInstance());
        guiSettingsMenu.setDisplayed(false);
        guiSettingsMenu.setOnBackButtonPress(() -> {
            guiSettingsMenu.setDisplayed(false);
            setDisplayed(true);
        });

        Text text = new Text(Words.SETTINGS, .7f, DEFAULT_FONT, Color.BLACK);
        GuiRectangleButton settingsButton = new GuiRectangleButton(this, this.buttonBackground, null, text);
        settingsButton.enableFilter();
        settingsButton.setOnMousePress(button -> {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
                setDisplayed(false);
                guiSettingsMenu.setDisplayed(true);
                return true;
            }
            return false;
        });
    }


    private void addQuitButton() {
        Text text = new Text(Words.QUIT, .7f, DEFAULT_FONT, Color.BLACK);
        GuiRectangleButton quitButton = new GuiRectangleButton(this, this.buttonBackground, null, text);
        quitButton.enableFilter();
        quitButton.setOnMousePress(button -> {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
                quitFunction();
                return true;
            }
            return false;
        });
    }

    public static GuiEscapeMenu getInstance() {
        return instance == null ? (instance = new GuiEscapeMenu()) : instance;
    }

    private void quitFunction() {
        Display.getWindow().close();
    }
}
