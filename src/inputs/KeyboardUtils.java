package inputs;

import static org.lwjgl.glfw.GLFW.*;

import engineTester.Game;
import entities.Camera;
import guis.Gui;
import guis.prefabs.GuiEscapeMenu;
import guis.prefabs.GuiItemSelection;
import guis.prefabs.GuiMainMenu.GuiMainMenu;
import items.RotatableItem;
import org.lwjgl.system.Callback;
import renderEngine.DisplayManager;
import renderEngine.GuiRenderer;
import renderEngine.MasterRenderer;
import terrains.Terrain;

public class KeyboardUtils {

    private static Callback callback;

    public static void setupListeners() {

        callback = glfwSetKeyCallback(DisplayManager.getWindow(), (w, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS) {

                Terrain terrain = Terrain.getInstance();
                switch (key) {
                    case GLFW_KEY_R:
                        terrain.getSelectedItems().stream().filter(RotatableItem.class::isInstance)
                                .forEach(item -> item.setRotation(item.getFacingDirection().getDegree() + 90));
                        break;
                    case GLFW_KEY_ESCAPE:
                        if (Game.getInstance().isStarted())
                            Gui.toggleGui(GuiEscapeMenu.getEscapeMenu());
                        else {
                            GuiMainMenu.getInstance().back();
                        }
                        break;
                    case GLFW_KEY_K:
                        if (Game.getInstance().isStarted())
                            MasterRenderer.getInstance().getItemRenderer().switchDisplayBoundingBoxes();
                        break;
                    case GLFW_KEY_W:
                        if (Game.getInstance().isStarted())
                            Camera.getInstance().moveTo(Camera.getInstance().getYaw());
                        break;
                    case GLFW_KEY_S:
                        if (Game.getInstance().isStarted())
                            Camera.getInstance().moveTo(Camera.getInstance().getYaw() + 180);
                        break;
                    case GLFW_KEY_A:
                        if (Game.getInstance().isStarted())
                            Camera.getInstance().moveTo(Camera.getInstance().getYaw() + 270);
                        break;
                    case GLFW_KEY_D:
                        if (Game.getInstance().isStarted())
                            Camera.getInstance().moveTo(Camera.getInstance().getYaw() + 90);
                        break;
                    case GLFW_KEY_M:
                        if (Game.getInstance().isStarted())
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
                        if (Game.getInstance().isStarted())
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
