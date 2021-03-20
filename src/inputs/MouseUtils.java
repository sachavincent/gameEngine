package inputs;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static renderEngine.DisplayManager.getWindow;
import static util.math.Maths.isPointIn2DBounds;

import engineTester.Game;
import engineTester.Game.GameState;
import entities.Camera;
import guis.Gui;
import guis.GuiComponent;
import guis.basics.GuiEllipse;
import guis.basics.GuiTriangle;
import guis.prefabs.GuiSelectedItem;
import guis.presets.buttons.GuiAbstractButton;
import items.Item;
import items.SelectableItem;
import items.abstractItem.AbstractItem;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import models.BoundingBox;
import models.RawModel;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.Callback;
import pathfinding.Road;
import pathfinding.RouteFinder;
import pathfinding.RouteFinder.Route;
import renderEngine.DisplayManager;
import renderEngine.FrustumCullingFilter;
import terrains.Terrain;
import terrains.TerrainPosition;
import util.MousePicker;
import util.math.Maths;
import util.math.Plane3D;
import util.math.Vector2f;
import util.math.Vector3f;

public class MouseUtils {

    // Represents the position clicked before releasing M1
    private static TerrainPosition startTerrainPos;
    private static TerrainPosition lastTerrainPos;

    public static boolean middleMouseButtonPressed;
    public static int     previousYaw;
    public static float   previousPitch;
    public static double  previousXPos = -1;
    public static double  previousYPos = -1;

    private static Callback callback1;
    private static Callback callback2;
    private static Callback callback3;

    private static State state = State.DEFAULT;

    private static final Terrain terrain = Terrain.getInstance();

    private static final List<GuiComponent>      ENTERED_GUI_COMPONENTS = new ArrayList<>();
    private static       List<GuiAbstractButton> buttons;

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
        if (gui instanceof GuiSelectedItem) // Doesn't make sense
            return false;

        boolean res = isPointIn2DBounds(getCursorPos(), gui.getX(), gui.getY(), gui.getWidth(), gui.getHeight());

        if (!res)
            return gui.getAllComponents().stream()
                    .filter(GuiComponent::isDisplayed)
                    .anyMatch(MouseUtils::isCursorInGuiComponent);

