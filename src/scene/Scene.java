package scene;

import entities.Camera.Direction;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import models.RawModel;
import pathfinding.Road;
import pathfinding.RoadGraph;
import pathfinding.RoadNode;
import pathfinding.RouteFinder.Route;
import renderEngine.Loader;
import renderEngine.Renderer;
import renderEngine.TerrainRenderer;
import scene.components.*;
import scene.gameObjects.GameObject;
import scene.gameObjects.Player;
import services.BuildingRequirementsService;
import services.ServiceManager;
import terrains.TerrainPosition;
import util.math.Vector2f;
import util.math.Vector3f;

public class Scene {

    private final Map<Integer, GameObject> gameObjects          = new HashMap<>();
    private final Set<GameObject>          previewedGameObjects = new HashSet<>();

    private final Map<Renderer, Set<GameObject>> renderableGameObjects = new HashMap<>();

    private int[][] positions = new int[500][500];

    private final ServiceManager<BuildingRequirementsService> serviceManager = new ServiceManager<>();

    private RoadGraph   roadGraph;
    private boolean     needUpdatePaths;
    private List<Route> paths;

    private static Scene instance;

    public static Scene getInstance() {
        return instance == null ? (instance = new Scene()) : instance;
    }

    private Scene() {
        this.roadGraph = createRoadGraph();
    }

    public void addGameObject(GameObject gameObject) {
        PositionComponent positionComponent = gameObject.getComponent(PositionComponent.class);
        if (positionComponent != null &&
                positionComponent.getPosition().isTerrainPosition()) { // Objects belongs on terrain and has offset
            placeGameObjectOnTerrain(gameObject, positionComponent.getPosition().toTerrainPosition(), true);
        } else
            this.gameObjects.put(gameObject.getId(), gameObject);
    }

