package inputs;

import static org.lwjgl.glfw.GLFW.*;

import entities.Camera;
import guis.Gui;
import guis.GuiEscapeMenu;
import items.RotatableItem;
import java.util.List;
import org.lwjgl.system.Callback;
import renderEngine.DisplayManager;
import renderEngine.MasterRenderer;
import terrains.Terrain;

public class KeyboardUtils {

    private static Callback callback;

    public static void setupListeners(List<Gui> guis) {
        long window = DisplayManager.getWindow();

        callback = glfwSetKeyCallback(window, (w, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS) {
                Terrain terrain = Terrain.getInstance();
                switch (key) {
                    case GLFW_KEY_H:
                        guis.forEach(Gui::showGui);
                        break;
                    case GLFW_KEY_R:
                        terrain.getSelectedItems().stream().filter(RotatableItem.class::isInstance)
                                .forEach(item -> item.setRotation(item.getFacingDirection().getDegree() + 90));
                        break;
                    case GLFW_KEY_ESCAPE:
                        Gui.showGui(GuiEscapeMenu.getEscapeMenu());
                        break;
                    case GLFW_KEY_K:
                        MasterRenderer.getInstance().getEntityRenderer().switchDisplayBoundingBoxes();
                        break;
                    case GLFW_KEY_W:
                        Camera.getInstance().moveTo(Camera.getInstance().getYaw());
                        break;
                    case GLFW_KEY_S:
                        Camera.getInstance().moveTo(Camera.getInstance().getYaw() + 180);
                        break;
                    case GLFW_KEY_A:
                        Camera.getInstance().moveTo(Camera.getInstance().getYaw() + 270);
                        break;
                    case GLFW_KEY_D:
                        Camera.getInstance().moveTo(Camera.getInstance().getYaw() + 90);
                        break;
                }
            } else if (action == GLFW_RELEASE) {
                switch (key) {
                    case GLFW_KEY_W:
                    case GLFW_KEY_S:
                    case GLFW_KEY_A:
                    case GLFW_KEY_D:
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
