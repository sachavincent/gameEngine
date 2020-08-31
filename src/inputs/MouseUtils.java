package inputs;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static renderEngine.DisplayManager.getWindow;
import static util.math.Maths.isPointIn2DBounds;

import abstractItem.AbstractItem;
import entities.Camera;
import guis.Gui;
import guis.GuiComponent;
import guis.basics.GuiEllipse;
import guis.prefabs.GuiSelectedItem;
import items.Item;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.Callback;
import renderEngine.DisplayManager;
import renderEngine.GuiRenderer;
import terrains.Terrain;
import util.MousePicker;
import util.math.Maths;
import util.math.Vector2f;
import util.math.Vector3f;

public class MouseUtils {

    public static boolean middleMouseButtonPressed;
    public static int     previousYaw;
    public static float   previousPitch;
    public static double  previousXPos = -1;
    public static double  previousYPos = -1;

    private static Callback callback1;
    private static Callback callback2;
    private static Callback callback3;

    public static Vector2f getCursorPos() {
        long windowID = DisplayManager.getWindow();
        DoubleBuffer posX = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer posY = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(windowID, posX, posY);

        Vector2f cursorPos = new Vector2f(posX.get(), posY.get());

        posX.clear();
        posY.clear();

        cursorPos.x /= DisplayManager.WIDTH;
        cursorPos.x *= 2;
        cursorPos.x -= 1;

        cursorPos.x = Maths.clamp(cursorPos.x, -1, 1);
        cursorPos.y /= DisplayManager.HEIGHT;
        cursorPos.y *= -2;
        cursorPos.y += 1;
        cursorPos.y = Maths.clamp(cursorPos.y, -1, 1);

        return cursorPos;
    }

    public static boolean isCursorInGui(Gui gui) {
        return isPointIn2DBounds(getCursorPos(), gui.getX(), gui.getY(), gui.getWidth(), gui.getHeight());
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

        return isPointIn2DBounds(getCursorPos(), guiComponent.getX(), guiComponent.getY(), guiComponent.getWidth(),
                guiComponent.getHeight());
    }

