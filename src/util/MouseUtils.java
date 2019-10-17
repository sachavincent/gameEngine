package util;

import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;

import guis.Gui;
import guis.components.GuiComponent;
import java.nio.DoubleBuffer;
import java.util.List;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import renderEngine.DisplayManager;
import util.vector.Vector2f;

public class MouseUtils {

    public static Vector2f getCursorPos() {
        long windowID = DisplayManager.getWindow();
        DoubleBuffer posX = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer posY = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(windowID, posX, posY);

        return new Vector2f(posX.get(), posY.get());
    }

    public static boolean isCursorInGui(Vector2f cursorPos, Gui gui) {
        return isPosInBounds(cursorPos, gui.getX(), gui.getY(), gui.getWidth(), gui.getHeight());
    }

    private static boolean isPosInBounds(Vector2f cursorPos, float x, float y, float width, float height) {

        return (x - width) < cursorPos.x && cursorPos.x < (x + width) &&
                (y - height) < cursorPos.y && cursorPos.y < (y + height);
    }

    private static boolean isCursorInGuiComponent(Vector2f cursorPos, GuiComponent guiComponent) {
//        System.out.println(guiComponent);
        return isPosInBounds(cursorPos, guiComponent.getX(), guiComponent.getY(), guiComponent.getWidth(),
                guiComponent.getHeight());
    }

    public static void setupListeners(List<Gui> guis) {
        long window = DisplayManager.getWindow();
        GLFW.glfwSetMouseButtonCallback(window, (w, button, action, mods) -> {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {
                Vector2f cursorPos = getCursorPos();
                cursorPos.x /= DisplayManager.WIDTH;
                cursorPos.x *= 2;
                cursorPos.x -= 1;

                cursorPos.y /= DisplayManager.HEIGHT;
                cursorPos.y *= -2;
                cursorPos.y += 1;

                guis.stream()
                        .filter(Gui::isDisplayed)
                        .filter(gui -> isCursorInGui(cursorPos, gui))
                        .forEach(gui -> gui.getComponents().stream()
                                .filter(guiComponent -> isCursorInGuiComponent(cursorPos, guiComponent))
                                .forEach(GuiComponent::onClick));
            }
        });

        GLFW.glfwSetScrollCallback(window, (w, xoffset, yoffset) -> {
            Vector2f cursorPos = getCursorPos();

        });

        GLFW.glfwSetKeyCallback(window, (w, key, scancode, action, mods) -> {
            Vector2f cursorPos = getCursorPos();

        });

        GLFW.glfwSetCursorPosCallback(window, (w, button, action) -> {
            Vector2f cursorPos = getCursorPos();

            guis.stream().filter(Gui::isDisplayed)
                    .forEach(gui -> {
                        if (isCursorInGui(cursorPos, gui)) {
                            gui.getComponents().stream()
                                    .filter(guiComponent -> isCursorInGuiComponent(cursorPos, guiComponent))
                                    .forEach(GuiComponent::onHover);
                        }

                        gui.setFocused(isCursorInGui(cursorPos, gui));
                    });
        });
    }
}
