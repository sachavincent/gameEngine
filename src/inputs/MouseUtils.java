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
import guis.prefabs.GuiDebug;
import guis.prefabs.GuiSelectedItem;
import guis.presets.GuiAbstractShapePreset;
import guis.presets.GuiClickablePreset;
import guis.presets.GuiPreset;
import guis.presets.GuiTextInput;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.Callback;
import pathfinding.Path;
import pathfinding.PathFinder;
import pathfinding.Road;
import renderEngine.DisplayManager;
import renderEngine.FrustumCullingFilter;
import scene.Scene;
import scene.components.RepleacableComponent;
import scene.components.SelectableComponent;
import scene.components.TerrainComponent;
import scene.gameObjects.GameObject;
import scene.gameObjects.Player;
import terrains.TerrainPosition;
import util.MousePicker;
import util.math.Maths;
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

    private static final Scene scene = Scene.getInstance();

    public static final List<GuiComponent>       ENTERED_GUI_COMPONENTS = new ArrayList<>();
    private static      List<GuiClickablePreset> clickables;

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
        clickables = new ArrayList<>(Game.getInstance().getAllGuis()).stream()
                .map(gui -> gui.getAllComponents().stream()
                        .filter(GuiClickablePreset.class::isInstance)
                        .filter(GuiAbstractShapePreset.class::isInstance)
                        .map(GuiClickablePreset.class::cast).collect(Collectors.toList())).flatMap(
                        Collection::stream).collect(Collectors.toList());

        callback1 = GLFW.glfwSetMouseButtonCallback(getWindow(), (w, button, action, mods) -> {
            boolean inDebugGui =
                    MouseUtils.isCursorInGui(GuiDebug.getInstance()) && GuiDebug.getInstance().isDisplayed();
            boolean inGui = Game.getInstance().getDisplayedGuis().stream().anyMatch(MouseUtils::isCursorInGui);
            final List<GuiClickablePreset> clickableComponents = new ArrayList<>(
                    inDebugGui ? Collections.singletonList(GuiDebug.getInstance())
                            : Game.getInstance().getDisplayedGuis())
                    .stream().map(gui -> gui.getAllComponents().stream()
                            .filter(GuiComponent::isDisplayed)
                            .filter(GuiClickablePreset.class::isInstance)
                            .filter(GuiAbstractShapePreset.class::isInstance)
                            .map(GuiClickablePreset.class::cast)
                            .filter(component -> isCursorInGuiComponent(
                                    ((GuiAbstractShapePreset) component).getShape()))
                            .collect(Collectors.toList())).flatMap(Collection::stream)
                    .collect(Collectors.toList());

            switch (button) {
                case GLFW_MOUSE_BUTTON_1:
                    if (!inGui && Game.getInstance().getGameState() == GameState.STARTED)
                        onM1OnTerrain(action);

                    switch (action) {
                        case GLFW_PRESS:
                            if (inGui)
                                clickableComponents.forEach(component -> component.onMousePress(GLFW_MOUSE_BUTTON_1));

                            Game.getInstance().getGuiTextInputs().stream()
                                    .filter(GuiComponent::isFocused)
                                    .filter(GuiTextInput::isUnfocusOnClick).forEach(guiTextInput -> {
                                if (!MouseUtils.isCursorInGuiComponent(guiTextInput.getOutline()))
                                    guiTextInput.unfocus();
                            });
                            break;
                        case GLFW_RELEASE:
                            clickables.stream().filter(c -> c.getClickType() == ClickType.M1).forEach(component -> {
                                if (!component.isReleaseInsideNeeded()
                                        || (GuiComponent.getParentGui((GuiPreset) component).isDisplayed() &&
                                        ((GuiPreset) component).isDisplayed() &&
                                        isCursorInGuiComponent(((GuiAbstractShapePreset) component).getShape())))
                                    component.onMouseRelease(GLFW_MOUSE_BUTTON_1);
                                else {
                                    component.reset();
                                }
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

                            if (inGui)
                                clickableComponents.forEach(guiButton -> guiButton.onMousePress(GLFW_MOUSE_BUTTON_2));
                            break;
                        case GLFW_RELEASE:
                            if (Game.getInstance().getGameState() == GameState.STARTED)
                                onM2Released();

                            clickables.stream().filter(c -> c.getClickType() == ClickType.M2).forEach(component -> {
                                if (!component.isReleaseInsideNeeded()
                                        || (GuiComponent.getParentGui((GuiPreset) component).isDisplayed() &&
                                        ((GuiPreset) component).isDisplayed() &&
                                        isCursorInGuiComponent(((GuiAbstractShapePreset) component).getShape())))
                                    component.onMouseRelease(GLFW_MOUSE_BUTTON_2);
                                else
                                    component.reset();
                            });
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
            Game.getInstance().getDisplayedGuis().forEach(
                    gui -> gui.getAllComponents().forEach(guiComponent -> guiComponent.onScroll(xoffset, yoffset)));
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

    public static void onMouseMove(Vector2f pos) {
        GuiSelectedItem selectedItemGui = GuiSelectedItem.getInstance();

        Game game = Game.getInstance();

        List<Gui> enteredGuis = game.getDisplayedGuis().stream()
                .filter(MouseUtils::isCursorInGui).collect(Collectors.toList());

        boolean inGui = !enteredGuis.isEmpty();

        new ArrayList<>(ENTERED_GUI_COMPONENTS).stream()
                .filter(guiComponent -> !MouseUtils.isCursorInGuiComponent(guiComponent))
                .forEach(guiComponent -> {
                    guiComponent.onLeave();
                    ENTERED_GUI_COMPONENTS.remove(guiComponent);
                });

        boolean gameStarted = game.getGameState() == GameState.STARTED;

        if (!inGui && gameStarted)
            MousePicker.getInstance().update();

        switch (state) {
            case DEFAULT:
                if (gameStarted)
                    selectedItemGui.setDisplayed(false);
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
//                        Terrain.getInstance().resetPreviewItems();
                        scene.resetPreviewedPositions();
                        selectedItemGui.setDisplayed(true);
                    }
                } else if (inGui) {
                    if (gameStarted) {
                        selectedItemGui.setDisplayed(true);
//                        terrain.resetPreviewItems();
                        scene.resetPreviewedPositions();
                    }

                    updateGuis(enteredGuis);
                }
                break;
            default:
                throw new IllegalStateException("Impossible state : " + state.name());
        }
    }

    private static void updateGuis(List<Gui> enteredGuis) {
        enteredGuis = enteredGuis.contains(GuiDebug.getInstance()) ? Collections.singletonList(GuiDebug.getInstance())
                : enteredGuis;
        List<GuiComponent> toUpdate = enteredGuis.stream()
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
        Vector3f point = MousePicker.getInstance().getIntersectionPoint();
        if (point == null)
            return;

        Vector2f p = new Vector2f(point.getX(), point.getZ());
        p = p.add(new Vector2f(-.5f, -.5f));
        TerrainPosition terrainPoint = p.toTerrainPosition();
//        System.out.println(terrainPoint);
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
//                camera.setFocusPoint(point);
                if (camera.getFocusPoint() != null) {
                    camera.setDistanceFromTerrainPoint((float) camera.getFocusPoint().distance(camera.getPosition()));
//                        camera.setDistanceFromTerrainPointChanged(true);
                }
                FrustumCullingFilter.updateFrustum();
            }
        }
    }

    private static void onM1OnTerrain(int action) {
        MousePicker picker = MousePicker.getInstance();
        picker.updateIntersectionOnClick();
        GameObject gameObject = picker.getGameObject();
        if (gameObject == null)
            return;

        Vector3f point = picker.getIntersectionPoint();

        switch (action) {
            case GLFW_PRESS:
                onM1Pressed(point, gameObject);
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

    public static void onM1Pressed(Vector3f point, GameObject gameObject) {
        startTerrainPos = null;
        lastTerrainPos = null;

        TerrainComponent terrainComponent = gameObject.getComponent(TerrainComponent.class);

        boolean onTerrain = terrainComponent != null;

        if (onTerrain) {
            Vector2f currTerrainPoint = new Vector2f(point.getX() - 0.5f,
                    point.getZ() - 0.5f); // -0.5 to account for road offset
            TerrainPosition currentTerrainPoint = currTerrainPoint.toTerrainPosition();
            GameObject gameObjectAtPosition = scene.getGameObjectAtPosition(currentTerrainPoint);
            boolean placeable = gameObjectAtPosition == null;
            if (!placeable) {
                RepleacableComponent repleacableComponent = gameObjectAtPosition
                        .getComponent(RepleacableComponent.class);
                if (repleacableComponent != null)
                    placeable = repleacableComponent.isRepleacable();
            }
            if (placeable) {
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

                        lastTerrainPos = startTerrainPos = currentTerrainPoint;
                        break;
                    case BUILDING:
                        state = State.PRESSED_WITH_BUILDING;
                        break;
                    case DESTRUCTION:
                        //TODO
                        break;
                    default:
                        throw new IllegalStateException("Impossible state : " + state.name());
                }
            }
        } else {
            SelectableComponent selectableComponent = gameObject.getComponent(SelectableComponent.class);
            if (selectableComponent != null)
                selectableComponent.getPressCallback().onPress(GLFW_MOUSE_BUTTON_1);
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

                scene.placePreviewedObjects();
                startTerrainPos = null;
                lastTerrainPos = null;
                break;
            case PRESSED_WITH_BUILDING:
                state = State.BUILDING;

                scene.placePreviewedObjects();

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

                GuiSelectedItem.getInstance().removeSelectedItem();
                scene.resetPreviewedPositions();
                break;
            default:
                throw new IllegalStateException("Impossible state : " + state.name());
        }
    }

    public static void onTerrainHover(TerrainPosition terrainPoint) {
        GuiSelectedItem selectedItemGui = GuiSelectedItem.getInstance();
        Class<? extends GameObject> selectedGameObjectClass = Player.getSelectedGameObject();

        switch (state) {
            case DEFAULT:
            case DESTRUCTION:
                //null
                break;
            case ROAD: // Previews only this road
            case BUILDING: // Previews only this building
            case PRESSED_WITH_BUILDING:
                scene.resetPreviewedPositions();
                if (scene.canGameObjectClassBePlaced(selectedGameObjectClass, terrainPoint)) {
                    if (selectedItemGui.isDisplayed())
                        selectedItemGui.setDisplayed(false);

                    scene.addToPreview(terrainPoint);
                } else {
                    selectedItemGui.updatePosition();
                    selectedItemGui.setDisplayed(true);
                }

                break;
            case PRESSED_WITH_ROAD:
                if (scene.canGameObjectClassBePlaced(selectedGameObjectClass, terrainPoint)) {
                    if (selectedItemGui.isDisplayed())
                        selectedItemGui.setDisplayed(false);

                    scene.resetPreviewedPositions();
                    lastTerrainPos = terrainPoint;
                    PathFinder pathFinder = new PathFinder(scene.getRoadGraphCopy());
                    Path path = pathFinder.findStraightClearPath(startTerrainPos, lastTerrainPos);
                    path.getAllRoads().stream().map(Road::getPosition).forEach(scene::addToPreview);
                } else {
                    scene.resetPreviewedPositions();
                    selectedItemGui.updatePosition();
                    selectedItemGui.setDisplayed(true);
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
