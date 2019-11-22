package util;

import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static util.Maths.isPosInBounds;

import guis.Gui;
import guis.GuiComponent;
import guis.basics.GuiEllipse;
import java.nio.DoubleBuffer;
import java.util.List;
import java.util.stream.Collectors;
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

        Vector2f cursorPos = new Vector2f(posX.get(), posY.get());

        cursorPos.x /= DisplayManager.WIDTH;
        cursorPos.x *= 2;
        cursorPos.x -= 1;

        cursorPos.y /= DisplayManager.HEIGHT;
        cursorPos.y *= -2;
        cursorPos.y += 1;

        return cursorPos;
    }

    public static boolean isCursorInGui(Gui gui) {
        return isPosInBounds(getCursorPos(), gui.getX(), gui.getY(), gui.getWidth(), gui.getHeight());
    }


    public static boolean isCursorInGuiComponent(GuiComponent guiComponent) {
        if (guiComponent instanceof GuiEllipse) {
            Vector2f cursorPos = getCursorPos();

            float x = cursorPos.x;
            float y = cursorPos.y;

            float h = guiComponent.getX();
            float k = guiComponent.getY();
            float r1 = guiComponent.getWidth();
            float r2 = guiComponent.getHeight();


            return (Math.pow(x - h, 2) / Math.pow(r1, 2) +
                    Math.pow(y - k, 2) / Math.pow(r2, 2)) <= 1;
        }

        return isPosInBounds(getCursorPos(), guiComponent.getX(), guiComponent.getY(), guiComponent.getWidth(),
                guiComponent.getHeight());
    }

    public static void setupListeners(List<Gui> guis) {
        long window = DisplayManager.getWindow();
        GLFW.glfwSetMouseButtonCallback(window, (w, button, action, mods) -> {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
                if (action == GLFW.GLFW_PRESS) {
                    guis.stream()
                            .filter(Gui::isDisplayed)
                            .filter(MouseUtils::isCursorInGui)
                            .forEach(gui -> gui.getComponents().stream()
                                    .filter(MouseUtils::isCursorInGuiComponent)
                                    .forEach(GuiComponent::onClick));
                } else if (action == GLFW_RELEASE) {

                    guis.forEach(gui -> {
                        List<GuiComponent> guiComponents = gui.getComponents()
                                .stream().filter(GuiComponent::isClicked).collect(Collectors.toList());


                        if (gui.isDisplayed() && MouseUtils.isCursorInGui(gui)) {
                            gui.getComponents().stream()
                                    .filter(guiComponent -> !guiComponent.isClicked())
                                    .filter(MouseUtils::isCursorInGuiComponent)
                                    .forEach(GuiComponent::onRelease);
                        }

                        guiComponents.forEach(GuiComponent::onRelease);
                    });
                }
            }
        });

        GLFW.glfwSetScrollCallback(window, (w, xoffset, yoffset) -> {
            Vector2f cursorPos = getCursorPos();

        });

        GLFW.glfwSetKeyCallback(window, (w, key, scancode, action, mods) -> {
            Vector2f cursorPos = getCursorPos();

        });

        GLFW.glfwSetCursorPosCallback(window, (w, button, action) -> {
            if (guis == null)
                return;

            guis.forEach(gui -> {
                List<GuiComponent> components = gui.getComponents();
                if (components != null)
                    components.forEach(GuiComponent::onLeave);
            });

            guis.stream()
                    .filter(Gui::isDisplayed)
                    .filter(MouseUtils::isCursorInGui)
                    .forEach(gui -> {
                        List<GuiComponent> components = gui.getComponents();
                        if (components != null)
                            components.stream()
                                    .filter(MouseUtils::isCursorInGuiComponent)
                                    .forEach(guiComponent -> {
                                        guiComponent.onHover();
                                        guiComponent.onEnter();
                                    });
                    });
        });
    }
}
