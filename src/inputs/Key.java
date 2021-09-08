package inputs;

import static util.KeybindingsManager.parseKey;

import engineTester.Game;
import engineTester.Game.GameState;
import entities.Camera;
import guis.Gui;
import guis.prefabs.GuiDebug;
import guis.prefabs.GuiEscapeMenu;
import guis.prefabs.GuiItemSelection;
import guis.prefabs.GuiMainMenu.GuiMainMenu;
import guis.prefabs.GuiMainMenu.GuiTab;
import inputs.callbacks.KeyPressCallback;
import inputs.callbacks.KeyReleaseCallback;
import inputs.callbacks.KeyRepeatCallback;
import java.util.ArrayList;
import java.util.List;
import language.Words;
import org.lwjgl.glfw.GLFW;
import renderEngine.AnimatedBuildingRenderer;
import renderEngine.BuildingRenderer;
import renderEngine.GuiRenderer;
import renderEngine.NPCRenderer;

public class Key {

    public static final String    TEMP = "TEMP";
    public static final List<Key> KEYS = new ArrayList<>();

    public static final Key DISPLAY_BOUNDING_BOXES = new Key("display_BB", new KeyInput('K'));
    public static final Key SHOW_BORDERS           = new Key("show_borders", new KeyInput(';'));
    public static final Key FORWARD                = new Key(Words.FORWARD.name(), new KeyInput('W'));
    public static final Key LEFT                   = new Key(Words.LEFT.name(), new KeyInput('A'));
    public static final Key BACKWARD               = new Key(Words.BACKWARD.name(), new KeyInput('S'));
    public static final Key RIGHT                  = new Key(Words.RIGHT.name(), new KeyInput('D'));
    public static final Key DEBUG                  = new Key("debug", new KeyInput('`'));
    public static final Key ESCAPE                 = new Key("escape", new KeyInput((char) GLFW.GLFW_KEY_ESCAPE));

    public static final Key ITEM_SELECTION = new Key("item selection", new KeyInput('M'));

    final KeyInput defaultKeyInput; // Default value in the QWERTY layout
    final String   name;
    KeyInput keyInput;

    private KeyPressCallback   onKeyPress;
    private KeyReleaseCallback onKeyRelease;
    private KeyRepeatCallback  onKeyRepeat;

    private Key(String name, KeyInput defaultKeyInput) {
        this.name = name;
        this.defaultKeyInput = defaultKeyInput;
        this.keyInput = defaultKeyInput;

        this.onKeyPress = () -> {
        };
        this.onKeyRelease = () -> {
        };
        this.onKeyRepeat = () -> {
        };
        if (!name.equals(TEMP))
            KEYS.add(this);
    }

    public void setValue(String value) {
        KeyInput keyInput = parseKey(value);
        setValue(keyInput);
    }

    public void setValue(KeyInput keyInput) {
        this.keyInput = keyInput;
    }

    public KeyInput getKeyInput() {
        return this.keyInput;
    }

    public KeyInput getDefaultKeyInput() {
        return this.defaultKeyInput;
    }

    public void setKeyInput(KeyInput keyInput) {
        this.keyInput = keyInput;
    }

    public static Key getKeyFromInput(KeyInput keyInput) {
        return KEYS.stream().filter(key -> key.getKeyInput().equals(keyInput)).findFirst().orElse(null);
    }

