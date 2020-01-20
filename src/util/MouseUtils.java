package util;

import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static util.Maths.isPosInBounds;

import entities.Camera;
import guis.Gui;
import guis.GuiComponent;
import guis.GuiEscapeMenu;
import guis.basics.GuiEllipse;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import renderEngine.DisplayManager;
import util.math.Vector2f;

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


    public static boolean isCursorInGuiComponent(GuiComponent<?> guiComponent) {
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

    public static void setupListeners(final List<Gui> guis, Camera camera) {
        long window = DisplayManager.getWindow();

        final List<Gui> guisClone = new ArrayList<>(guis);

        guisClone.add(GuiEscapeMenu.getEscapeMenu());

//        GLFW.glfwSetMouseButtonCallback(window, (w, button, action, mods) -> {
//            if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
//                if (action == GLFW.GLFW_PRESS) {
//                    guisClone.stream()
//                            .filter(Gui::isDisplayed)
//                            .filter(MouseUtils::isCursorInGui)
//                            .forEach(gui -> {
//                                gui.getComponents().keySet().stream()
//                                        .filter(MouseUtils::isCursorInGuiComponent)
//                                        .filter(gui::areTransitionsOfComponentDone)
//                                        .forEach(GuiComponent::onClick);
//                            });
//                    System.out.println(MousePicker.getInstance().getCurrentTerrainPoint());
//                } else if (action == GLFW_RELEASE) {
//                    guisClone.forEach(gui -> {
//                        List<GuiComponent<?>> guiComponents = gui.getComponents().keySet()
//                                .stream().filter(GuiComponent::isClicked).collect(Collectors.toList());
//
//
//                        if (gui.isDisplayed() && MouseUtils.isCursorInGui(gui)) {
//                            guiComponents.stream()
////                                    .filter(guiComponent -> guiComponent.isClicked())
//                                    .filter(MouseUtils::isCursorInGuiComponent)
//                                    .filter(gui::areTransitionsOfComponentDone)
//                                    .forEach(GuiComponent::onPress);
//                        }
//
//                        guiComponents.forEach(GuiComponent::onRelease);
//                    });
//                }
//            } else if (button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
//                if (action == GLFW.GLFW_PRESS) {
//                    camera.setMiddleButtonPressed(true);
//                    System.out.println("middle button");
//                } else if (action == GLFW.GLFW_RELEASE) {
//                    camera.setMiddleButtonPressed(false);
//                }
//            }
//        });


//        GLFW.glfwSetScrollCallback(window, (w, xoffset, yoffset) -> {
//            camera.getPosition().x -= 1f;
//        });

        GLFW.glfwSetKeyCallback(window, (w, key, scancode, action, mods) -> {
            Vector2f cursorPos = getCursorPos();

        });

//        GLFW.glfwSetCursorPosCallback(window, (w, button, action) -> {
//            guisClone.forEach(gui -> {
//                gui.getComponents().keySet().stream()
//                        .filter(gui::areTransitionsOfComponentDone)
//                        .forEach(GuiComponent::onLeave);
//            });
//
//            guisClone.stream()
//                    .filter(Gui::isDisplayed)
//                    .filter(MouseUtils::isCursorInGui)
//                    .forEach(gui -> {
//                        gui.getComponents().keySet().stream()
//                                .filter(MouseUtils::isCursorInGuiComponent)
//                                .filter(gui::areTransitionsOfComponentDone)
//                                .forEach(guiComponent -> {
//                                    guiComponent.onHover();
//                                    guiComponent.onEnter();
//                                });
//                    });
//        });
    }
}
