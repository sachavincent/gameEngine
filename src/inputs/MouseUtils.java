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
import renderEngine.DisplayManager;
import renderEngine.FrustumCullingFilter;
import scene.Scene;
import scene.components.SelectableComponent;
import scene.gameObjects.GameObject;
import scene.gameObjects.Player;
import terrains.TerrainPosition;
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

    private static State state = State.DEFAULT;
    private static State previousState;

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
//                    if (!inGui && Game.getInstance().getGameState() == GameState.STARTED)
//                        onM1OnTerrain(action);

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

                            OnM1Pressed();
                            break;
                        case GLFW_RELEASE:
                            OnM1Released();

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
                            OnM2Pressed();

                            if (inGui)
                                clickableComponents.forEach(guiButton -> guiButton.onMousePress(GLFW_MOUSE_BUTTON_2));
                            break;
                        case GLFW_RELEASE:
                            OnM2Released();

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

        callback3 = GLFW.glfwSetCursorPosCallback(window, (w, xPos, yPos) -> OnMouseMove(new Vector2f(xPos, yPos)));
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
        new ArrayList<>(ENTERED_GUI_COMPONENTS).stream()
                .filter(guiComponent -> !MouseUtils.isCursorInGuiComponent(guiComponent))
                .forEach(guiComponent -> {
                    guiComponent.onLeave();
                    ENTERED_GUI_COMPONENTS.remove(guiComponent);
                });
    }

    private static void hoverOnTerrain(GameObject hoveredObject, Vector2f position) {

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

    public static void SelectRoad() {
        switch (state) {
            case IN_GUI:
                state = State.ROAD;
                GuiSelectedItem.getInstance().setDisplayed(true);
                break;
            case DEFAULT:
            case HOVER_TERRAIN:
            case PRESSED_WITH_ROAD:
            case PRESSED_WITH_BUILDING:
            case ROAD:
            case BUILDING:
                // Impossible
                break;
            default:
                throw new IllegalStateException("Impossible state : " + state.name());
        }
    }

    public static void SelectBuilding() {
        switch (state) {
            case IN_GUI:
                state = State.BUILDING;
                GuiSelectedItem.getInstance().setDisplayed(true);
                break;
            case DEFAULT:
            case HOVER_TERRAIN:
            case PRESSED_WITH_ROAD:
            case PRESSED_WITH_BUILDING:
            case ROAD:
            case BUILDING:
                //impossible
                break;
            default:
                throw new IllegalStateException("Impossible state : " + state.name());
        }
    }

    public static void Deselect() {
        switch (state) {
            case DEFAULT:
            case HOVER_TERRAIN:
                //null
                break;
            case IN_GUI:
                if (previousState != null) {
                    previousState = null;
                    GuiSelectedItem.getInstance().setDisplayed(false);
                }
                break;
            case ROAD:
            case BUILDING:
                state = State.DEFAULT;
                GuiSelectedItem.getInstance().setDisplayed(false);
                break;
            case PRESSED_WITH_ROAD:
            case PRESSED_WITH_BUILDING:
                // impossible
                break;
            default:
                throw new IllegalStateException("Impossible state : " + state.name());
        }
    }

    public static void OnMouseMove(Vector2f pos) {
        GuiSelectedItem selectedItemGui = GuiSelectedItem.getInstance();

        Game game = Game.getInstance();

        List<Gui> enteredGuis = game.getDisplayedGuis().stream()
                .filter(MouseUtils::isCursorInGui).collect(Collectors.toList());

        boolean isMouseInGui = !enteredGuis.isEmpty();
        if (isMouseInGui)
            updateGuis(enteredGuis);

        boolean gameStarted = game.getGameState() == GameState.STARTED;

        if (!isMouseInGui && gameStarted)
            MousePicker.getInstance().update();

        GameObject objectAtPosition = MousePicker.getInstance().getGameObject();
        boolean isMouseOnTerrain = MousePicker.getInstance().isPointOnTerrain();
        if (!gameStarted)
            return;
        { //TEMP
            if (middleMouseButtonPressed) {
                Camera camera = Camera.getInstance();
                if (previousXPos == -1 && previousYPos == -1) { // Premier mvt depuis que middle click a été appuyé
                    previousXPos = pos.x;
                    previousYPos = pos.y;

                    previousYaw = camera.getYaw();
                    previousPitch = camera.getPitch();
                } else {
                    float xOffset = (float) (previousXPos - pos.x) / (float) DisplayManager.WIDTH * 360;
                    float yOffset = (float) (previousYPos - pos.y) / (float) DisplayManager.HEIGHT * 360;
                    //TODO: Sensitivity

                    camera.setYaw((int) (previousYaw - xOffset));
                    camera.setPitch(previousPitch - yOffset);
//                camera.setFocusPoint(point);
                    if (camera.getFocusPoint() != null) {
                        camera.setDistanceFromTerrainPoint(
                                (float) camera.getFocusPoint().distance(camera.getPosition()));
//                        camera.setDistanceFromTerrainPointChanged(true);
                    }
                    FrustumCullingFilter.updateFrustum();
                }
            }
        }
        TerrainPosition terrainPos;
        selectedItemGui.updatePosition();
        Vector3f intersectionPoint = MousePicker.getInstance().getIntersectionPoint();
        switch (state) {
            case DEFAULT:
                if (isMouseInGui) {
                    state = State.IN_GUI;
                } else if (isMouseOnTerrain) {
                    state = State.HOVER_TERRAIN;
                    hoverOnTerrain(objectAtPosition, pos);
                }
                break;
            case IN_GUI:
                if (!isMouseInGui) {
                    if (previousState == null) {
                        if (isMouseOnTerrain) {
                            state = State.HOVER_TERRAIN;
                            hoverOnTerrain(objectAtPosition, pos);
                        } else {
                            state = State.DEFAULT;
                        }
                    } else {
                        state = previousState;
                        previousState = null;
                    }
                }
                break;
            case HOVER_TERRAIN:
                if (isMouseInGui) {
                    state = State.IN_GUI;
                } else if (isMouseOnTerrain) {
                    hoverOnTerrain(objectAtPosition, pos);
                } else {
                    state = State.DEFAULT;
                }
                break;
            case ROAD:
            case BUILDING:
                scene.resetPreviewedPositions();
                if (isMouseInGui) {
                    previousState = state;
                    state = State.IN_GUI;
                    GuiSelectedItem.getInstance().setDisplayed(true);
                } else if (isMouseOnTerrain && scene.canGameObjectClassBePlaced(Player.getSelectedGameObject(),
                        (terrainPos = intersectionPoint.toTerrainPositionForPlacing()))) {
                    GuiSelectedItem.getInstance().setDisplayed(false);
                    scene.addToPreview(terrainPos);
                } else {
                    GuiSelectedItem.getInstance().setDisplayed(true);
                }
                break;
            case PRESSED_WITH_ROAD:
                if (isMouseInGui) {
                    state = State.IN_GUI;
                    previousState = State.PRESSED_WITH_ROAD;
                    GuiSelectedItem.getInstance().setDisplayed(true);
                    scene.resetPreviewedPositions();
                } else if (isMouseOnTerrain && scene.canGameObjectClassBePlaced(Player.getSelectedGameObject(),
                        (terrainPos = intersectionPoint.toTerrainPositionForPlacing()))) {
                    if (!scene.getPreviewItemPositions().contains(terrainPos)) {
                        scene.addToPreview(terrainPos);
                    }
                } else {
                    GuiSelectedItem.getInstance().setDisplayed(true);
                    scene.resetPreviewedPositions();
                }
                break;
            case PRESSED_WITH_BUILDING:
                scene.resetPreviewedPositions();

                if (isMouseInGui) {
                    state = State.IN_GUI;
                    previousState = State.PRESSED_WITH_BUILDING;
                    GuiSelectedItem.getInstance().setDisplayed(true);
                } else if (isMouseOnTerrain && scene.canGameObjectClassBePlaced(Player.getSelectedGameObject(),
                        (terrainPos = intersectionPoint.toTerrainPositionForPlacing()))) {
                    scene.addToPreview(terrainPos);
                } else {
                    GuiSelectedItem.getInstance().setDisplayed(true);
                }
                break;
            default:
                throw new IllegalStateException("Impossible state : " + state.name());
        }
    }

    public static void OnM1Pressed() {
        MousePicker mousePicker = MousePicker.getInstance();
        switch (state) {
            case DEFAULT:
            case IN_GUI:
            case PRESSED_WITH_ROAD:
            case PRESSED_WITH_BUILDING:
                // null
                break;
            case HOVER_TERRAIN:
                mousePicker.updateIntersectionOnClick();
                GameObject objectAtPosition = mousePicker.getGameObject();
                if (objectAtPosition != null && !mousePicker.isPointOnTerrain()) {
                    if (objectAtPosition.hasComponent(SelectableComponent.class))
                        objectAtPosition.getComponent(SelectableComponent.class).getPressCallback()
                                .onPress(GLFW_MOUSE_BUTTON_1);
                }
                break;
            case ROAD:
                state = State.PRESSED_WITH_ROAD;
                break;
            case BUILDING:
                state = State.PRESSED_WITH_BUILDING;
                break;
            default:
                throw new IllegalStateException("Impossible state : " + state.name());
        }

    }

    private static void OnM1Released() {
        switch (state) {
            case DEFAULT:
            case ROAD:
            case BUILDING:
                // null
                break;
            case IN_GUI:
                if (previousState == State.PRESSED_WITH_BUILDING || previousState == State.PRESSED_WITH_ROAD) {
                    scene.resetPreviewedPositions();
                    if (previousState == State.PRESSED_WITH_ROAD) {
                        previousState = State.ROAD;
                    } else {
                        previousState = State.BUILDING;
                    }
                }
                break;
            case HOVER_TERRAIN:
                //null for now
                break;
            case PRESSED_WITH_ROAD:
                state = State.ROAD;

                scene.placePreviewedObjects();
                break;
            case PRESSED_WITH_BUILDING:
                state = State.BUILDING;

                scene.placePreviewedObjects();
                break;
            default:
                throw new IllegalStateException("Impossible state : " + state.name());
        }
    }

    private static void OnM2Pressed() {
        MousePicker mousePicker = MousePicker.getInstance();
        if (Game.getInstance().getGameState() == GameState.STARTED) {
            switch (state) {
                case DEFAULT:
                    //null
                    break;
                case IN_GUI:
                    if (previousState != null) {
                        previousState = null;
                        scene.resetPreviewedPositions();
                        GuiSelectedItem.getInstance().setDisplayed(false);
                        GuiSelectedItem.getInstance().removeSelectedItem();
                    }
                    break;
                case HOVER_TERRAIN:
                    mousePicker.updateIntersectionOnClick();
                    GameObject objectAtPosition = mousePicker.getGameObject();
                    if (objectAtPosition != null && !mousePicker.isPointOnTerrain()) {
                        if (objectAtPosition.hasComponent(SelectableComponent.class))
                            objectAtPosition.getComponent(SelectableComponent.class).getPressCallback()
                                    .onPress(GLFW_MOUSE_BUTTON_2);
                    }
                    break;
                case ROAD:
                case BUILDING:
                    state = State.DEFAULT;
                    scene.resetPreviewedPositions();
                    GuiSelectedItem.getInstance().setDisplayed(false);
                    GuiSelectedItem.getInstance().removeSelectedItem();
                    break;
                case PRESSED_WITH_BUILDING:
                    state = State.BUILDING;
                    scene.resetPreviewedPositions();
                    break;
                case PRESSED_WITH_ROAD:
                    state = State.ROAD;
                    scene.resetPreviewedPositions();
                    break;
                default:
                    throw new IllegalStateException("Impossible state : " + state.name());
            }
        }
    }

    private static void OnM2Released() {
        if (Game.getInstance().getGameState() == GameState.STARTED) {
            switch (state) {
                case DEFAULT:
                case IN_GUI:
                case ROAD:
                case BUILDING:
                case PRESSED_WITH_ROAD:
                case PRESSED_WITH_BUILDING:
                    // null
                    break;
                case HOVER_TERRAIN:
                    // null for now
                    break;
                default:
                    throw new IllegalStateException("Impossible state : " + state.name());
            }
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


    private enum State {
        DEFAULT,
        HOVER_TERRAIN,
        IN_GUI,
        ROAD,
        BUILDING,
        PRESSED_WITH_ROAD,
        PRESSED_WITH_BUILDING
//        DESTRUCTION,
//        PRESSED_WITH_DESTRUCTION

    }
}