    public static void setupListeners() {
        long window = DisplayManager.getWindow();

        final List<Gui> guisClone = new ArrayList<>(GuiRenderer.getInstance().getGuis());

        callback1 = GLFW.glfwSetMouseButtonCallback(getWindow(), (w, button, action, mods) -> {
            boolean inGui = guisClone.stream()
                    .filter(Gui::isDisplayed)
                    .anyMatch(MouseUtils::isCursorInGui);
            if (!inGui)
                onClickOnTerrain(button, action);
            else if (button == GLFW_MOUSE_BUTTON_1) {
                guisClone.stream()
                        .filter(Gui::isDisplayed).forEach(
                        gui -> gui.getAllComponents().stream().filter(GuiComponent::isDisplayed)
                                .filter(MouseUtils::isCursorInGuiComponent)
                                .forEach(guiComponent -> {
                                    switch (action) {
                                        case GLFW_PRESS:
                                            guiComponent.onPress();
                                            break;
                                        case GLFW_RELEASE:
                                            guiComponent.onRelease();
                                            break;
                                    }
                                }));
            }
        });

        callback2 = GLFW.glfwSetScrollCallback(window, (w, xoffset, yoffset) -> {
            if (yoffset > 0)
                Camera.getInstance().moveCloser();
            else if (yoffset < 0)
                Camera.getInstance().moveFurther();
        });

        callback3 = GLFW.glfwSetCursorPosCallback(window, (w, xPos, yPos) -> {
            boolean inGui = guisClone.stream()
                    .filter(Gui::isDisplayed)
                    .anyMatch(MouseUtils::isCursorInGui);

            Terrain terrain = Terrain.getInstance();
            GuiSelectedItem selectedItemGui = GuiSelectedItem.getSelectedItemGui();

            if (!selectedItemGui.isDisplayed() &&
                    selectedItemGui.getSelectedItem() != null)
                Gui.showGui(selectedItemGui);

            terrain.removeItem(terrain.getPreviewItemPosition());
            terrain.resetPreviewItem();
            selectedItemGui.updatePosition();

            if (!inGui) {
                MousePicker picker = MousePicker.getInstance();
                picker.update();

                Vector3f point = picker.getCurrentTerrainPoint();
                if (point == null)
                    return;

                Vector2f terrainPoint = new Vector2f(point.getX(), point.getZ());
//                terrain.removeItem(terrain.getPreviewItemPosition());
//                terrain.resetPreviewItem();

                terrainPoint.x = (float) Math.rint(terrainPoint.x);
                terrainPoint.y = (float) Math.rint(terrainPoint.y);

                if (terrain.getItems().get(terrainPoint) == null) {
                    if (selectedItemGui.isDisplayed())
                        Gui.hideGui(selectedItemGui);

                    AbstractItem selectedItem = selectedItemGui.getSelectedItem();
                    if (selectedItem == null)
                        Gui.hideGui(selectedItemGui);
                    else
                        terrain.setPreviewItem(terrainPoint, selectedItem);
                } else {
                    if (!selectedItemGui.isDisplayed())
                        Gui.showGui(selectedItemGui);
                }

                Camera camera = Camera.getInstance();
                if (middleMouseButtonPressed) {
                    if (previousXPos == -1 && previousYPos == -1) { // Premier mvt depuis que middle click a été appuyé
                        previousXPos = xPos;
                        previousYPos = yPos;

                        previousYaw = camera.getYaw();
                        previousPitch = camera.getPitch();
                    } else {
                        float xOffset = (float) (previousXPos - xPos) / (float) DisplayManager.WIDTH * 360;
                        float yOffset = (float) (previousYPos - yPos) / (float) DisplayManager.HEIGHT * 360;
                        //TODO: Sensitivity

                        camera.setYaw((int) (previousYaw - xOffset));
                        camera.setPitch(previousPitch - yOffset);
                        camera.setFocusPoint(MousePicker.getInstance().getCurrentTerrainPoint());
                        if (camera.getFocusPoint() != null) {
                            camera.setDistanceFromTerrainPoint(
                                    (float) camera.getFocusPoint()
                                            .distance(camera.getPosition()));
//                        camera.setDistanceFromTerrainPointChanged(true);
                        }
                    }
                }
            } else {
//                Gui.hideGui(GuiSelectedItem.getSelectedItemGui());

                guisClone.forEach(gui -> {
                    gui.getComponents().keySet().stream()
                            .filter(gui::areTransitionsOfComponentDone)
                            .forEach(GuiComponent::onLeave);
                });

                guisClone.stream()
                        .filter(Gui::isDisplayed)
                        .filter(MouseUtils::isCursorInGui)
                        .forEach(gui -> {
                            gui.getComponents().keySet().stream()
                                    .filter(MouseUtils::isCursorInGuiComponent)
                                    .filter(gui::areTransitionsOfComponentDone)
                                    .forEach(guiComponent -> {
                                        guiComponent.onHover();
                                        guiComponent.onEnter();
                                    });
                        });
            }
        });
    }