    public static Key getKeyFromName(String name) {
        return KEYS.stream().filter(key -> key.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void reset() {
        setValue(this.defaultKeyInput);
    }

    public String getName() {
        return this.name;
    }

    public static int getEmptyKey() {
        return 48;
    }

    public void on(int action) {
        if (action == GLFW.GLFW_PRESS)
            this.onKeyPress.onPress();
        else if (action == GLFW.GLFW_REPEAT)
            this.onKeyRepeat.onRepeat();
        else if (action == GLFW.GLFW_RELEASE)
            this.onKeyRelease.onRelease();
    }

    static {
        Key.DEBUG.onKeyPress = () -> {
//            System.out.println("pressed debug");
            Gui.toggleGui(GuiDebug.getInstance());
//            System.out.println("t");
        };

        Key.ESCAPE.onKeyPress = () -> {
            if (Game.getInstance().getGameState() == GameState.NOT_STARTED)
                GuiMainMenu.getInstance().back();
            else {
                final List<Gui> displayedGuis = Game.getInstance().getDisplayedGuis();
                boolean isNoGuiDisplayed = displayedGuis.isEmpty();
                GuiEscapeMenu guiEscapeMenu = GuiEscapeMenu.getInstance();
                switch (Game.getInstance().getGameState()) {
                    case NOT_STARTED:
                        break;
                    case STARTED:
                        if (!isNoGuiDisplayed) {
                            Gui lastGuiOpened = displayedGuis.get(displayedGuis.size() - 1);
                            lastGuiOpened.setDisplayed(false);
                            displayedGuis.remove(lastGuiOpened);
                        } else {
                            guiEscapeMenu.setDisplayed(true);
                            Game.getInstance().pause();
                        }
                        break;
                    case PAUSED:
                        if (!guiEscapeMenu.isDisplayed() && !isNoGuiDisplayed) { // Sub-menu opened
                            Gui lastGuiOpened = displayedGuis.get(displayedGuis.size() - 1);
                            lastGuiOpened.setDisplayed(false);
                            if (lastGuiOpened instanceof GuiTab) {
                                ((GuiTab) lastGuiOpened).getParent().setDisplayed(false);
                                displayedGuis.remove(((GuiTab) lastGuiOpened).getParent());
                            }
                            displayedGuis.remove(lastGuiOpened);
                            if (Game.getInstance().getDisplayedGuis().isEmpty())
                                guiEscapeMenu.setDisplayed(true);
                        } else if (isNoGuiDisplayed ||
                                Game.getInstance().getGameState() == GameState.PAUSED) {
                            guiEscapeMenu.setDisplayed(false);
                            Game.getInstance().resume();
                        }
                        break;
                }
            }
        };
        Key.DISPLAY_BOUNDING_BOXES.onKeyPress = () -> {
            if (Game.getInstance().getGameState() == GameState.STARTED) {
                BuildingRenderer.getInstance().switchDisplayBoundingBoxes();
                AnimatedBuildingRenderer.getInstance().switchDisplayBoundingBoxes();
                NPCRenderer.getInstance().switchDisplayBoundingBoxes();
                System.out.println("Display BB pressed");
            }
        };
        Key.FORWARD.onKeyPress = () -> {
            if (Game.getInstance().getGameState() == GameState.STARTED)
                Camera.getInstance().moveTo(Camera.getInstance().getYaw());
        };
        Key.BACKWARD.onKeyPress = () -> {
            if (Game.getInstance().getGameState() == GameState.STARTED)
                Camera.getInstance().moveTo(Camera.getInstance().getYaw() + 180);
        };
        Key.LEFT.onKeyPress = () -> {
            if (Game.getInstance().getGameState() == GameState.STARTED)
                Camera.getInstance().moveTo(Camera.getInstance().getYaw() + 270);
        };
        Key.RIGHT.onKeyPress = () -> {
            if (Game.getInstance().getGameState() == GameState.STARTED)
                Camera.getInstance().moveTo(Camera.getInstance().getYaw() + 90);
        };
        Key.LEFT.onKeyRelease = Key.RIGHT.onKeyRelease = Key.FORWARD.onKeyRelease = Key.BACKWARD.onKeyRelease = () -> {
            if (Game.getInstance().getGameState() == GameState.STARTED)
                Camera.getInstance().resetMovement();
        };
        Key.SHOW_BORDERS.onKeyPress = GuiRenderer::switchDisplayDebugOutlines;

        Key.ITEM_SELECTION.onKeyPress = () -> {
            if (Game.getInstance().getGameState() == GameState.STARTED)
                Gui.toggleGui(GuiItemSelection.getItemSelectionGui());
        };
    }
}
