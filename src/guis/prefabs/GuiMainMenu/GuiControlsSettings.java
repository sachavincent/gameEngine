package guis.prefabs.GuiMainMenu;

import static guis.prefabs.GuiMainMenu.GuiDisplaySettings.LIGHT_GRAY;

import fontMeshCreator.Text;
import guis.basics.GuiRectangle;
import guis.basics.GuiText;
import guis.constraints.*;
import guis.presets.Background;
import guis.presets.GuiMultiOption;
import guis.presets.buttons.GuiRectangleButton;
import inputs.KeyBindings;
import inputs.KeyboardUtils;
import inputs.Request;
import java.awt.Color;
import java.util.Arrays;
import java.util.stream.Collectors;
import language.Words;
import org.lwjgl.glfw.GLFW;
import util.KeybindingsManager.KeyboardLayout;

public class GuiControlsSettings extends GuiTab {

    public GuiControlsSettings(GuiMultiTab parent) {
        super(Background.NO_BACKGROUND, parent, Words.CONTROLS);

        setConstraints(new GuiConstraintsManager.Builder()
                .setWidthConstraint(new RelativeConstraint(1, parent.content))
                .setHeightConstraint(new RelativeConstraint(1, parent.content))
                .setxConstraint(new CenterConstraint(parent.content))
                .setyConstraint(new RelativeConstraint(0, parent.content))
                .create());

        setLayout(new PatternGlobalConstraint(1, 5, 0));

        createLayoutOption();
        createKeybindingsOption();
    }

    private void createLayoutOption() {
        Text text = new Text(Words.TARGET_DISPLAY, .8f, DEFAULT_FONT, Color.BLACK);
        GuiRectangle layoutArea = new GuiRectangle(this, Background.NO_BACKGROUND);
        GuiText guiText = new GuiText(layoutArea, text,
                new GuiConstraintsManager.Builder()
                        .setDefault()
                        .setWidthConstraint(new RelativeConstraint(.5f))
                        .setHeightConstraint(new RelativeConstraint(1))
                        .setxConstraint(new SideConstraint(Side.LEFT, 0f))
                        .create());

        GuiMultiOption<String> layoutMultiOption = new GuiMultiOption<>(layoutArea, Background.NO_BACKGROUND,
                KeyboardLayout.getCurrentKeyboardLayout().name(), new GuiConstraintsManager.Builder()
                .setDefault()
                .setWidthConstraint(new RelativeConstraint(.5f))
                .setHeightConstraint(new RelativeConstraint(1))
                .setxConstraint(new StickyConstraint(Side.RIGHT, guiText, 0f)).create(), LIGHT_GRAY,
                Arrays.stream(KeyboardLayout.values()).map(Enum::name).collect(Collectors.toList()));

        layoutMultiOption.setOptionSelectedCallback(
                layout -> KeyboardLayout.setCurrentKeyboardLayout(KeyboardLayout.getKeyboardLayoutFromName(layout)));
    }

    private void createKeybindingsOption() {
        Text text = new Text(Words.FORWARD, .8f, DEFAULT_FONT, Color.BLACK);
        Text defaultText = new Text(Words.PRESS_TO_ASSIGN, .8f, DEFAULT_FONT, Color.BLACK);
        Text pressKeyText = new Text(Words.PRESS_KEY, .8f, DEFAULT_FONT, Color.BLACK);
        Text assignedKeyText = new Text(String.valueOf((char) KeyBindings.FORWARD.getKey()), .8f, DEFAULT_FONT,
                Color.BLACK);

        GuiRectangle keyArea = new GuiRectangle(this, Background.NO_BACKGROUND);
        keyArea.setLayout(new RatioedPatternGlobalConstraint(3, 1, 0, 0, 30f, 100f, 30f, 100f, 40f, 100f));
        keyArea.setDisplayDebugOutline(false);
        GuiText guiText = new GuiText(keyArea, text);

        GuiText assignedKeyGuiText = new GuiText(keyArea, assignedKeyText);

        GuiRectangleButton rectangleButton = new GuiRectangleButton(keyArea, Background.NO_BACKGROUND,
                (GuiConstraintsManager) null);

        rectangleButton.setupText(defaultText);
        rectangleButton.setBorder(Color.BLUE);
        rectangleButton.setToggleType(true);
        final Request request = new Request((action, key) -> {
            rectangleButton.getText().setText(defaultText);
            rectangleButton.setClicked(false);

            if (key != GLFW.GLFW_KEY_ESCAPE)
                assignedKeyGuiText.getText().setTextString((char) key);

            return true;
        });
        rectangleButton.setOnPress(() -> {
            if (rectangleButton.isClicked()) {
                rectangleButton.getText().setText(pressKeyText);

                KeyboardUtils.request(request);
            } else {
                rectangleButton.getText().setText(defaultText);
                KeyboardUtils.cancelRequest(request);
            }
        });
    }
}