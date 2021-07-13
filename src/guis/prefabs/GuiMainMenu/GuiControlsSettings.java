package guis.prefabs.GuiMainMenu;

import static guis.prefabs.GuiMainMenu.GuiDisplaySettings.LIGHT_GRAY;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;

import fontMeshCreator.Text;
import guis.basics.GuiRectangle;
import guis.basics.GuiText;
import guis.constraints.CenterConstraint;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RelativeConstraint;
import guis.constraints.Side;
import guis.constraints.SideConstraint;
import guis.constraints.StickyConstraint;
import guis.constraints.layout.PatternLayout;
import guis.constraints.layout.RatioedPatternLayout;
import guis.presets.Background;
import guis.presets.GuiMultiOption;
import guis.presets.buttons.GuiAbstractButton;
import guis.presets.buttons.GuiRectangleButton;
import inputs.ClickType;
import inputs.Key;
import inputs.KeyInput;
import inputs.requests.KeyMappingRequest;
import inputs.requests.Request.RequestType;
import inputs.requests.RequestManager;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import language.Words;
import org.lwjgl.glfw.GLFW;
import util.KeybindingsManager;
import util.KeybindingsManager.KeyboardLayout;

public class GuiControlsSettings extends GuiTab {

    private final static Text PRESS_TO_ASSIGN = new Text(Words.PRESS_TO_ASSIGN, .8f, DEFAULT_FONT, Color.BLACK);
    private final static Text PRESS_KEY_TEXT  = new Text(Words.PRESS_KEY, .8f, DEFAULT_FONT, Color.BLACK);

    private GuiMultiOption<String> layoutMultiOption;
    private List<GuiText>          bindings;

    public GuiControlsSettings(GuiMultiTab parent) {
        super(Background.NO_BACKGROUND, parent, Words.CONTROLS);

        setConstraints(new GuiConstraintsManager.Builder()
                .setWidthConstraint(new RelativeConstraint(1, parent.content))
                .setHeightConstraint(new RelativeConstraint(1, parent.content))
                .setxConstraint(new CenterConstraint(parent.content))
                .setyConstraint(new RelativeConstraint(0, parent.content))
                .create());

        setLayout(new PatternLayout(1, Key.KEYS.size() + 1, 0, 0.02f));

        createLayoutOption();
        createKeybindingsOption();
    }

