package guis.prefabs.GuiMainMenu;

import fontMeshCreator.Text;
import guis.basics.GuiRectangle;
import guis.basics.GuiText;
import guis.constraints.*;
import guis.presets.Background;
import guis.presets.GuiMultiOption;
import guis.presets.GuiOnOffOption;
import guis.presets.GuiOnOffOption.OnOff;
import guis.presets.GuiSlider;
import guis.presets.GuiSlider.Interval;
import guis.presets.GuiTextInput;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import language.Words;
import renderEngine.DisplayManager;
import renderEngine.DisplayManager.DisplayMode;
import renderEngine.DisplayManager.Resolution;
import util.Utils;

public class GuiDisplaySettings extends GuiTab {

    public final static Color LIGHT_GRAY = Color.decode("#BDBDBD");

    public       GuiSlider                  fpsSlider;
    private      GuiMultiOption<Resolution> resolutionOptions;

    public GuiDisplaySettings(GuiMultiTab parent) {
        super(Background.NO_BACKGROUND, parent, Words.DISPLAY);

        setConstraints(new GuiConstraintsManager.Builder()
                .setWidthConstraint(new RelativeConstraint(1, parent.content))
                .setHeightConstraint(new RelativeConstraint(1, parent.content))
                .setxConstraint(new CenterConstraint(parent.content))
                .setyConstraint(new RelativeConstraint(0, parent.content))
                .create());

        setLayout(new PatternGlobalConstraint(1, 6, 0));

        createDisplayOption();
        createDisplayModeOption();
        createResolutionOption();
        createFPSOption();
        createVSyncOption();

        GuiTextInput guiTextInput = new GuiTextInput(this);
        guiTextInput.setOutlineConstraints(new RelativeConstraint(1), new RelativeConstraint(.4f));
        guiTextInput.setOutlineColor(Color.WHITE);
        guiTextInput.setMaxLength(20);
        guiTextInput.setSelectedBackgroundColor(Utils.setAlphaColor(Color.decode("#1976D2"), 100));
    }


    private void createResolutionOption() {
        Text text = new Text(Words.RESOLUTION, .8f, DEFAULT_FONT, Color.BLACK);
        GuiRectangle resolutionArea = new GuiRectangle(this, Background.NO_BACKGROUND);
        GuiText guiText = new GuiText(resolutionArea, text,
                new GuiConstraintsManager.Builder()
                        .setDefault()
                        .setWidthConstraint(new RelativeConstraint(.5f))
                        .setHeightConstraint(new RelativeConstraint(1))
                        .setxConstraint(new SideConstraint(Side.LEFT, 0f))
                        .create());

        this.resolutionOptions = new GuiMultiOption<>(resolutionArea, Background.NO_BACKGROUND,
                DisplayManager.currentScreen.resolution, new GuiConstraintsManager.Builder()
                .setDefault()
                .setWidthConstraint(new RelativeConstraint(.5f))
                .setHeightConstraint(new RelativeConstraint(1))
                .setxConstraint(new StickyConstraint(Side.RIGHT, guiText, 0f))
                .create(), LIGHT_GRAY, new ArrayList<>(DisplayManager.currentScreen.resolutions));

        //TODO: Native Option by default "NATIVE"
        this.resolutionOptions.setOptionSelectedCallback(DisplayManager::setWindowSize);
    }

    private void createDisplayOption() {
        Text text = new Text(Words.TARGET_DISPLAY, .8f, DEFAULT_FONT, Color.BLACK);
        GuiRectangle displayArea = new GuiRectangle(this, Background.NO_BACKGROUND);
        GuiText guiText = new GuiText(displayArea, text,
                new GuiConstraintsManager.Builder()
                        .setDefault()
                        .setWidthConstraint(new RelativeConstraint(.5f))
                        .setHeightConstraint(new RelativeConstraint(1))
                        .setxConstraint(new SideConstraint(Side.LEFT, 0f))
                        .create());

        GuiMultiOption<String> displayMultiOptions = new GuiMultiOption<>(displayArea, Background.NO_BACKGROUND,
                Words.MONITOR + " " + (DisplayManager.indexCurrentScreen + 1), new GuiConstraintsManager.Builder()
                .setDefault()
                .setWidthConstraint(new RelativeConstraint(.5f))
                .setHeightConstraint(new RelativeConstraint(1))
                .setxConstraint(new StickyConstraint(Side.RIGHT, guiText, 0f)).create(), LIGHT_GRAY,
                DisplayManager.screens.stream().map(screen -> DisplayManager.screens.indexOf(screen))
                        .map(nb -> Words.MONITOR + " " + (nb + 1)).collect(Collectors.toList()));

        displayMultiOptions.setOptionSelectedCallback(
                screenName -> {
                    DisplayManager.setScreen(Integer.parseInt(screenName.split(" ")[1]) - 1);
                    DisplayManager.setDisplayMode(DisplayManager.displayMode);
                    DisplayManager.setWindowSize(DisplayManager.currentScreen.resolution);

                    this.resolutionOptions.setOptions(new ArrayList<>(DisplayManager.currentScreen.resolutions));
                    this.resolutionOptions.setSelectedOption(DisplayManager.currentScreen.resolution);
                });
    }

