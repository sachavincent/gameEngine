package inputs;

import static org.lwjgl.glfw.GLFW.*;

import engineTester.Game;
import engineTester.Game.GameState;
import entities.Camera;
import guis.Gui;
import guis.GuiComponent;
import guis.prefabs.GuiEscapeMenu;
import guis.prefabs.GuiItemSelection;
import guis.prefabs.GuiMainMenu.GuiMainMenu;
import guis.presets.GuiTextInput;
import items.RotatableItem;
import java.util.List;
import java.util.stream.Collectors;
import org.lwjgl.system.Callback;
import renderEngine.DisplayManager;
import renderEngine.GuiRenderer;
import renderEngine.MasterRenderer;
import terrains.Terrain;

public class KeyboardUtils {

    private static Callback callback;

    public static void setupListeners() {
        callback = glfwSetKeyCallback(DisplayManager.getWindow(), (w, key, scancode, action, mods) -> {
            List<GuiTextInput> focusedInputs = Game.getInstance().getGuiTextInputs().stream()
                    .filter(GuiComponent::isClicked).collect(Collectors.toList());
            boolean isKeyProcessed = false;
            if (!focusedInputs.isEmpty())
                isKeyProcessed = focusedInputs.stream()
                        .allMatch(guiTextInput -> guiTextInput.processKeyboardInput(action, key));

            if (isKeyProcessed)
                return;

            if (action == GLFW_PRESS) {
                Terrain terrain = Terrain.getInstance();
                switch (key) {
                    case GLFW_KEY_R:
                        terrain.getSelectedItems().stream().filter(RotatableItem.class::isInstance)
                                .forEach(item -> item.setRotation(item.getFacingDirection().getDegree() + 90));
                        break;
                    case GLFW_KEY_ESCAPE:
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
                                        Gui.showGui(guiEscapeMenu);
                                        Game.getInstance().pause();
                                    }
                                    break;
                                case PAUSED:
                                    if (!guiEscapeMenu.isDisplayed() && !isNoGuiDisplayed) { // Sub-menu opened
                                        Gui lastGuiOpened = displayedGuis.get(displayedGuis.size() - 1);
                                        lastGuiOpened.setDisplayed(false);
                                        displayedGuis.remove(lastGuiOpened);
                                        if (Game.getInstance().getDisplayedGuis().isEmpty())
                                            Gui.showGui(guiEscapeMenu);
                                    } else if (isNoGuiDisplayed ||
                                            Game.getInstance().getGameState() == GameState.PAUSED) {
                                        Gui.hideGui(guiEscapeMenu);
                                        Game.getInstance().resume();
                                    }
                                    break;
                            }
                        }
                        break;
                    case GLFW_KEY_K:
                        if (Game.getInstance().getGameState() == GameState.STARTED)
                            MasterRenderer.getInstance().getItemRenderer().switchDisplayBoundingBoxes();
                        break;
                    case GLFW_KEY_W:
                        if (Game.getInstance().getGameState() == GameState.STARTED)
                            Camera.getInstance().moveTo(Camera.getInstance().getYaw());
                        break;
                    case GLFW_KEY_S:
                        if (Game.getInstance().getGameState() == GameState.STARTED)
                            Camera.getInstance().moveTo(Camera.getInstance().getYaw() + 180);
                        break;
                    case GLFW_KEY_A:
                        if (Game.getInstance().getGameState() == GameState.STARTED)
                            Camera.getInstance().moveTo(Camera.getInstance().getYaw() + 270);
                        break;
                    case GLFW_KEY_D:
                        if (Game.getInstance().getGameState() == GameState.STARTED)
                            Camera.getInstance().moveTo(Camera.getInstance().getYaw() + 90);
                        break;
                    case GLFW_KEY_M:
                        if (Game.getInstance().getGameState() == GameState.STARTED)
                            Gui.toggleGui(GuiItemSelection.getItemSelectionGui());
                        break;
                    case GLFW_KEY_SEMICOLON:
                        GuiRenderer.switchDisplayDebugOutlines();
                        break;
                }
            } else if (action == GLFW_RELEASE) {
                switch (key) {
                    case GLFW_KEY_W:
                    case GLFW_KEY_S:
                    case GLFW_KEY_A:
                    case GLFW_KEY_D:
                        if (Game.getInstance().getGameState() == GameState.STARTED)
                            Camera.getInstance().resetMovement();
                        break;
                }
            }
        });
    }

    public static void freeCallbacks() {
        if (callback != null)
            callback.free();
    }

}
