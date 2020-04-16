package inputs;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_H;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_K;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;

import guis.Gui;
import guis.GuiEscapeMenu;
import java.util.List;
import renderEngine.DisplayManager;
import renderEngine.MasterRenderer;

public class KeyboardUtils {

    public static void setupListeners(List<Gui> guis) {
        long window = DisplayManager.getWindow();

        glfwSetKeyCallback(window, (w, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS) {
                switch (key) {
                    case GLFW_KEY_H:
                        guis.forEach(Gui::showGui);
                        break;
                    case GLFW_KEY_ESCAPE:
                        Gui.showGui(GuiEscapeMenu.getEscapeMenu());
                        break;
                    case GLFW_KEY_K:
                        MasterRenderer.getInstance().getEntityRenderer().switchDisplayBoundingBoxes();
                        break;
                }
            }
        });
    }
}