    private void createLayoutOption() {
        Text text = new Text(Words.KEYBOARD_KAYOUT, .8f, DEFAULT_FONT, Color.BLACK);
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
                .setxConstraint(new StickyConstraint(Side.RIGHT, 0, guiText)).create(), LIGHT_GRAY,
                Arrays.stream(KeyboardLayout.values()).map(Enum::name).collect(Collectors.toList()));
    }

    private void createKeybindingsOption() {
        this.bindings = new ArrayList<>();
        final List<Key> keyBindings = new ArrayList<>(Key.KEYS);
        for (Key key : keyBindings) {
            if (key.getKeyInput() == null)
                continue;
            KeyInput keyInput = key.getKeyInput().toLocalKeyboardLayout();
            Text assignedKeyText = new Text(keyInput.formatKeyInput(), .8f, DEFAULT_FONT, Color.BLACK);

            GuiRectangle keyArea = new GuiRectangle(this, Background.NO_BACKGROUND);
            keyArea.setLayout(new RatioedPatternLayout(3, 1, 0, 0, 30f, 100f, 30f, 100f, 40f, 100f));
            new GuiText(keyArea, new Text(key.getName(), .8f, DEFAULT_FONT, Color.BLACK));

            GuiText assignedKeyGuiText = new GuiText(keyArea, assignedKeyText);
            assignedKeyGuiText.setOnUpdate(() -> {
                String text = key.getKeyInput().getKey() == Key.getEmptyKey() ? ""
                        : key.getKeyInput().toLocalKeyboardLayout().formatKeyInput();
                assignedKeyGuiText.getText().setTextString(text);
            });
            this.bindings.add(assignedKeyGuiText);

            GuiRectangleButton rectangleButton = new GuiRectangleButton(keyArea, Background.NO_BACKGROUND, Color.BLUE,
                    (GuiConstraintsManager) null);

            rectangleButton.setupText(PRESS_TO_ASSIGN);
            rectangleButton.setToggleType(true);
            final KeyMappingRequest request = new KeyMappingRequest((action, newKeyInput) -> {
                if (action == GLFW.GLFW_PRESS) {
                    rectangleButton.getText().setText(PRESS_TO_ASSIGN);
                    rectangleButton.setClickType(ClickType.NONE);

                    char newGlobalKey = newKeyInput.getKey();
                    if (newGlobalKey != GLFW.GLFW_KEY_ESCAPE) { // Non cancel keys
                        updateAllKeys(assignedKeyGuiText.getText().getTextString(), newKeyInput);
                        Key.getKeyFromName(key.getName()).setValue(newKeyInput);
                        assignedKeyGuiText.update();
                    }
                } else
                    return false;

                return true;
            });
            AtomicInteger requestId = new AtomicInteger();
            rectangleButton.setOnMousePress(button -> {
                ClickType clickType = rectangleButton.getClickType();
                switch (button) {
                    case GLFW_MOUSE_BUTTON_1:
                        if (clickType == ClickType.M1) {
                            rectangleButton.getText().setText(PRESS_KEY_TEXT);

                            int id = RequestManager.getInstance().request(new KeyMappingRequest(request));
                            requestId.set(id);
                        } else {
                            restoreButton(rectangleButton, requestId.get());
                        }
                        break;
                    case GLFW_MOUSE_BUTTON_2:
                        switch (clickType) {
                            case M1:
                                Key.getKeyFromName(key.getName()).setValue(String.valueOf((char) Key.getEmptyKey()));
                                assignedKeyGuiText.update();

                                rectangleButton.setClickType(ClickType.NONE);
                                restoreButton(rectangleButton, requestId.get());
                                break;
                            case M2:
                                rectangleButton.setClickType(ClickType.NONE); // If M2 before M1
                                break;
                            case MIDDLE:
                            case NONE:
                                return false;
                        }
                        break;
                    default:
                        String keyName = "MOUSEBUTTON" + button;
                        KeyInput newInput = KeybindingsManager.parseKey(keyName);
                        updateAllKeys(assignedKeyGuiText.getText().getTextString(), newInput);

                        Key.getKeyFromName(key.getName()).setKeyInput(newInput);
                        assignedKeyGuiText.update();

                        rectangleButton.setClickType(ClickType.NONE);
                        restoreButton(rectangleButton, requestId.get());
                        break;
                }
                return true;
            });

        }
        this.layoutMultiOption.setOptionSelectedCallback(layout -> {
            KeyboardLayout.setCurrentKeyboardLayout(KeyboardLayout.getKeyboardLayoutFromName(layout));
            this.bindings.forEach(GuiText::update);
        });
    }

    private void updateAllKeys(String assignedKeyText, KeyInput newKeyInput) {
        String previousLocalKey = assignedKeyText.isEmpty() ? String
                .valueOf((char) Key.getEmptyKey()) : assignedKeyText;

        KeyInput previousKeyInput = KeybindingsManager.parseKey(previousLocalKey);
        if (previousKeyInput != null) {
            KeyInput previousGlobalKeyInput = previousKeyInput.toDefaultKeyboardLayout();

            Key.KEYS.stream().filter(k -> k.getKeyInput().equals(newKeyInput))
                    .findFirst()
                    .ifPresent(k -> {
                        k.setKeyInput(previousGlobalKeyInput);
                        this.bindings.forEach(GuiText::update);
                    });
        }
    }

    private void restoreButton(GuiAbstractButton button, int requestId) {
        button.getText().setText(PRESS_TO_ASSIGN);
        RequestManager.getInstance().cancelRequest(RequestType.KEY, requestId);
    }
}