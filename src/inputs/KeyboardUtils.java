package inputs;

import static org.lwjgl.glfw.GLFW.*;

import engineTester.Game;
import engineTester.Game.GameState;
import entities.Camera;
import fontMeshCreator.Text;
import guis.Gui;
import guis.GuiComponent;
import guis.prefabs.GuiEscapeMenu;
import guis.prefabs.GuiItemSelection;
import guis.prefabs.GuiMainMenu.GuiMainMenu;
import guis.presets.GuiTextInput;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import org.lwjgl.system.Callback;
import renderEngine.DisplayManager;
import renderEngine.GuiRenderer;

public class KeyboardUtils {

    private static Callback callback;

    private static final Queue<Request> requests = new LinkedList<>();

    public static void cancelRequest(Request request) {
        requests.remove(request);
    }

    public static void request(Request request) {
        requests.add(request);
    }

    private static boolean handleRequests(int action, int key) {
        if (requests.isEmpty())
            return false;

        Request request = requests.element();

        if (request != null) {
            request.getHecklingCallback().onKeyRequest(action, key);
            if (request.getHecklingCallback().onKeyRequest(action, key)) {
                requests.remove();
            }

            return true;
        }
        return false;
    }

    public static void setupListeners() {
        callback = glfwSetKeyCallback(DisplayManager.getWindow(), (w, key, scancode, action, mods) -> {
            boolean handled = handleRequests(action, key);
            if(handled)
                return;

            List<GuiTextInput> focusedInputs = Game.getInstance().getGuiTextInputs().stream()
                    .filter(GuiComponent::isClicked).collect(Collectors.toList());
            boolean isKeyProcessed = false;
            if (!focusedInputs.isEmpty())
                isKeyProcessed = focusedInputs.stream()
                        .allMatch(guiTextInput -> guiTextInput.processKeyboardInput(action, key));

            if (isKeyProcessed)
                return;

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
