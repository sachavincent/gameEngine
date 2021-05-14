package inputs;

import static org.lwjgl.glfw.GLFW.*;

import engineTester.Game;
import engineTester.Game.GameState;
import entities.Camera;
import fontMeshCreator.Text;
import guis.Gui;
import guis.prefabs.GuiEscapeMenu;
import guis.prefabs.GuiItemSelection;
import guis.prefabs.GuiMainMenu.GuiMainMenu;
import inputs.requests.Request;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.lwjgl.system.Callback;
import renderEngine.DisplayManager;
import renderEngine.GuiRenderer;

public class KeyboardUtils {

    private static Callback callback;

    private static final Queue<Request> requests = new LinkedList<>();

    public static boolean isCapsLock;
    public static boolean isNumLock;
    public static boolean isSuper;
    public static boolean isAlt;
    public static boolean isControl;
    public static boolean isShift;

    public static void cancelRequest(Request request) {
        requests.remove(request);
    }

    public static void cancelRequest() {
        requests.poll();
    }

    public static void request(Request request) {
        requests.add(request);
    }

    private static boolean handleRequests(int action, char pressedKey) {
        if (requests.isEmpty())
            return false;

        Request request = requests.element();

        if (request != null)
            return request.getOnHandleRequest().onHandle(action, pressedKey, request, requests);

        return false;
    }

    public static void setupListeners() {
        callback = glfwSetKeyCallback(DisplayManager.getWindow(), (w, key, scancode, action, mods) -> {
            // Caps lock & Num lock handling
            {
                int numLock = (mods & GLFW_MOD_NUM_LOCK);
                int capsLock = ((mods - numLock) & GLFW_MOD_CAPS_LOCK);
                int superPressed = ((mods - capsLock) & GLFW_MOD_SUPER);
                int altPressed = ((mods - superPressed) & GLFW_MOD_ALT);
                int ctrlPressed = ((mods - altPressed) & GLFW_MOD_CONTROL);
                int shiftPressed = ((mods - ctrlPressed) & GLFW_MOD_SHIFT);

                isCapsLock = capsLock == GLFW_MOD_CAPS_LOCK;
                isNumLock = numLock == GLFW_MOD_NUM_LOCK;
                isSuper = superPressed == GLFW_MOD_SUPER;
                isAlt = altPressed == GLFW_MOD_ALT;
                isControl = ctrlPressed == GLFW_MOD_CONTROL;
                isShift = shiftPressed == GLFW_MOD_SHIFT;
            }
            boolean handled = handleRequests(action, (char) key);
            if (handled)
                return;

//            List<GuiTextInput> focusedInputs = Game.getInstance().getGuiTextInputs().stream()
//                    .filter(guiTextInput -> guiTextInput.getClickType() != ClickType.NONE).collect(Collectors.toList());
//            boolean isKeyProcessed = false;
//            if (!focusedInputs.isEmpty())
//                isKeyProcessed = focusedInputs.stream()
//                        .allMatch(guiTextInput -> guiTextInput.processKeyboardInput(action, key));
//
//            if (isKeyProcessed)
//                return;

            if (action == GLFW_PRESS) {
                if (key == GLFW_KEY_ESCAPE) {
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
                } else if (key == KeyBindings.DISPLAY_BOUNDING_BOXES.getKey()) {
                    if (Game.getInstance().getGameState() == GameState.STARTED)
//                        HouseRenderer.getInstance().switchDisplayBoundingBoxes();
                        System.out.println("Display BB pessed");
                } else if (key == KeyBindings.FORWARD.getKey()) {
                    if (Game.getInstance().getGameState() == GameState.STARTED)
                        Camera.getInstance().moveTo(Camera.getInstance().getYaw());
                } else if (key == KeyBindings.BACKWARD.getKey()) {
                    if (Game.getInstance().getGameState() == GameState.STARTED)
                        Camera.getInstance().moveTo(Camera.getInstance().getYaw() + 180);
                } else if (key == KeyBindings.LEFT.getKey()) {
                    if (Game.getInstance().getGameState() == GameState.STARTED)
                        Camera.getInstance().moveTo(Camera.getInstance().getYaw() + 270);
                } else if (key == KeyBindings.RIGHT.getKey()) {
                    if (Game.getInstance().getGameState() == GameState.STARTED)
                        Camera.getInstance().moveTo(Camera.getInstance().getYaw() + 90);
                } else if (key == GLFW_KEY_M) {
                    if (Game.getInstance().getGameState() == GameState.STARTED)
                        Gui.toggleGui(GuiItemSelection.getItemSelectionGui());
                } else if (key == GLFW_KEY_SEMICOLON) {
                    GuiRenderer.switchDisplayDebugOutlines();
                } else if (key == GLFW_KEY_DOWN) {
                    Text textSettings = GuiMainMenu.getInstance().textSettings;
                    textSettings.setCharWidth(textSettings.getCharWidth() - 0.01f);
                    System.out.println("CharWidth : " + textSettings.getCharWidth());
                } else if (key == GLFW_KEY_UP) {
                    Text textSettings;
                    textSettings = GuiMainMenu.getInstance().textSettings;
                    textSettings.setCharWidth(textSettings.getCharWidth() + 0.01f);
                    System.out.println("CharWidth : " + textSettings.getCharWidth());
                } else if (key == GLFW_KEY_LEFT) {
                    Text textSettings;
                    textSettings = GuiMainMenu.getInstance().textSettings;
                    textSettings.setEdgeCharWidth(textSettings.getEdgeCharWidth() - 0.01f);
                    System.out.println("EdgeCharWidth : " + textSettings.getEdgeCharWidth());
                } else if (key == GLFW_KEY_RIGHT) {
                    Text textSettings;
                    textSettings = GuiMainMenu.getInstance().textSettings;
                    textSettings.setEdgeCharWidth(textSettings.getEdgeCharWidth() + 0.01f);
                    System.out.println("EdgeCharWidth : " + textSettings.getEdgeCharWidth());
                }
            } else if (action == GLFW_RELEASE) {
                if (key == KeyBindings.FORWARD.getKey() || key == KeyBindings.BACKWARD.getKey() ||
                        key == KeyBindings.LEFT.getKey() || key == KeyBindings.RIGHT.getKey()) {
                    if (Game.getInstance().getGameState() == GameState.STARTED)
                        Camera.getInstance().resetMovement();
                }
            }
        });
    }

    public static void freeCallbacks() {
        if (callback != null)
            callback.free();
    }
}
