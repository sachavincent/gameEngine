package inputs.callbacks;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_H;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;

import guis.Gui;
import java.util.List;
import renderEngine.DisplayManager;

public class KeyboardUtils {

    public static void setupListeners(List<Gui> guis) {
        long window = DisplayManager.getWindow();

        glfwSetKeyCallback(window, (w, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_H && action == GLFW_PRESS) {
                guis.forEach(gui -> {
                    if(gui.isDisplayed())
                        gui.hide();
                    else
                        gui.show();
                });
            }
        });
    }
}
