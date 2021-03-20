package guis.prefabs;

import engineTester.Game;
import fontMeshCreator.Text;
import guis.Gui;
import guis.constraints.GuiConstraints;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.PatternGlobalConstraint;
import guis.constraints.RelativeConstraint;
import guis.prefabs.GuiMainMenu.GuiMainMenu;
import guis.prefabs.GuiMainMenu.GuiSettingsMenu;
import guis.presets.Background;
import guis.presets.buttons.GuiRectangleButton;
import java.awt.Color;
import renderEngine.DisplayManager;

public class GuiEscapeMenu extends Gui {

    private final static GuiConstraints[] DEFAULT_DIMENSIONS = new GuiConstraints[]{
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

        setChildrenConstraints(new PatternGlobalConstraint(1, 5, 0.15f, 0.1f));

        addResumeButton();
        addSaveAndQuitButton();
        addQuickSaveButton();
        addSettingsButton();
        addQuitButton();
    }

    private void addResumeButton() {
        Text text = new Text("Resume", .7f, DEFAULT_FONT, new Color(72, 72, 72));
        GuiRectangleButton resumeButton = new GuiRectangleButton(this, this.buttonBackground, text);
        resumeButton.enableFilter();
        resumeButton.setOnPress(() -> {
            Gui.hideGui(this);
            Game.getInstance().resume();
        });
    }

    private void addSaveAndQuitButton() {
        Text text = new Text("Save & Quit", .7f, DEFAULT_FONT, new Color(72, 72, 72));
        GuiRectangleButton saveAndQuitButton = new GuiRectangleButton(this, this.buttonBackground, text);
        saveAndQuitButton.enableFilter();
        saveAndQuitButton.setOnPress(() -> {

        });
    }

    private void addQuickSaveButton() {
        Text text = new Text("Quick Save", .7f, DEFAULT_FONT, new Color(72, 72, 72));
        GuiRectangleButton saveAndQuitButton = new GuiRectangleButton(this, this.buttonBackground, text);
        saveAndQuitButton.enableFilter();
        saveAndQuitButton.setOnPress(() -> {

        });
    }

    private void addSettingsButton() {
        GuiSettingsMenu guiSettingsMenu = new GuiSettingsMenu(GuiMainMenu.getInstance());
        guiSettingsMenu.onBackButtonPress(() -> {
            guiSettingsMenu.setDisplayed(false);
            Gui.showGui(this);
        });

        Text text = new Text("Settings", .7f, DEFAULT_FONT, new Color(72, 72, 72));
        GuiRectangleButton settingsButton = new GuiRectangleButton(this, this.buttonBackground, text);
        settingsButton.enableFilter();
        settingsButton.setOnPress(() -> {
            Gui.hideGui(this);
            guiSettingsMenu.setDisplayed(true);
        });
    }


    private void addQuitButton() {
        Text text = new Text("Quit", .7f, DEFAULT_FONT, new Color(72, 72, 72));
        GuiRectangleButton quitButton = new GuiRectangleButton(this, this.buttonBackground, text);
        quitButton.enableFilter();
        quitButton.setOnPress(() -> {
            quitFunction();
        });
    }

    public static GuiEscapeMenu getInstance() {
        return instance == null ? (instance = new GuiEscapeMenu()) : instance;
    }

    private void quitFunction() {
        DisplayManager.closeDisplay();
    }
}
