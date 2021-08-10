package util.commands;

import entities.Camera.Direction;
import pathfinding.Path;
import pathfinding.PathFinder;
import renderEngine.PathRenderer;
import scene.Scene;
import scene.components.*;
import scene.components.PathComponent.PathType;
import scene.gameObjects.GameObject;
import scene.gameObjects.GameObjectDatas;
import scene.gameObjects.Terrain;
import terrain.TerrainPosition;
import util.Utils;

import java.awt.*;
import java.util.List;
import java.util.*;

import static entities.Camera.Direction.NORTH;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class SpawnCommand extends Command {

    private final static String[] VALID_GAMEOBJECTS = new String[]{"NPC", "Insula", "Market"};

    private final static String[] LOCAL_ALIAS = new String[]{"spawn"};
    private final static String[] LOCAL_PARAMETERS = new String[]{"GameObject", "x", "y"};
    private final static String[] LOCAL_OPTIONAL_PARAMETERS = new String[]{"direction", "start",
            "destination", "show"};

    private final static LocalExecuteCommandCallback LOCAL_CALLBACK = (gameObjectName, x, z, dir, start, dest, show) -> {
        if (!isGameObjectValid(gameObjectName))
            return 1;

        if (!x.matches("\\d+(\\.\\d+)?"))
            return 2;
        if (!z.matches("\\d+(\\.\\d+)?"))
            return 3;

        int xValue = Integer.parseInt(x);
        int zValue = Integer.parseInt(z);
        Direction direction;
        try {
            direction = Direction.valueOf(dir.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            direction = NORTH;
        }
        if (dest == null && start != null)
            return 7;

        boolean showBool;

        if (show != null && !show.equalsIgnoreCase("true") && !show.equalsIgnoreCase("false"))
            return 7;

        showBool = Boolean.parseBoolean(show);

        if (start != null && !start.matches(COMMAND_POSITION_REGEX))
            return 5;

        if (dest != null && !dest.matches(COMMAND_POSITION_REGEX))
            return 5;

        Class<? extends GameObject> gameObjectClass = GameObject.getClassFromName(gameObjectName);

        Terrain terrain = Scene.getInstance().getTerrain();
        HeightMapComponent heightMapComponent = terrain.getComponent(HeightMapComponent.class);
        TerrainPosition position = new TerrainPosition(xValue, heightMapComponent.getHeight(xValue, zValue), zValue);
        if (!Scene.getInstance().canGameObjectClassBePlaced(gameObjectClass, position, direction))
            return 4;

        GameObject gameObject = GameObject.newInstance(gameObjectClass, position);
        if (!gameObject.hasComponent(PathComponent.class) && (start != null || dest != null)) {
            Scene.getInstance().removeGameObject(gameObject.getId());
            return 6;
        }

        if (gameObject.hasComponent(PathComponent.class) && dest != null) {
            gameObject.removeComponent(PositionComponent.class);
            TerrainPosition startPos = position;
            if (start != null)
                startPos = Utils.decodePositionCommand(start);
            TerrainPosition destPos = Utils.decodePositionCommand(dest);
            PathFinder pathFinder = new PathFinder(Scene.getInstance().getRoadGraph());
            gameObject.addComponent(new TransparencyComponent(1));
            gameObject.addComponent(new BoundingBoxComponent(GameObjectDatas.NPC.getBoundingBox()));

            PathComponent component = gameObject.getComponent(PathComponent.class);
            component.createPathComponent(gameObject, position.toVector3f(), PathType.ROAD_TO_ROAD);
            Path bestPath = pathFinder.findBestPath(startPos, destPos, 0);
            gameObject.addComponent(new SelectableComponent(button -> {
                if (button == GLFW_MOUSE_BUTTON_LEFT) {
                    showPath(bestPath);
                    return true;
                }
                return false;
            }));
            bestPath.savePathCoordinates();
            if (showBool)
                showPath(bestPath);
            component.setPath(bestPath);
        }
        gameObject.addComponent(new DirectionComponent(direction));

        return 0;
    };

    private static void showPath(Path bestPath) {
        System.out.println("Clicked on NPC");
        Map<Path, Color> tempPathsList = new HashMap<>(PathRenderer.getInstance().getTempPathsList());
        tempPathsList.put(bestPath, Color.WHITE);
        PathRenderer.getInstance().addToTempPathsList(tempPathsList);
    }

    public SpawnCommand() {
        super(LOCAL_ALIAS, LOCAL_PARAMETERS, LOCAL_OPTIONAL_PARAMETERS, LOCAL_CALLBACK);
    }

    @Override
    public String getMessageFromResult(int result) {
        List<String> parameterValues = new ArrayList<>(this.parameters.values());
        switch (result) {
            case -1:
                return "Syntax: " + getSyntax();
            case 0:
                return "Spawned " + parameterValues.get(0) + " successfully!";
            case 1:
                return "Unsupported GameObject: " + parameterValues.get(0) + "!";
            case 2:
                return "Incorrect x value: " + parameterValues.get(1) + "!";
            case 3:
                return "Incorrect y value: " + parameterValues.get(2) + "!";
            case 4:
                return "Could not spawn " + parameterValues.get(0) + " at this location!";
            case 5:
                return "Position syntax is (x, y)!";
            case 6:
                return "Could not give path to " + parameterValues.get(0) + "!";
            case 7:
                return "Destination must be given!";
            case 8:
                return "Show value must be true or false!";
        }
        return "";
    }

    @Override
    protected Command copy() {
        return new SpawnCommand();
    }

    private static boolean isGameObjectValid(String gameObject) {
        return Arrays.stream(VALID_GAMEOBJECTS).anyMatch(gameObject::equalsIgnoreCase);
    }


    @FunctionalInterface
    interface LocalExecuteCommandCallback extends ExecuteCommandCallback {

        @Override
        default int onExecute(String... params) {
            if (params.length == 3)
                return onExecute(params[0], params[1], params[2], null, null, null, null);
            else if (params.length == 4)
                return onExecute(params[0], params[1], params[2], params[3], null, null, null);
            else if (params.length == 5)
                return onExecute(params[0], params[1], params[2], params[3], params[4], null, null);
            else if (params.length == 6)
                return onExecute(params[0], params[1], params[2], params[3], params[4], params[5], null);
            else if (params.length == 7)
                return onExecute(params[0], params[1], params[2], params[3], params[4], params[5], params[6]);

            return -1;
        }

        int onExecute(String p1, String p2, String p3, String p4, String p5, String p6, String p7);
    }
}
