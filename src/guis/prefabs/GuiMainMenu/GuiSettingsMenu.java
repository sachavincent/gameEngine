package guis.prefabs.GuiMainMenu;

import fontMeshCreator.Text;
import guis.Gui;
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
import guis.presets.buttons.GuiRectangleButton;
import inputs.callbacks.BackCallback;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import renderEngine.DisplayManager;
import renderEngine.DisplayManager.DisplayMode;
import renderEngine.DisplayManager.Resolution;
import util.Utils;

public class GuiSettingsMenu extends Gui {

    private final Color                      lightGray = Color.decode("#BDBDBD");
    private final GuiRectangle               buttonsArea;
    public        GuiSlider                  fpsSlider;
    private       GuiMultiOption<Resolution> resolutionOptions;
    private final GuiRectangle               backButtonArea;

    public GuiSettingsMenu(Gui parent) {
        super(Background.NO_BACKGROUND);

        setConstraints(new GuiConstraintsManager.Builder()
                .setWidthConstraint(new RelativeConstraint(1, parent))
                .setHeightConstraint(new RelativeConstraint(1, parent))
                .setxConstraint(new CenterConstraint(parent))
                .setyConstraint(new RelativeConstraint(0, parent))
                .create());

        setChildrenConstraints(new RatioedPatternGlobalConstraint(1, 2, 0, 0, 100f, 4f, 100f, 96f));
        this.backButtonArea = new GuiRectangle(this, Background.NO_BACKGROUND);

        this.buttonsArea = new GuiRectangle(this, Background.NO_BACKGROUND);
        this.buttonsArea.setChildrenConstraints(new PatternGlobalConstraint(1, 6, 0));


        createDisplayOption();
        createDisplayModeOption();
        createResolutionOption();
        createFPSOption();
        createVSyncOption();

        GuiTextInput guiTextInput = new GuiTextInput(this.buttonsArea);
        guiTextInput.setOutlineConstraints(new RelativeConstraint(1), new RelativeConstraint(.4f));
        guiTextInput.setOutlineColor(Color.WHITE);
        guiTextInput.setMaxLength(20);
        guiTextInput.setSelectedBackgroundColor(Utils.setAlphaColor(Color.decode("#1976D2"), 100));
    }

    public void onBackButtonPress(BackCallback backCallback) {
        Background<String> back = new Background<>("back_arrow.png");
        GuiRectangleButton guiBack = new GuiRectangleButton(this.backButtonArea, back,
                new GuiConstraintsManager.Builder()
                        .setHeightConstraint(new RelativeConstraint(1))
                        .setWidthConstraint(new AspectConstraint(1))
                        .setxConstraint(new SideConstraint(Side.LEFT, 0f))
                        .setyConstraint(new CenterConstraint())
                        .create());

        guiBack.setOnPress(backCallback::onBack);
    }

    private void createResolutionOption() {
        Text text = new Text("Resolution", .8f, DEFAULT_FONT, Color.BLACK);
        GuiRectangle resolutionArea = new GuiRectangle(this.buttonsArea, Background.NO_BACKGROUND);
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
                .create(), lightGray, new ArrayList<>(DisplayManager.currentScreen.resolutions));

        this.resolutionOptions.setOptionSelectedCallback(DisplayManager::setWindowSize);
    }

    private void createDisplayOption() {
        Text text = new Text("Target Display", .8f, DEFAULT_FONT, Color.BLACK);
        GuiRectangle displayArea = new GuiRectangle(this.buttonsArea, Background.NO_BACKGROUND);
        GuiText guiText = new GuiText(displayArea, text,
                new GuiConstraintsManager.Builder()
                        .setDefault()
                        .setWidthConstraint(new RelativeConstraint(.5f))
                        .setHeightConstraint(new RelativeConstraint(1))
                        .setxConstraint(new SideConstraint(Side.LEFT, 0f))
                        .create());

        GuiMultiOption<String> displayMultiOptions = new GuiMultiOption<>(displayArea, Background.NO_BACKGROUND,
                "Monitor " + (DisplayManager.indexCurrentScreen + 1), new GuiConstraintsManager.Builder()
                .setDefault()
                .setWidthConstraint(new RelativeConstraint(.5f))
                .setHeightConstraint(new RelativeConstraint(1))
                .setxConstraint(new StickyConstraint(Side.RIGHT, guiText, 0f)).create(), lightGray,
                DisplayManager.screens.stream().map(screen -> DisplayManager.screens.indexOf(screen))
                        .map(nb -> "Monitor " + (nb + 1)).collect(Collectors.toList()));

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
        Text text = new Text("Display Mode", .8f, DEFAULT_FONT, Color.BLACK);
        GuiRectangle displayModeArea = new GuiRectangle(this.buttonsArea, Background.NO_BACKGROUND);
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
                .create(), lightGray, Arrays.asList(DisplayMode.values()));

        guiMultiOption.setOptionSelectedCallback(displayMode -> {
            DisplayManager.setDisplayMode(displayMode);
            DisplayManager.setWindowSize(DisplayManager.currentScreen.resolution);
        });
    }

    private void createVSyncOption() {
        Text text = new Text("V-Sync", .8f, DEFAULT_FONT, Color.BLACK);
        GuiRectangle vsyncArea = new GuiRectangle(this.buttonsArea, Background.NO_BACKGROUND);
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
                .create(), lightGray);

        guiOnOffOption.setOptionSelectedCallback(res -> {
            DisplayManager.setVsync(res.getValue());
        });
    }

    private void createFPSOption() {
        Text text = new Text("FPS Limit", .8f, DEFAULT_FONT, Color.BLACK);
        GuiRectangle FPSArea = new GuiRectangle(this.buttonsArea, Background.NO_BACKGROUND);
        GuiText guiText = new GuiText(FPSArea, text,
                new GuiConstraintsManager.Builder()
                        .setDefault()
                        .setWidthConstraint(new RelativeConstraint(.5f))
                        .setHeightConstraint(new RelativeConstraint(1))
                        .setxConstraint(new SideConstraint(Side.LEFT, 0f))
                        .create());


        fpsSlider = new GuiSlider(
                FPSArea, new Interval(DisplayManager.MAX_FPS, 30, 300, 1), lightGray, Color.BLACK,
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