    private void createDisplayModeOption() {
        Text text = new Text(Words.DISPLAY_MODE, .8f, DEFAULT_FONT, Color.BLACK);
        GuiRectangle displayModeArea = new GuiRectangle(this, Background.NO_BACKGROUND);
        GuiText guiText = new GuiText(displayModeArea, text,
                new GuiConstraintsManager.Builder()
                        .setDefault()
                        .setWidthConstraint(new RelativeConstraint(.5f))
                        .setHeightConstraint(new RelativeConstraint(1))
                        .setxConstraint(new SideConstraint(Side.LEFT, 0f))
                        .create());

        GuiMultiOption<DisplayMode> guiMultiOption = new GuiMultiOption<>(displayModeArea, Background.NO_BACKGROUND,
                DisplayManager.displayMode, new GuiConstraintsManager.Builder()
                .setDefault()
                .setWidthConstraint(new RelativeConstraint(.5f))
                .setHeightConstraint(new RelativeConstraint(1))
                .setxConstraint(new StickyConstraint(Side.RIGHT, guiText, 0f))
                .create(), LIGHT_GRAY, Arrays.asList(DisplayMode.values()));

        guiMultiOption.setOptionSelectedCallback(displayMode -> {
            DisplayManager.setDisplayMode(displayMode);
            DisplayManager.setWindowSize(DisplayManager.currentScreen.resolution);
        });
    }

    private void createVSyncOption() {
        Text text = new Text(Words.VERTICAL_SYNC, .8f, DEFAULT_FONT, Color.BLACK);
        GuiRectangle vsyncArea = new GuiRectangle(this, Background.NO_BACKGROUND);
        GuiText guiText = new GuiText(vsyncArea, text,
                new GuiConstraintsManager.Builder()
                        .setDefault()
                        .setWidthConstraint(new RelativeConstraint(.5f))
                        .setHeightConstraint(new RelativeConstraint(1))
                        .setxConstraint(new SideConstraint(Side.LEFT, 0f))
                        .create());


        GuiOnOffOption guiOnOffOption = new GuiOnOffOption(vsyncArea, Background.NO_BACKGROUND,
                OnOff.getType(DisplayManager.vSync), new GuiConstraintsManager.Builder()
                .setDefault()
                .setWidthConstraint(new RelativeConstraint(.5f))
                .setHeightConstraint(new RelativeConstraint(1))
                .setxConstraint(new StickyConstraint(Side.RIGHT, guiText, 0f))
                .create(), LIGHT_GRAY);

        guiOnOffOption.setOptionSelectedCallback(res -> {
            DisplayManager.setVsync(res.getValue());
        });
    }

    private void createFPSOption() {
        Text text = new Text(Words.FPS_LIMIT, .8f, DEFAULT_FONT, Color.BLACK);
        GuiRectangle FPSArea = new GuiRectangle(this, Background.NO_BACKGROUND);
        GuiText guiText = new GuiText(FPSArea, text,
                new GuiConstraintsManager.Builder()
                        .setDefault()
                        .setWidthConstraint(new RelativeConstraint(.5f))
                        .setHeightConstraint(new RelativeConstraint(1))
                        .setxConstraint(new SideConstraint(Side.LEFT, 0f))
                        .create());


        fpsSlider = new GuiSlider(
                FPSArea, new Interval(DisplayManager.MAX_FPS, 30, 300, 1), LIGHT_GRAY, Color.BLACK,
                new GuiConstraintsManager.Builder()
                        .setDefault()
                        .setWidthConstraint(new RelativeConstraint(.5f))
                        .setHeightConstraint(new RelativeConstraint(.2f))
                        .setxConstraint(new StickyConstraint(Side.RIGHT, guiText, 0f))
                        .create());

        fpsSlider.showValue(Side.RIGHT);
        fpsSlider.setOnValueChanged(value -> DisplayManager.setFPS((int) value));
    }
}