    public void addPosition(int x, int z, int value) {
        try {
            this.positions[x][z] = value;
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    public void removeGameObject(GameObject gameObject, Vector3f position) {
        this.gameObjects.remove(gameObject.getId());

        if (position != null && position.isTerrainPosition()) { // Object belongs on Terrain
            if (gameObject.hasComponent(OffsetsComponent.class)) {
                OffsetsComponent offsetsComponent = gameObject.getComponent(OffsetsComponent.class);
                Arrays.stream(offsetsComponent.getOffsetPositions()).map(pos -> pos.add(position.toTerrainPosition()))
                        .forEach(pos -> addPosition(pos.getX(), pos.getZ(), 0));
            }
        }
    }

    public int[][] getPositions() {
        return this.positions;
    }

    public void addRenderableGameObject(Renderer renderer, GameObject gameObject) {
        if (!this.renderableGameObjects.containsKey(renderer))
            this.renderableGameObjects.put(renderer, new HashSet<>());

        boolean added = this.renderableGameObjects.get(renderer).add(gameObject);
    }

    public void removeRenderableGameObject(Renderer renderer, GameObject gameObject) {
        if (this.renderableGameObjects.containsKey(renderer))
            this.renderableGameObjects.get(renderer).remove(gameObject);
    }

    public void render() {
        this.renderableGameObjects.forEach((renderer, lGameObjects) -> {
            lGameObjects.forEach(GameObject::prepareRender);
            renderer.render();
        });
    }

    public Set<GameObject> getGameObjects() {
        return new HashSet<>(this.gameObjects.values());
    }

    public GameObject getGameObjectAtPosition(TerrainPosition terrainPoint) {
        int id = getIdFromPosition(terrainPoint);
        if (id == -1)
            return null;

        return this.gameObjects.get(id);
    }

    /**
     * Add given position to the list of previewed positions
     *
     * @param currTerrainPoint new position
     */
    public void addToPreview(TerrainPosition currTerrainPoint) {
        if (!Player.hasSelectedGameObject())
            return;

        GameObject gameObject = GameObject.getObjectFromClass(Player.getSelectedGameObject());
        gameObject.getComponent(PreviewComponent.class).setPreviewPosition(currTerrainPoint);
        addRenderableGameObject(gameObject.getComponent(RendererComponent.class).getRenderer(), gameObject);
        this.previewedGameObjects.add(gameObject);
    }

    /**
     * Resets all the positions that were previously previewed
     */
    public void resetPreviewedPositions() {
        this.previewedGameObjects.forEach(GameObject::destroy);
        this.previewedGameObjects.clear();
    }

    /**
     * Returns the previewed positions
     */
    public Set<TerrainPosition> getPreviewItemPositions() {
        return this.previewedGameObjects.stream()
                .map(gameObject -> gameObject.getComponent(PreviewComponent.class).getPreviewPosition())
                .collect(Collectors.toUnmodifiableSet());
    }

    public boolean isPositionOccupied(TerrainPosition pos) {
        int idFound = getIdFromPosition(pos);
        if (idFound < 0)
            return true;
        if (idFound == 0)
            return false;

        GameObject object = this.gameObjects.get(idFound);
        if (object == null)
            return true;

        return !(object.hasComponent(RepleacableComponent.class) && object.getComponent(RepleacableComponent.class)
                .isRepleacable()); // Is component is repleacable then ignore
    }

    /**
     * Place previewed items on Terrain
     */
    public void placePreviewedObjects() {
        this.previewedGameObjects
                .forEach(gameObject -> {
                    PreviewComponent previewComponent = gameObject.getComponent(PreviewComponent.class);
                    TerrainPosition previewPosition = previewComponent.getPreviewPosition();
                    if (previewPosition != null) { // = Was previewed
                        previewComponent.setPreviewPosition(null);
                        gameObject.addComponent(new PositionComponent(previewPosition));
//                        placeGameObjectOnTerrain(gameObject, previewPosition, true);
                    }
                });
        this.previewedGameObjects.clear();
    }

    public int getMaxPeopleCapacity() {
        return this.gameObjects.values().stream()
                .map(gameObject -> gameObject.getComponent(FrequentedPlaceComponent.class))
                .filter(Objects::nonNull).mapToInt(FrequentedPlaceComponent::getMaxPeopleCapacity).sum();
    }

    public Set<GameObject> getRoads() {
        return this.gameObjects.values().stream().filter(gameObject ->
                        gameObject.hasComponent(RoadComponent.class)
                /*&& !isGameObjectPreviewed(gameObject)*/).collect(Collectors.toSet());
    }

    public boolean canGameObjectBePlaced(Class<? extends GameObject> gameObjectClass, TerrainPosition pos) {
        if (gameObjectClass == null)
            return false;

        return canGameObjectBePlaced(GameObject.getObjectFromClass(gameObjectClass), pos);
    }

    private boolean canGameObjectBePlaced(GameObject objectToPlace, TerrainPosition pos) {
        if (objectToPlace == null || isPositionOccupied(pos))
            return false;

        OffsetsComponent offsetsComponent = objectToPlace.getComponent(OffsetsComponent.class);
        if (offsetsComponent == null) // Only one wide
            return !isPositionOccupied(pos);


        List<TerrainPosition> relativePositions = Arrays.asList(offsetsComponent.getOffsetPositions());
        List<TerrainPosition> positions = relativePositions.stream().map(p -> p.add(pos))
                .collect(Collectors.toList());

        return positions.stream().noneMatch(this::isPositionOccupied);
    }

    /**
     * Place given object at given position on Terrain
     * returns false if object cannot be placed
     * returns true if object was placed successfully
     */
    private boolean placeGameObjectOnTerrain(GameObject gameObject, TerrainPosition pos, boolean checkIfSpace) {
        if (gameObject == null)
            return false;

        OffsetsComponent offsetsComponent = gameObject.getComponent(OffsetsComponent.class);
        if (offsetsComponent == null) { // Only one wide
            place(gameObject, pos, 0, 0, 0, 0);
            addPosition(pos.getX(), pos.getZ(), gameObject.getId());
            this.gameObjects.put(gameObject.getId(), gameObject);
            return true;
        }

        List<TerrainPosition> relativePositions = Arrays.asList(offsetsComponent.getOffsetPositions());
        List<TerrainPosition> positions = relativePositions.stream().map(p -> p.add(pos))
                .collect(Collectors.toList());

        if (checkIfSpace && !canGameObjectBePlaced(gameObject, pos))
            return false;

        place(gameObject, pos, offsetsComponent.getxNegativeOffset(), offsetsComponent.getxPositiveOffset(),
                offsetsComponent.getzNegativeOffset(), offsetsComponent.getzPositiveOffset());

        positions.forEach(p -> {
            int idFromPosition = getIdFromPosition(p);
            if (this.gameObjects.containsKey(idFromPosition)) { // If something already here
                GameObject gameObj = this.gameObjects.get(idFromPosition);
                if (gameObj.hasComponent(RepleacableComponent.class) &&
                        gameObj.getComponent(RepleacableComponent.class).isRepleacable()) // If replaceable
                    gameObj.destroy(); // Then destroy it before placing the new one
            }
            addPosition(p.getX(), p.getZ(), gameObject.getId());
        });

        this.gameObjects.put(gameObject.getId(), gameObject);
        return true;
    }

    private void place(GameObject gameObject, TerrainPosition pos, int xNeg, int xPos, int zNeg, int zPos) {
        RoadConnectionsComponent roadConnectionsComponent = gameObject.getComponent(RoadConnectionsComponent.class);
        if (roadConnectionsComponent == null)
            return;

        for (int x = -xNeg; x <= xPos; x++) {
            int id = getIdFromPosition(
                    new TerrainPosition(pos.getX() + x, pos.getZ() - zNeg - 1));
            if (this.gameObjects.containsKey(id)) {
                GameObject relativeGameObject = this.gameObjects.get(id);
                roadConnectionsComponent.connect(Direction.WEST);
                if (relativeGameObject.hasComponent(RoadConnectionsComponent.class))
                    relativeGameObject.getComponent(RoadConnectionsComponent.class).connect(Direction.EAST);
            }
            id = getIdFromPosition(
                    new TerrainPosition(pos.getX() + x, pos.getZ() + zPos + 1));
            if (this.gameObjects.containsKey(id)) {
                GameObject relativeGameObject = this.gameObjects.get(id);
                roadConnectionsComponent.connect(Direction.EAST);
                if (relativeGameObject.hasComponent(RoadConnectionsComponent.class))
                    relativeGameObject.getComponent(RoadConnectionsComponent.class).connect(Direction.WEST);
            }
        }
        for (int z = -zNeg; z <= zPos; z++) {
            int id = getIdFromPosition(
                    new TerrainPosition(pos.getX() - xNeg - 1, pos.getZ() + z));
            if (this.gameObjects.containsKey(id)) {
                GameObject relativeGameObject = this.gameObjects.get(id);
                roadConnectionsComponent.connect(Direction.NORTH);
                if (relativeGameObject.hasComponent(RoadConnectionsComponent.class))
                    relativeGameObject.getComponent(RoadConnectionsComponent.class).connect(Direction.SOUTH);
            }
            id = getIdFromPosition(
                    new TerrainPosition(pos.getX() + xPos + 1, pos.getZ() + z));
            if (this.gameObjects.containsKey(id)) {
                GameObject relativeGameObject = this.gameObjects.get(id);
                roadConnectionsComponent.connect(Direction.SOUTH);
                if (relativeGameObject.hasComponent(RoadConnectionsComponent.class))
                    relativeGameObject.getComponent(RoadConnectionsComponent.class).connect(Direction.NORTH);
            }
        }
    }

    /**
     * Get id of GameObject at given position
     * returns -1 if the position is wrong and 0 if the position is empty
     */
    private int getIdFromPosition(TerrainPosition position) {
        try {
            return this.positions[position.getX()][position.getZ()];
        } catch (IndexOutOfBoundsException ignored) {
        }
        return -1;
    }

//
//    private boolean isGameObjectPreviewed(GameObject gameObject) {
//        return gameObject.hasComponent(PositionComponent.class) && gameObject.hasComponent(PreviewComponent.class) &&
//                gameObject.getComponent(PreviewComponent.class).getPreviewPosition() != null;
//    }

    private RoadGraph createRoadGraph() {
        final RoadGraph roadGraph = new RoadGraph();

        Map<RoadNode, Direction[]> nodes = new HashMap<>();

        getRoads().forEach(road -> {
            TerrainPosition pos = road.getComponent(PositionComponent.class).getPosition().toTerrainPosition();
            Direction[] directions = getRoadConnections(pos);
            if (directions.length >= 3)
                nodes.put(new RoadNode(pos), directions);
        });

        for (Entry<RoadNode, Direction[]> node : nodes.entrySet()) {
            RoadNode roadNode = node.getKey();
            if (!roadGraph.getNodes().contains(roadNode))
                roadGraph.searchForNextNode(roadNode.getPosition(), node.getValue(), null);
        }

        return roadGraph;
    }

    public Direction[] getRoadConnections(TerrainPosition itemPosition) {
        GameObject gameObjectAtPosition = getGameObjectAtPosition(itemPosition);

        if (gameObjectAtPosition == null)
            return new Direction[0];

        if (!gameObjectAtPosition.hasComponent(RoadConnectionsComponent.class))
            return new Direction[0];

        RoadConnectionsComponent roadConnectionsComponent = gameObjectAtPosition
                .getComponent(RoadConnectionsComponent.class);

        Set<Direction> directions = new TreeSet<>();

        for (Direction direction : Direction.values()) {
            if (roadConnectionsComponent.getAccessPoints()[direction.ordinal()]) {
                TerrainPosition connectedItemPosition = new TerrainPosition(itemPosition)
                        .add(Direction.toRelativeDistance(direction));
                GameObject connectedItem = getGameObjectAtPosition(connectedItemPosition);
                if (connectedItem == null)
                    continue;

                if (connectedItem.hasComponent(RoadComponent.class))
                    directions.add(direction);
            }
        }

        return directions.toArray(new Direction[0]);
    }

    public RoadGraph getRoadGraph() {
        return this.roadGraph;
    }

    public void updateRequirements() {
        BuildingRequirementsService service = new BuildingRequirementsService(true, r -> {
            this.paths = r;
            needUpdatePaths = true;
        });

        this.serviceManager.addService(service);
        this.serviceManager.execute();
    }

    /**
     * Method created for testing purposes
     */
    public void resetObjects() {
        this.gameObjects.clear();
        this.previewedGameObjects.clear();
        this.positions = new int[500][500];
    }

    public Set<GameObject> getRoadsConnectedToGameObject(GameObject gameObject) {
        Set<GameObject> roads = new HashSet<>();
        if (!gameObject.hasComponent(PositionComponent.class) ||
                !gameObject.hasComponent(RoadConnectionsComponent.class))
            return roads;

        TerrainPosition fromPosition = gameObject.getComponent(PositionComponent.class).getPosition()
                .toTerrainPosition();
        RoadConnectionsComponent roadConnectionsComponent = gameObject.getComponent(RoadConnectionsComponent.class);
        int[] offsets;
        if (gameObject.hasComponent(OffsetsComponent.class))
            offsets = gameObject.getComponent(OffsetsComponent.class).getOffsets();
        else
            offsets = new int[]{0, 0, 0, 0}; // If no offsetsComponent, no offsets

        for (int i = 0; i < 4; i++) {
            if (roadConnectionsComponent.getAccessPoints()[i]) {
                Direction direction = Direction.values()[i];
                TerrainPosition roadPosition = fromPosition
                        .add(Direction.toRelativeDistance(direction, offsets[i] + 1));
                int offset = offsets[(i + 1) % 4];
                Direction newDirection = Direction.values()[(i + 1) % 4];
                roadPosition = roadPosition.add(Direction.toRelativeDistance(newDirection, offset));
                for (int j = 0; j <= offset + offsets[(i + 3) % 4]; j++) {
                    TerrainPosition distance = roadPosition
                            .add(Direction.toRelativeDistance(newDirection.getOppositeDirection(), j));
                    GameObject gameObjectAtPosition = getGameObjectAtPosition(distance);
                    if (gameObjectAtPosition != null && gameObjectAtPosition.hasComponent(RoadComponent.class))
                        roads.add(gameObjectAtPosition);
                }
            }
        }
        return roads;
    }

    /**
     * Testing purposes
     */
    public void resetRoadGraph() {
        this.roadGraph = new RoadGraph();
    }

    public void updateHighlightedPaths() {
        if (needUpdatePaths) {
            setHightlightedPaths(paths);

            needUpdatePaths = false;
        }
    }

    public void setHightlightedPaths(List<Route> routeList) {
        List<RawModel> paths = new ArrayList<>();
        if (!routeList.isEmpty()) {
//            System.out.println("Highlighted paths: ");

            for (Route routes : routeList) {
//                System.out.println("\tPath: " + routes);
//                System.out.println();
//                System.out.println();
                Set<Vector2f> positions = new LinkedHashSet<>();
                routes.forEach(routeRoad -> {
                    List<Road> roads = new ArrayList<>(routeRoad.getRoute());

                    if (roads.size() == 1) {
                        TerrainPosition position = roads.get(0).getPosition();

                        GameObject gameObject = getGameObjectAtPosition(position);
                        if (gameObject.hasComponent(RoadConnectionsComponent.class)) {
                            RoadConnectionsComponent connections = gameObject
                                    .getComponent(RoadConnectionsComponent.class);
                            for (Direction dir : Direction.values()) {
                                if (connections.isConnected(dir)) {
                                    TerrainPosition pos = position.add(Direction.toRelativeDistance(dir));
                                    positions.add(new Vector2f(pos.getX(), pos.getZ()));
                                    if (positions.size() == 2) // if too many connections then visuals fuck up
                                        break;
                                }
                            }
                        }
                    } else
                        roads.stream().map(Road::getPosition)
                                .forEach(pos -> positions.add(new Vector2f(pos.getX(), pos.getZ())));
                });

                if (positions.isEmpty())
                    return;

                float[] positionsFloat = new float[positions.size() * 3];
                int i = 0;
                for (Vector2f pos : positions) {
                    positionsFloat[i++] = pos.x;
                    positionsFloat[i++] = .5f;
                    positionsFloat[i++] = pos.y;
                }
                int[] indicesTab = new int[positions.size() * 2 - 2];
                int j = 0;
                for (i = 0; i < indicesTab.length; i++) {
                    indicesTab[i++] = j++;
                    indicesTab[i] = j;
                }

                RawModel path = Loader.getInstance()
                        .loadToVAO(positionsFloat, new float[]{0}, new float[]{0, 1, 0}, indicesTab);
                paths.add(path);
            }
        }
        TerrainRenderer.getInstance().setPaths(paths);
    }

    public Set<GameObject> getGameObjectsOfType(Class<? extends GameObject> gameObjectClass) {
        return this.gameObjects.values().stream().filter(item -> item.getClass() == gameObjectClass)
                .collect(Collectors.toSet());
    }
}
