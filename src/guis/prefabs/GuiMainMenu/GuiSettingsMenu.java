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
import guis.presets.buttons.GuiRectangleButton;
import java.awt.Color;
import java.util.ArrayList;
import renderEngine.DisplayManager;
import renderEngine.DisplayManager.Resolution;

public class GuiSettingsMenu extends Gui {

    private final Color        lightGray = Color.decode("#BDBDBD");
    private final GuiRectangle buttonsArea;
    public        GuiSlider    fpsSlider;

    public GuiSettingsMenu(Gui parent) {
        super(Background.NO_BACKGROUND);

        setConstraints(new GuiConstraintsManager.Builder()
                .setWidthConstraint(new RelativeConstraint(1, parent))
                .setHeightConstraint(new RelativeConstraint(1, parent))
                .setxConstraint(new CenterConstraint(parent))
                .setyConstraint(new RelativeConstraint(0, parent))
                .create());

        setChildrenConstraints(new RatioedPatternGlobalConstraint(1, 2, 0, 0, 100f, 4f, 100f, 96f));
        GuiRectangle backButtonArea = new GuiRectangle(this, Background.NO_BACKGROUND);
        this.buttonsArea = new GuiRectangle(this, Background.NO_BACKGROUND);
        this.buttonsArea.setChildrenConstraints(new PatternGlobalConstraint(1, 3, 0));

        createBackButton(backButtonArea);

        createResolutionOption();
        createFPSSlider();
        createVSyncOption();

        setDisplayed(false);
    }

    private void createBackButton(GuiRectangle backButtonArea) {
        Background<String> back = new Background<>("back_arrow.png");
        GuiRectangleButton guiBack = new GuiRectangleButton(backButtonArea, back, new GuiConstraintsManager.Builder()
                .setHeightConstraint(new RelativeConstraint(1))
                .setWidthConstraint(new AspectConstraint(1))
                .setxConstraint(new SideConstraint(Side.LEFT, 0))
                .setyConstraint(new CenterConstraint())
                .create());

        guiBack.setOnPress(() -> GuiMainMenu.getInstance().back());
    }

    private void createResolutionOption() {
        Text text = new Text("Resolution", .8f, DEFAULT_FONT, Color.BLACK);
        GuiRectangle resolutionArea = new GuiRectangle(this.buttonsArea, Background.NO_BACKGROUND);
        GuiText guiText = new GuiText(resolutionArea, text,
                new GuiConstraintsManager.Builder()
                        .setDefault()
                        .setWidthConstraint(new RelativeConstraint(.5f))
                        .setHeightConstraint(new RelativeConstraint(1))
                        .setxConstraint(new SideConstraint(Side.LEFT, 0))
                        .create());

        GuiMultiOption<Resolution> guiMultiOption = new GuiMultiOption<>(resolutionArea, Background.NO_BACKGROUND,
                DisplayManager.currentScreen.resolution, new GuiConstraintsManager.Builder()
                .setDefault()
                .setWidthConstraint(new RelativeConstraint(.5f))
                .setHeightConstraint(new RelativeConstraint(1))
                .setxConstraint(new StickyConstraint(Side.RIGHT, guiText, 0))
                .create(), lightGray, new ArrayList<>(DisplayManager.currentScreen.resolutions));

        guiMultiOption.setOptionSelectedCallback(DisplayManager::setWindowSize);
    }

    private void createVSyncOption() {
        Text text = new Text("V-Sync", .8f, DEFAULT_FONT, Color.BLACK);
        GuiRectangle vsyncArea = new GuiRectangle(this.buttonsArea, Background.NO_BACKGROUND);
        GuiText guiText = new GuiText(vsyncArea, text,
                new GuiConstraintsManager.Builder()
                        .setDefault()
                        .setWidthConstraint(new RelativeConstraint(.5f))
                        .setHeightConstraint(new RelativeConstraint(1))
                        .setxConstraint(new SideConstraint(Side.LEFT, 0))
                        .create());


        GuiOnOffOption guiOnOffOption = new GuiOnOffOption(vsyncArea, Background.NO_BACKGROUND,
                OnOff.getType(DisplayManager.vSync), new GuiConstraintsManager.Builder()
                .setDefault()
                .setWidthConstraint(new RelativeConstraint(.5f))
                .setHeightConstraint(new RelativeConstraint(1))
                .setxConstraint(new StickyConstraint(Side.RIGHT, guiText, 0))
                .create(), lightGray);

        guiOnOffOption.setOptionSelectedCallback(res -> {
            DisplayManager.setVsync(res.getValue());
        });
    }

    private void createFPSSlider() {
        Text text = new Text("FPS Limit", .8f, DEFAULT_FONT, Color.BLACK);
        GuiRectangle FPSArea = new GuiRectangle(this.buttonsArea, Background.NO_BACKGROUND);
        GuiText guiText = new GuiText(FPSArea, text,
                new GuiConstraintsManager.Builder()
                        .setDefault()
                        .setWidthConstraint(new RelativeConstraint(.5f))
                        .setHeightConstraint(new RelativeConstraint(1))
                        .setxConstraint(new SideConstraint(Side.LEFT, 0))
                        .create());


        fpsSlider = new GuiSlider(
                FPSArea, new Interval(DisplayManager.MAX_FPS, 30, 300, 1), lightGray, Color.BLACK,
                new GuiConstraintsManager.Builder()
                        .setDefault()
                        .setWidthConstraint(new RelativeConstraint(.5f))
                        .setHeightConstraint(new RelativeConstraint(.2f))
                        .setxConstraint(new StickyConstraint(Side.RIGHT, guiText, 0))
                        .create());

        fpsSlider.showValue(Side.RIGHT);
        fpsSlider.setOnValueChanged(value -> DisplayManager.setFPS((int) value));
    }
}