    private static void onClickOnTerrain(int button, int action) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
            MousePicker picker = MousePicker.getInstance();
            Terrain terrain = Terrain.getInstance();
            if (action == GLFW_PRESS) {
                picker.update();
                if (picker.getCurrentTerrainPoint() == null)
                    return;
                Vector2f currentTerrainPoint = new Vector2f(picker.getCurrentTerrainPoint().getX(),
                        picker.getCurrentTerrainPoint().getZ());

//                    currentTerrainPoint.x = 2 * (float) Math.floor(currentTerrainPoint.x / 2) + 1;
//                    currentTerrainPoint.y = 2 * (float) Math.floor(currentTerrainPoint.y / 2) + 1;
                currentTerrainPoint.x = (float) Math.rint(currentTerrainPoint.x);
                currentTerrainPoint.y = (float) Math.rint(currentTerrainPoint.y);
//                            entities.add(new Entity(dirtRoad.getTexture(), currentTerrainPoint, 0,
//                            entities.add(new Entity(dirtRoad.getTexture(), currentTerrainPoint, 0,
//                                    90, 0, 1f, 3));
//                            System.out.println(currentTerrainPoint);
//                            System.out.println("Player: " + player.getPosition());
//                            AbstractInsula abstractInsula2 = new AbstractInsula();
//                            terrain.placeItem(new AbstractDirtRoadItem(), currentTerrainPoint);
//                            terrain.placeItem(abstractInsula2, currentTerrainPoint);
                terrain.placeItem(GuiSelectedItem.getSelectedItemGui().getSelectedItem(), currentTerrainPoint);
            }
        } else if (button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
            onMiddleMouseButton(action);
        } else if (button == GLFW_MOUSE_BUTTON_2) {
            MousePicker picker = MousePicker.getInstance();
            Terrain terrain = Terrain.getInstance();
            if (action == GLFW_PRESS) {
                picker.update();
                for (Entry<Vector2f, Item> entry : terrain.getItems().entrySet()) {
                    Vector2f pos = entry.getKey();
                    Item item = entry.getValue();

                    Vector3f min = item.getBoundingBox().getRawModel().getMin();
                    Vector3f max = item.getBoundingBox().getRawModel().getMax();

                    float minX = min.x;
                    float minY = min.y;
                    float minZ = min.z;

                    float maxX = max.x;
                    float maxY = max.y;
                    float maxZ = max.z;

                    Vector3f[] rec1 = new Vector3f[4];
                    Vector3f[] rec2 = new Vector3f[4];

                    rec1[0] = new Vector3f(minX + pos.x, minY, minZ + pos.y);
                    rec1[1] = new Vector3f(minX + pos.x, maxY, minZ + pos.y);
                    rec1[2] = new Vector3f(minX + pos.x, minY, maxZ + pos.y);
                    rec1[3] = new Vector3f(minX + pos.x, maxY, maxZ + pos.y);

                    rec2[0] = new Vector3f(maxX + pos.x, minY, minZ + pos.y);
                    rec2[1] = new Vector3f(maxX + pos.x, maxY, minZ + pos.y);
                    rec2[2] = new Vector3f(maxX + pos.x, minY, maxZ + pos.y);
                    rec2[3] = new Vector3f(maxX + pos.x, maxY, maxZ + pos.y);

                    Vector3f p = Maths.temp(rec1, rec2, Camera.getInstance().getPosition(), picker);

                    if (p == null) {
                        rec1[0] = new Vector3f(minX + pos.x, minY, maxZ + pos.y);
                        rec1[1] = new Vector3f(minX + pos.x, maxY, maxZ + pos.y);
                        rec1[2] = new Vector3f(maxX + pos.x, minY + pos.y, maxZ + pos.y);
                        rec1[3] = new Vector3f(maxX + pos.x, maxY + pos.y, maxZ + pos.y);

                        rec2[0] = new Vector3f(minX + pos.x, minY, minZ + pos.y);
                        rec2[1] = new Vector3f(minX + pos.x, maxY, minZ + pos.y);
                        rec2[2] = new Vector3f(maxX + pos.x, minY, minZ + pos.y);
                        rec2[3] = new Vector3f(maxX + pos.x, maxY, minZ + pos.y);
                        //Todo pour y, ajouter la hauteur du terrain sur l'item, pour l'instant 0

                        p = Maths.temp(rec1, rec2, Camera.getInstance().getPosition(), picker);
                    }

                    if (p != null) {
                        if (!item.isSelected())
                            item.select();
                        else
                            item.unselect();

                        System.out.println("Intersection avec " + item.getId());

                        break;
                    }
                }
            }
        }
    }

    public static void onMiddleMouseButton(int action) {
        switch (action) {
            case GLFW_PRESS:
                middleMouseButtonPressed = true;
                break;
            case GLFW_RELEASE:
                middleMouseButtonPressed = false;
                previousXPos = -1;
                previousYPos = -1;
                break;
        }
    }

    public static void freeCallbacks() {
        if (callback1 != null)
            callback1.free();
        if (callback2 != null)
            callback2.free();
        if (callback3 != null)
            callback3.free();
    }
}
