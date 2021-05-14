package guis.prefabs.GuiMainMenu;

import static guis.prefabs.GuiMainMenu.GuiDisplaySettings.LIGHT_GRAY;

import fontMeshCreator.Text;
import guis.basics.GuiRectangle;
import guis.basics.GuiText;
import guis.constraints.*;
import guis.presets.Background;
import guis.presets.GuiMultiOption;
import guis.presets.buttons.GuiRectangleButton;
import inputs.ClickType;
import inputs.KeyBindings;
import inputs.KeyInput;
import inputs.requests.KeyMappingRequest;
import inputs.KeyboardUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import language.Words;
import org.lwjgl.glfw.GLFW;
import util.KeybindingsManager;
import util.KeybindingsManager.KeyboardLayout;

public class GuiControlsSettings extends GuiTab {

    private GuiMultiOption<String> layoutMultiOption;

    public GuiControlsSettings(GuiMultiTab parent) {
        super(Background.NO_BACKGROUND, parent, Words.CONTROLS);

        setConstraints(new GuiConstraintsManager.Builder()
                .setWidthConstraint(new RelativeConstraint(1, parent.content))
                .setHeightConstraint(new RelativeConstraint(1, parent.content))
                .setxConstraint(new CenterConstraint(parent.content))
                .setyConstraint(new RelativeConstraint(0, parent.content))
                .create());

        setLayout(new PatternGlobalConstraint(1, 6, 0, 0.02f));

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

        this.layoutMultiOption = new GuiMultiOption<>(layoutArea, Background.NO_BACKGROUND,
                KeyboardLayout.getCurrentKeyboardLayout().name(), new GuiConstraintsManager.Builder()
                .setDefault()
                .setWidthConstraint(new RelativeConstraint(.5f))
                .setHeightConstraint(new RelativeConstraint(1))
                .setxConstraint(new StickyConstraint(Side.RIGHT, guiText, 0f)).create(), LIGHT_GRAY,
                Arrays.stream(KeyboardLayout.values()).map(Enum::name).collect(Collectors.toList()));
    }

    private void createKeybindingsOption() {
        final List<GuiText> bindings = new ArrayList<>();
        final List<KeyBindings> keyBindings = Arrays.asList(KeyBindings.values());
        for (KeyBindings keyBinding : keyBindings) {
            Text defaultText = new Text(Words.PRESS_TO_ASSIGN, .8f, DEFAULT_FONT, Color.BLACK);
            Text pressKeyText = new Text(Words.PRESS_KEY, .8f, DEFAULT_FONT, Color.BLACK);
            KeyInput keyInput = keyBinding.getKeyInput().toLocalKeyboardLayout();
            Text assignedKeyText = new Text(keyInput.formatKeyInput(), .8f, DEFAULT_FONT, Color.BLACK);

            GuiRectangle keyArea = new GuiRectangle(this, Background.NO_BACKGROUND);
            keyArea.setLayout(new RatioedPatternGlobalConstraint(3, 1, 0, 0, 30f, 100f, 30f, 100f, 40f, 100f));
            new GuiText(keyArea, new Text(keyBinding.getName(), .8f, DEFAULT_FONT, Color.BLACK));

            GuiText assignedKeyGuiText = new GuiText(keyArea, assignedKeyText);
            assignedKeyGuiText.setOnUpdate(() -> {
                String text = keyBinding.getKey() == KeyBindings.getEmptyKey() ? ""
                        : keyBinding.getKeyInput().toLocalKeyboardLayout().formatKeyInput();
                assignedKeyGuiText.getText().setTextString(text);
            });
            bindings.add(assignedKeyGuiText);

            GuiRectangleButton rectangleButton = new GuiRectangleButton(keyArea, Background.NO_BACKGROUND,
                    (GuiConstraintsManager) null);

            rectangleButton.setupText(defaultText);
            rectangleButton.setBorder(Color.BLUE);
            rectangleButton.setToggleType(true);
            final KeyMappingRequest request = new KeyMappingRequest((action, newKeyInput) -> {
                if (action == GLFW.GLFW_PRESS) {
                    rectangleButton.getText().setText(defaultText);
                    rectangleButton.setClickType(ClickType.NONE);

                    char newGlobalKey = newKeyInput.getKey();
                    if (newGlobalKey != GLFW.GLFW_KEY_ESCAPE) { // Non cancel keys
                        String previousLocalKey = assignedKeyText.getTextString().isEmpty() ? String
                                .valueOf((char) KeyBindings.getEmptyKey()) : assignedKeyText.getTextString();

                        KeyInput previousKeyInput = KeybindingsManager.parseKey(previousLocalKey);
                        if (previousKeyInput != null) {
                            KeyInput previousGlobalKeyInput = previousKeyInput.toDefaultKeyboardLayout();

                            keyBindings.stream().filter(k -> k.getKeyInput().equals(newKeyInput))
                                    .findFirst()
                                    .ifPresent(k -> {
                                        k.setKeyInput(previousGlobalKeyInput);
                                        bindings.forEach(GuiText::update);
                                    });
                        }
                        KeybindingsManager.Key.getKey(keyBinding.getName()).setValue(newKeyInput);
                        assignedKeyGuiText.update();
                    }
                } else
                    return false;

                return true;
            });

            rectangleButton.setOnMousePress(button -> {
                ClickType clickType = rectangleButton.getClickType();
                if (button == GLFW.GLFW_MOUSE_BUTTON_2 && clickType == ClickType.M2) {
                    rectangleButton.setClickType(ClickType.NONE); // If M2 before M1
                } else if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
                    if (clickType == ClickType.M1) {
                        rectangleButton.getText().setText(pressKeyText);

                        KeyboardUtils.request(new KeyMappingRequest(request));
                    } else {
                        rectangleButton.getText().setText(defaultText);
                        KeyboardUtils.cancelRequest();
                    }
                } else if (button == GLFW.GLFW_MOUSE_BUTTON_2 && clickType == ClickType.M1) {
                    KeybindingsManager.Key.getKey(keyBinding.getName())
                            .setValue(String.valueOf((char) KeyBindings.getEmptyKey()));
                    assignedKeyGuiText.update();

                    rectangleButton.getText().setText(defaultText);
                    rectangleButton.setClickType(ClickType.NONE);
                    KeyboardUtils.cancelRequest();
                }
            });

        }
        this.layoutMultiOption.setOptionSelectedCallback(layout -> {
            KeyboardLayout.setCurrentKeyboardLayout(KeyboardLayout.getKeyboardLayoutFromName(layout));
            bindings.forEach(GuiText::update);
        });
    }
}