        return true;
    }

    public static boolean isCursorInGuiComponent(GuiComponent guiComponent) {
        return isCursorInGuiComponent(guiComponent, false);
    }

    public static boolean isCursorInGuiComponent(GuiComponent guiComponent, boolean print) {
        Vector2f cursorPos = getCursorPos();
        if (guiComponent instanceof GuiEllipse) {
            float x = cursorPos.x;
            float y = cursorPos.y;

            float h = guiComponent.getX();
            float k = guiComponent.getY();
            float r1 = guiComponent.getWidth();
            float r2 = guiComponent.getHeight();


            return (Math.pow(x - h, 2) / Math.pow(r1, 2) +
                    Math.pow(y - k, 2) / Math.pow(r2, 2)) <= 1;
        }

        if (guiComponent instanceof GuiTriangle) {
            GuiTriangle guiTriangle = (GuiTriangle) guiComponent;
            Vector2f p0, p1, p2;
            float width = guiTriangle.getWidth();
            float height = guiTriangle.getHeight();
            float xCenter = guiTriangle.getX();
            float yCenter = guiTriangle.getY();

            int rotation = guiTriangle.getRotation() * 90;
            switch (rotation) {
                case 0:
                    float yCenterMinusHeight = yCenter - height;
                    p0 = new Vector2f(xCenter - width, yCenterMinusHeight);
                    p1 = new Vector2f(xCenter, yCenter + height);
                    p2 = new Vector2f(xCenter + width, yCenterMinusHeight);
                    break;
                case 90:
                    float xCenterMinusWidth = xCenter - width;
                    p0 = new Vector2f(xCenterMinusWidth, yCenter - height);
                    p1 = new Vector2f(xCenterMinusWidth, yCenter + height);
                    p2 = new Vector2f(xCenter + width, yCenter);
                    break;
                case 180:
                    float yCenterPlusHeight = yCenter + height;
                    p0 = new Vector2f(xCenter - width, yCenterPlusHeight);
                    p1 = new Vector2f(xCenter + width, yCenterPlusHeight);
                    p2 = new Vector2f(xCenter, yCenter - height);
                    break;
                case 270:
                    float xCenterPlusWidth = xCenter + width;
                    p0 = new Vector2f(xCenterPlusWidth, yCenter - height);
                    p1 = new Vector2f(xCenter - width, yCenter);
                    p2 = new Vector2f(xCenterPlusWidth, yCenter + height);
                    break;
                default:
                    return false;
            }
            if (print) {
                System.out.println(p0);
                System.out.println(p1);
                System.out.println(p2);
            }
            var dX = cursorPos.x - p2.x;
            var dY = cursorPos.y - p2.y;
            var dX21 = p2.x - p1.x;
            var dY12 = p1.y - p2.y;
            var D = dY12 * (p0.x - p2.x) + dX21 * (p0.y - p2.y);
            var s = dY12 * dX + dX21 * dY;
            var t = (p2.y - p0.y) * dX + (p0.x - p2.x) * dY;
            if (D < 0)
                return s <= 0 && t <= 0 && s + t >= D;
            return s >= 0 && t >= 0 && s + t <= D;
        }

        return isPointIn2DBounds(cursorPos, guiComponent.getX(), guiComponent.getY(), guiComponent.getWidth(),
                guiComponent.getHeight());
    }

    public static void setupListeners() {
        long window = getWindow();
        buttons = new ArrayList<>(Game.getInstance().getAllGuis()).stream().map(gui -> gui.getAllComponents().stream()
                .filter(GuiAbstractButton.class::isInstance)
                .map(GuiAbstractButton.class::cast).collect(Collectors.toList())).flatMap(
                Collection::stream).collect(Collectors.toList());

        callback1 = GLFW.glfwSetMouseButtonCallback(getWindow(), (w, button, action, mods) -> {
            boolean inGui = Game.getInstance().getDisplayedGuis().stream().anyMatch(MouseUtils::isCursorInGui);

            switch (button) {
                case GLFW_MOUSE_BUTTON_1:
                    if (!inGui && Game.getInstance().getGameState() == GameState.STARTED)
                        onM1OnTerrain(action);

                    switch (action) {
                        case GLFW_PRESS:
                            if (inGui)
                                new ArrayList<>(Game.getInstance().getDisplayedGuis()).forEach(
                                        gui -> gui.getAllComponents().stream().filter(GuiComponent::isDisplayed)
                                                .filter(GuiAbstractButton.class::isInstance)
                                                .map(GuiAbstractButton.class::cast)
                                                .filter(guiButton -> isCursorInGuiComponent(guiButton.getButtonShape()))
                                                .forEach(GuiAbstractButton::onPress));

                            Game.getInstance().getGuiTextInputs().forEach(guiTextInput -> {
                                if (!MouseUtils.isCursorInGuiComponent(guiTextInput.getOutline()))
                                    guiTextInput.unfocus();
                            });
                            break;
                        case GLFW_RELEASE:
                            buttons.stream().filter(GuiComponent::isClicked).forEach(guiButton -> {
                                if (!guiButton.isReleaseInsideNeeded()
                                        || (GuiComponent.getParentGui(guiButton).isDisplayed() &&
                                        guiButton.isDisplayed() &&
                                        isCursorInGuiComponent(guiButton.getButtonShape())))
                                    guiButton.onRelease();
                                else
                                    guiButton.resetFilter();
                            });
                            break;
                        default:
                            System.err.println("Unknown action (M1)");
                            break;
                    }
                    break;
                case GLFW_MOUSE_BUTTON_2:
                    switch (action) {
                        case GLFW_PRESS:
                            if (Game.getInstance().getGameState() == GameState.STARTED)
                                onM2Pressed();
                            break;
                        case GLFW_RELEASE:
                            if (Game.getInstance().getGameState() == GameState.STARTED)
                                onM2Released();
                            break;
                        default:
                            System.err.println("Unknown action (M2)");
                            break;
                    }
                    break;
                case GLFW_MOUSE_BUTTON_MIDDLE:
                    if (!inGui)
                        onMiddleMouseButton(action);
                    break;
                default:
                    System.err.println("Unknown button");
                    break;
            }
        });

        callback2 = GLFW.glfwSetScrollCallback(window, (w, xoffset, yoffset) -> {
            if (Game.getInstance().getGameState() == GameState.STARTED) {
                if (yoffset > 0)
                    Camera.getInstance().moveCloser();
                else if (yoffset < 0)
                    Camera.getInstance().moveFurther();

                FrustumCullingFilter.updateFrustum();
            }
        });

        callback3 = GLFW.glfwSetCursorPosCallback(window, (w, xPos, yPos) -> onMouseMove(new Vector2f(xPos, yPos)));
    }

    private static void onMouseMove(Vector2f pos) {
        GuiSelectedItem selectedItemGui = GuiSelectedItem.getSelectedItemGui();

        List<Gui> enteredGuis = Game.getInstance().getDisplayedGuis().stream()
                .filter(MouseUtils::isCursorInGui).collect(Collectors.toList());

        boolean inGui = !enteredGuis.isEmpty();

        new ArrayList<>(ENTERED_GUI_COMPONENTS).stream()
                .filter(guiComponent -> !MouseUtils.isCursorInGuiComponent(guiComponent))
                .forEach(guiComponent -> {
                    guiComponent.onLeave();
                    ENTERED_GUI_COMPONENTS.remove(guiComponent);
                });

        boolean gameStarted = Game.getInstance().getGameState() == GameState.STARTED;

        if (!inGui && gameStarted)
            MousePicker.getInstance().update();

        switch (state) {
            case DEFAULT:
                if (gameStarted)
                    Gui.hideGui(selectedItemGui);
                if (!inGui && MousePicker.getInstance().isPointOnTerrain() && gameStarted)
                    onHoverOnTerrain(pos.x, pos.y);
                else if (inGui)
                    updateGuis(enteredGuis);
                break;
            case ROAD:
            case BUILDING:
            case DESTRUCTION:
            case PRESSED_WITH_ROAD:
            case PRESSED_WITH_BUILDING:
            case PRESSED_WITH_DESTRUCTION:
                selectedItemGui.updatePosition();

                if (!inGui && gameStarted) {
                    if (MousePicker.getInstance().isPointOnTerrain())
                        onHoverOnTerrain(pos.x, pos.y);
                    else {
                        Terrain.getInstance().resetPreviewItems();
                        Gui.showGui(selectedItemGui);
                    }
                } else if (inGui) {
                    if (gameStarted) {
                        Gui.showGui(selectedItemGui);
                        terrain.resetPreviewItems();
                    }

                    updateGuis(enteredGuis);
                }
                break;
            default:
                throw new IllegalStateException("Impossible state : " + state.name());
        }
    }

    private static void updateGuis(List<Gui> enteredGuis) {
        List<GuiComponent> toUpdate = enteredGuis.stream()
                .filter(Gui::areTransitionsDone)
                .map(Gui::getAllComponents).flatMap(Collection::stream)
                .filter(GuiComponent::isDisplayed)
                .filter(MouseUtils::isCursorInGuiComponent)
                .collect(Collectors.toList());

        toUpdate.forEach(guiComponent -> {
            if (ENTERED_GUI_COMPONENTS.contains(guiComponent))
                guiComponent.onHover();
            else {
                guiComponent.onEnter();
                ENTERED_GUI_COMPONENTS.add(guiComponent);
            }
        });
    }

    private static void onHoverOnTerrain(double xPos, double yPos) {
        Vector3f point = MousePicker.getInstance().getCurrentTerrainPoint();
        if (point == null)
            return;

        Vector2f p = new Vector2f(point.getX(), point.getZ());

        TerrainPosition terrainPoint = p.toGridCoordinates();

        onTerrainHover(terrainPoint);
        // TODO: UX à finir pour le reste
        if (middleMouseButtonPressed) {
            Camera camera = Camera.getInstance();
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
                camera.setFocusPoint(point);
                if (camera.getFocusPoint() != null) {
                    camera.setDistanceFromTerrainPoint((float) camera.getFocusPoint().distance(camera.getPosition()));
//                        camera.setDistanceFromTerrainPointChanged(true);
                    FrustumCullingFilter.updateFrustum();
                }
            }
        }
    }

    private static void onM1OnTerrain(int action) {
        MousePicker picker = MousePicker.getInstance();
        Vector3f terrainPoint = picker.getCurrentTerrainPoint();
        if (terrainPoint == null)
            return;

        Vector2f currTerrainPoint = new Vector2f(terrainPoint.getX(), terrainPoint.getZ());

        TerrainPosition currentTerrainPoint = currTerrainPoint.toGridCoordinates();

        switch (action) {
            case GLFW_PRESS:
                onM1Pressed(currentTerrainPoint);
                break;
            case GLFW_RELEASE:
                onM1Released();
                break;
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

    /**
     * =SelectBuilding
     */
    public static void setBuildingState() {
        switch (state) {
            case DEFAULT:
            case ROAD:
            case DESTRUCTION:
                state = State.BUILDING;
                break;
            case BUILDING:
                //null
                break;
            case PRESSED_WITH_ROAD:
            case PRESSED_WITH_BUILDING:
            case PRESSED_WITH_DESTRUCTION:
                //impossible
                break;
            default:
                throw new IllegalStateException("Impossible state : " + state.name());
        }
    }

    /**
     * = SelectRoad
     */
    public static void setRoadState() {
        switch (state) {
            case DEFAULT:
            case BUILDING:
            case DESTRUCTION:
                state = State.ROAD;
                break;
            case ROAD:
                //null
                break;
            case PRESSED_WITH_ROAD:
            case PRESSED_WITH_BUILDING:
            case PRESSED_WITH_DESTRUCTION:
                //impossible
                break;
            default:
                throw new IllegalStateException("Impossible state : " + state.name());
        }
    }

    /**
     * = Deselect
     */
    public static void setDefaultState() {
        switch (state) {
            case DEFAULT:
                //null
                break;
            case ROAD:
            case BUILDING:
            case DESTRUCTION:
                state = State.DEFAULT;
                break;
            case PRESSED_WITH_ROAD:
            case PRESSED_WITH_BUILDING:
            case PRESSED_WITH_DESTRUCTION:
                //impossible
                break;
            default:
                throw new IllegalStateException("Impossible state : " + state.name());
        }
    }

    public static void onM1Pressed(TerrainPosition currentTerrainPoint) {
        startTerrainPos = null;
        lastTerrainPos = null;
        if (terrain.isPositionOccupied(currentTerrainPoint)) {
            Set<Item> items = terrain.getItems().stream().filter(SelectableItem.class::isInstance)
                    .collect(Collectors.toSet());
            for (Item item : items) {
                BoundingBox boundingBox = item.getBoundingBox();
                if (boundingBox == null)
                    continue;

                RawModel boundingBoxRawModel = boundingBox.getRawModel();
                if (boundingBoxRawModel == null)
                    continue;

                TerrainPosition position = item.getPosition();

                boolean found = true;
                for (Plane3D plane3D : boundingBox.getPlanes()) {
                    Plane3D plane = new Plane3D(plane3D);
                    plane.rotate(-item.getFacingDirection().getDegree());

                    plane.add(new Vector3f(position.getX(), 0, position.getZ())); //TODO temp 0

                    found = MousePicker.getInstance().intersectionWithPlane(plane, true) != null;

                    if (found)
                        break;
                }

                if (!found)
                    continue;

                if (!item.isSelected())
                    item.select();
                else
                    item.unselect();

                System.out.println("Intersection avec " + item.getId());
            }
            return;
        }

        switch (state) {
            case DEFAULT:
                //TODO
                break;
            case PRESSED_WITH_ROAD:
            case PRESSED_WITH_BUILDING:
            case PRESSED_WITH_DESTRUCTION:
                // null
                break;
            case ROAD:
                state = State.PRESSED_WITH_ROAD;

                terrain.resetPreviewItems();
                lastTerrainPos = startTerrainPos = currentTerrainPoint;
                terrain.addPreviewItem(currentTerrainPoint);
                break;
            case BUILDING:
                state = State.PRESSED_WITH_BUILDING;

                terrain.resetPreviewItems();
                terrain.addPreviewItem(currentTerrainPoint);
                break;
            case DESTRUCTION:
                //TODO
                break;
            default:
                throw new IllegalStateException("Impossible state : " + state.name());
        }
    }

    private static void onM1Released() {
        switch (state) {
            case DEFAULT:
            case ROAD:
            case BUILDING:
            case DESTRUCTION:
                //null
                break;
            case PRESSED_WITH_ROAD:
                state = State.ROAD;

                terrain.placePreviewItems();
                terrain.resetPreviewItems();

                startTerrainPos = null;
                lastTerrainPos = null;
                break;
            case PRESSED_WITH_BUILDING:
                state = State.BUILDING;

                terrain.placePreviewItems();
                terrain.resetPreviewItems();

                startTerrainPos = null;
                lastTerrainPos = null;
                break;
            case PRESSED_WITH_DESTRUCTION:
                //TODO
                break;
            default:
                throw new IllegalStateException("Impossible state : " + state.name());
        }
    }

    private static void onM2Released() {
        switch (state) {
            case DEFAULT:
                //null
                break;
            case ROAD:
            case BUILDING:
            case DESTRUCTION:
            case PRESSED_WITH_ROAD:
            case PRESSED_WITH_BUILDING:
            case PRESSED_WITH_DESTRUCTION:
                // impossible
                break;
            default:
                throw new IllegalStateException("Impossible state : " + state.name());
        }
    }

    private static void onM2Pressed() {
        switch (state) {
            case DEFAULT:
                //null
                break;
            case ROAD:
            case BUILDING:
            case DESTRUCTION:
            case PRESSED_WITH_ROAD:
            case PRESSED_WITH_BUILDING:
            case PRESSED_WITH_DESTRUCTION:
                state = State.DEFAULT;

                GuiSelectedItem.getSelectedItemGui().removeSelectedItem();
                Terrain.getInstance().resetPreviewItems();
                break;
            default:
                throw new IllegalStateException("Impossible state : " + state.name());
        }
    }

    public static void onTerrainHover(TerrainPosition terrainPoint) {
        GuiSelectedItem selectedItemGui = GuiSelectedItem.getSelectedItemGui();
        AbstractItem selectedItem = terrain.getPreviewedItem();

        switch (state) {
            case DEFAULT:
            case DESTRUCTION:
                //null
                break;
            case ROAD: // Previews only this road
            case BUILDING: // Previews only this building
            case PRESSED_WITH_BUILDING:
                if (terrain.canItemBePlaced(selectedItem, terrainPoint)) {
                    if (selectedItemGui.isDisplayed())
                        Gui.hideGui(selectedItemGui);

                    terrain.resetPreviewItems();
                    terrain.addPreviewItem(terrainPoint);
                } else {
                    terrain.resetPreviewItems();
                    selectedItemGui.updatePosition();
                    Gui.showGui(selectedItemGui);
                }

                break;
            case PRESSED_WITH_ROAD:
                if (terrain.canItemBePlaced(selectedItem, terrainPoint)) {
                    if (selectedItemGui.isDisplayed())
                        Gui.hideGui(selectedItemGui);

                    terrain.resetPreviewItems();
                    lastTerrainPos = terrainPoint;
                    RouteFinder routeFinder = new RouteFinder(terrain.getRoadGraph());
                    Route unobstructedRouteV1 = routeFinder.findUnobstructedRouteV1(startTerrainPos, lastTerrainPos);
                    unobstructedRouteV1.getAllRoads().stream().map(Road::getPosition).forEach(terrain::addPreviewItem);
                } else {
                    terrain.resetPreviewItems();
                    selectedItemGui.updatePosition();
                    Gui.showGui(selectedItemGui);
                }
                break;
            case PRESSED_WITH_DESTRUCTION:
                //TODO
                break;
            default:
                throw new IllegalStateException("Impossible state : " + state.name());
        }
    }


    private enum State {
        DEFAULT,
        ROAD,
        BUILDING,
        PRESSED_WITH_ROAD,
        PRESSED_WITH_BUILDING,
        DESTRUCTION,
        PRESSED_WITH_DESTRUCTION
    }
}
