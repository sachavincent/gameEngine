package scene;

import engineTester.Game;
import entities.Camera.Direction;
import models.Model;
import pathfinding.NodeRoad;
import pathfinding.RoadGraph;
import renderEngine.*;
import scene.callbacks.FilterGameObjectCallback;
import scene.components.*;
import scene.components.requirements.ResourceRequirementComponent;
import scene.gameObjects.GameObject;
import scene.gameObjects.Player;
import scene.gameObjects.Route;
import scene.gameObjects.Terrain;
import services.BuildingRequirementsService;
import services.ServiceManager;
import terrain.TerrainPosition;
import util.DayNightCycle;
import util.math.Vector3f;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static pathfinding.RoadGraph.FILTER;

public class Scene {

    private final Map<Class<? extends Component>, Set<Integer>> idGameObjectsForComponents = new HashMap<>();

    private final Map<Integer, GameObject> gameObjects = new HashMap<>();

    private final Map<Integer, GameObject> previewedGameObjects = new HashMap<>();

    private final Map<GameObjectRenderer, Set<GameObject>> renderableGameObjects = new HashMap<>();

    private int[][] positions = new int[Game.TERRAIN_WIDTH][Game.TERRAIN_DEPTH];

    private final ServiceManager<BuildingRequirementsService> serviceManager = new ServiceManager<>();

    private RoadGraph roadGraph;

    private Terrain terrain;

    private static Scene instance;

    public static Scene getInstance() {
        return instance == null ? (instance = new Scene()) : instance;
    }

    private Scene() {
        this.roadGraph = createRoadGraph();
    }

    public boolean addGameObject(GameObject gameObject) {
        if (gameObject instanceof Terrain)
            this.terrain = (Terrain) gameObject;

        PositionComponent positionComponent = gameObject.getComponent(PositionComponent.class);
        if (positionComponent != null &&
                positionComponent.getPosition().isTerrainPosition()) { // Objects belongs on terrain and has offset
            return placeGameObjectOnTerrain(gameObject, positionComponent.getPosition().toTerrainPosition(), true);
        } else
            this.gameObjects.put(gameObject.getId(), gameObject);

        return true;
    }

    public void addPosition(int x, int z, int value) {
        try {
            this.positions[x][z] = value;
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    /**
     * Removes GameObject from all lists
     * including the renderable list
     *
     * @param idGameObject id of the GameObject
     */
    public void removeGameObject(int idGameObject) {
        GameObject gameObject = this.gameObjects.remove(idGameObject);
        if (gameObject == null)
            return;

        if (gameObject.hasComponent(PositionComponent.class)) {
            Vector3f position = gameObject.getComponent(PositionComponent.class).getPosition();
            if (position != null && position.isTerrainPosition()) { // Object belongs on Terrain
                if (gameObject.hasComponent(OffsetsComponent.class)) {
                    OffsetsComponent offsetsComponent = gameObject.getComponent(OffsetsComponent.class);
                    Direction direction = gameObject.hasComponent(DirectionComponent.class) ? gameObject
                            .getComponent(DirectionComponent.class).getDirection() : Direction.defaultDirection();
                    Arrays.stream(offsetsComponent.getOffsetPositions(direction))
                            .map(pos -> pos.add(position.toTerrainPosition()))
                            .forEach(pos -> addPosition(pos.getX(), pos.getZ(), 0));
                }
            }
        }
        if (gameObject.hasComponent(RendererComponent.class)) {
            RendererComponent rendererComponent = gameObject.getComponent(RendererComponent.class);
            removeRenderableGameObject(rendererComponent.getRenderer(), gameObject);
        }
    }

    public int[][] getPositions() {
        return this.positions;
    }

    public void addRenderableGameObject(GameObjectRenderer gameObjectRenderer, GameObject gameObject) {
        if (!this.renderableGameObjects.containsKey(gameObjectRenderer))
            this.renderableGameObjects.put(gameObjectRenderer, new HashSet<>());

        boolean added = this.renderableGameObjects.get(gameObjectRenderer).add(gameObject);
    }

    public void removeRenderableGameObject(GameObjectRenderer gameObjectRenderer, GameObject gameObject) {
        if (this.renderableGameObjects.containsKey(gameObjectRenderer))
            this.renderableGameObjects.get(gameObjectRenderer).remove(gameObject);
    }

    public void render() {
        MasterRenderer.getInstance().prepare();
        SkyboxRenderer.getInstance().render();
        this.renderableGameObjects.forEach((renderer, lGameObjects) -> {
            lGameObjects.forEach(GameObject::prepareRender);
            renderer.render();
        });
        DayNightCycle.incrementCycleTime();
    }

    public Set<GameObject> getGameObjects() {
        return new HashSet<>(this.gameObjects.values());
    }

    public GameObject getGameObjectAtPosition(TerrainPosition terrainPoint) {
        int id = getIdFromPosition(terrainPoint);
        if (id == -1)
            return null;

        return getGameObjectFromId(id);
    }

    public GameObject getGameObjectFromId(int id) {
        if (this.gameObjects.containsKey(id))
            return this.gameObjects.get(id);
        return this.previewedGameObjects.get(id);
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
        if (gameObject == null)
            return;
        gameObject.addComponent(new DirectionComponent(Player.getDirection()));
        if (gameObject.hasComponent(PreviewComponent.class))
            gameObject.getComponent(PreviewComponent.class).setPreviewPosition(currTerrainPoint.toVector3f());
        addRenderableGameObject(gameObject.getComponent(RendererComponent.class).getRenderer(), gameObject);
        this.previewedGameObjects.put(gameObject.getId(), gameObject);
    }

    /**
     * Change preview position, only works if only one position is previewed
     *
     * @param newTerrainPoint the new preview position
     */
    public void changePreviewPosition(TerrainPosition newTerrainPoint) {
        if (this.previewedGameObjects.size() != 1)
            return;

        GameObject previewedGameObject = this.previewedGameObjects.values().stream().findFirst().orElse(null);
//        removeRenderableGameObject(previewedGameObject.getComponent(RendererComponent.class).getRenderer(), previewedGameObject);
//        addRenderableGameObject(previewedGameObject.getComponent(RendererComponent.class).getRenderer(), previewedGameObject);
        if (!previewedGameObject.hasComponent(PreviewComponent.class))
            return;
        previewedGameObject.getComponent(PreviewComponent.class).setPreviewPosition(newTerrainPoint.toVector3f());
    }

    /**
     * Resets all the positions that were previously previewed
     */
    public void resetPreviewedPositions(boolean purge) {
        if (purge)
            this.previewedGameObjects.values().forEach(GameObject::destroy);
        this.previewedGameObjects.clear();
    }

    /**
     * Returns the previewed positions
     */
    public Set<TerrainPosition> getPreviewItemPositions() {
        return this.previewedGameObjects.values().stream()
                .map(gameObject -> gameObject.getComponent(PreviewComponent.class).getPreviewPosition()
                        .toTerrainPosition())
                .collect(Collectors.toUnmodifiableSet());
    }

    public Map<Integer, GameObject> getPreviewedGameObjects() {
        return Collections.unmodifiableMap(this.previewedGameObjects);
    }

    public Map<Class<? extends Component>, Set<Integer>> getIdGameObjectsForComponents() {
        return this.idGameObjectsForComponents;
    }


    public Set<Integer> getIdGameObjectsForComponentClass(Class<? extends Component> clazz, boolean includePreviews) {
        if (!this.idGameObjectsForComponents.containsKey(clazz))
            return new HashSet<>();

        Set<Integer> ids = this.idGameObjectsForComponents.get(clazz);
        if (!includePreviews)
            ids.removeAll(this.previewedGameObjects.keySet());

        return ids;
    }

    public Set<GameObject> getGameObjectsForComponent(Class<? extends Component> clazz, boolean includePreviews) {
        return getIdGameObjectsForComponentClass(clazz, includePreviews).stream()
                .map(this::getGameObjectFromId).filter(Objects::nonNull).collect(Collectors.toSet());
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
        GameObject obj = null;
        TerrainPosition pos = null;
        for (GameObject gameObject : this.previewedGameObjects.values()) {
            PreviewComponent previewComponent = gameObject.getComponent(PreviewComponent.class);
            Vector3f previewPosition = previewComponent.getPreviewPosition();
            previewComponent.setPreviewPosition(null);
            if (previewPosition != null) { // = Was previewed
                gameObject.destroy();
                System.out.println("Placing at " + previewPosition);
                Direction direction = Direction.defaultDirection();
                if (gameObject.hasComponent(DirectionComponent.class))
                    direction = gameObject.getComponent(DirectionComponent.class).getDirection();
                obj = GameObject
                        .newInstance(gameObject.getClass(), previewPosition.toTerrainPosition(), direction, true);

                pos = previewPosition.toTerrainPosition();
            }
        }
        if (obj != null)
            obj.onUniqueAddGameObject(pos.toVector3f());
        resetPreviewedPositions(false);
    }

    public int getMaxPeopleCapacity() {
        return this.gameObjects.values().stream()
                .map(gameObject -> gameObject.getComponent(ResidenceComponent.class))
                .filter(Objects::nonNull).mapToInt(ResidenceComponent::getMaxPeopleCapacity).sum();
    }

    public Set<GameObject> getRoads() {
        return new HashSet<>(getGameObjectsForComponent(RoadComponent.class, false));
    }

    public boolean canGameObjectClassBePlaced(Class<? extends GameObject> gameObjectClass, TerrainPosition pos,
                                              Direction direction) {
        if (gameObjectClass == null)
            return false;

        GameObject objectFromClass = GameObject.getObjectFromClass(gameObjectClass);
        objectFromClass.addComponent(new DirectionComponent(direction));
        if (objectFromClass.hasComponent(PreviewComponent.class))
            objectFromClass.getComponent(PreviewComponent.class).setPreviewPosition(pos.toVector3f());
        boolean res = canGameObjectBePlaced(objectFromClass, pos);
        objectFromClass.destroy();

        return res;
    }

    private boolean canGameObjectBePlaced(GameObject objectToPlace, TerrainPosition pos) {
        if (objectToPlace == null)
            return false;

        if (objectToPlace.hasComponent(ResourceRequirementComponent.class)) {
            ResourceRequirementComponent component = objectToPlace.getComponent(ResourceRequirementComponent.class);
            component.getPlacingRequirements().keySet().forEach(component::clearRequirement);
            if (component.getPlacingRequirements().keySet().stream()
                    .anyMatch(requirement -> !requirement.isRequirementMet(objectToPlace)))
                return false;
        }
        LayerableComponent layerableComponent = objectToPlace.getComponent(LayerableComponent.class);
        OffsetsComponent offsetsComponent = objectToPlace.getComponent(OffsetsComponent.class);
        if (offsetsComponent == null) { // Only one wide
            if (!objectToPlace.hasComponent(LayerableComponent.class))
                return !isPositionOccupied(pos);
            GameObject gameObjectAtPosition = getGameObjectAtPosition(pos);
            if (gameObjectAtPosition == null)
                return false;
            return layerableComponent.getLayerableGameObjectsClasses()
                    .contains(gameObjectAtPosition.getClass()) || !isPositionOccupied(pos);
        }

        Direction direction = objectToPlace.hasComponent(DirectionComponent.class) ? objectToPlace
                .getComponent(DirectionComponent.class).getDirection() : Direction.defaultDirection();

        List<TerrainPosition> relativePositions = Arrays.asList(offsetsComponent.getOffsetPositions(direction));
        List<TerrainPosition> positions = relativePositions.stream().map(p -> p.add(pos))
                .collect(Collectors.toList());

        if (!objectToPlace.hasComponent(LayerableComponent.class))
            return positions.stream().noneMatch(this::isPositionOccupied);

        return positions.stream().noneMatch(position -> {
            GameObject gameObjectAtPosition = getGameObjectAtPosition(position);
            if (gameObjectAtPosition != null)
                return !layerableComponent.getLayerableGameObjectsClasses()
                        .contains(gameObjectAtPosition.getClass()) && isPositionOccupied(pos);
            return false;
        });
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
            if (!gameObject.hasComponent(LayerableComponent.class))
                addPosition(pos.getX(), pos.getZ(), gameObject.getId());

            this.gameObjects.put(gameObject.getId(), gameObject);
            return true;
        }

        Direction direction = gameObject.hasComponent(DirectionComponent.class) ? gameObject
                .getComponent(DirectionComponent.class).getDirection() : Direction.defaultDirection();
        List<TerrainPosition> relativePositions = Arrays.asList(offsetsComponent.getOffsetPositions(direction));
        List<TerrainPosition> positions = relativePositions.stream().map(p -> p.add(pos))
                .collect(Collectors.toList());

        if (checkIfSpace && !canGameObjectBePlaced(gameObject, pos))
            return false;

        positions.forEach(p -> {
            int idFromPosition = getIdFromPosition(p);
            if (this.gameObjects.containsKey(idFromPosition)) { // If something already here
                GameObject gameObj = this.gameObjects.get(idFromPosition);
                if (gameObj.hasComponent(RepleacableComponent.class) &&
                        gameObj.getComponent(RepleacableComponent.class).isRepleacable()) // If replaceable
                    gameObj.destroy(); // Then destroy it before placing the new one
            }
            if (!gameObject.hasComponent(LayerableComponent.class))
                addPosition(p.getX(), p.getZ(), gameObject.getId());
        });

        this.gameObjects.put(gameObject.getId(), gameObject);

        if (gameObject.hasComponent(ResourceRequirementComponent.class)) {
            ResourceRequirementComponent component = gameObject.getComponent(ResourceRequirementComponent.class);
            component.getPlacingRequirements().keySet().forEach(component::meetRequirement);
        }
        return true;
    }

    /**
     * Get id of GameObject at given position
     * returns -1 if the position is wrong and 0 if the position is empty
     */
    public int getIdFromPosition(TerrainPosition position) {
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

        Map<NodeRoad, Direction[]> nodes = new HashMap<>();

        getRoads().forEach(road -> {
            TerrainPosition pos = road.getComponent(PositionComponent.class).getPosition().toTerrainPosition();
            Direction[] directions = getConnectedDirections(pos, FILTER);
            if (directions.length >= 3)
                nodes.put(new NodeRoad(pos), directions);
        });

        for (Entry<NodeRoad, Direction[]> node : nodes.entrySet()) {
            NodeRoad nodeRoad = node.getKey();
            if (!roadGraph.getNodes().contains(nodeRoad))
                roadGraph.searchForNextNode(nodeRoad.getPosition(), node.getValue(), null);
        }

        return roadGraph;
    }

    /**
     * @param gameObjectPosition position of the GameObject
     * @param filter             filter on connections
     * @return the directions in which the GameObject is connected
     */
    public Direction[] getConnectedDirections(TerrainPosition gameObjectPosition, FilterGameObjectCallback filter) {
        GameObject gameObjectAtPosition = getGameObjectAtPosition(gameObjectPosition);

        if (gameObjectAtPosition == null || !gameObjectAtPosition.hasComponent(ConnectionsComponent.class))
            return new Direction[0];

        ConnectionsComponent<?> connectionsComponent = gameObjectAtPosition.getComponent(ConnectionsComponent.class);

        Set<Direction> directions = new TreeSet<>();

        for (Direction direction : Direction.values()) {
            if (connectionsComponent.isConnected(direction)) {
                GameObject connectedGameObject = getGameObjectFromId(connectionsComponent.getConnection(direction));
                if (connectedGameObject != null && filter.onFilter(connectedGameObject))
                    directions.add(direction);
            }
        }

        return directions.toArray(new Direction[0]);
    }

    /**
     * @param gameObject GameObject of which we want the neighbors
     * @param filter     filter on neighbors
     * @return set of neighbors : 0 <= size <= 4
     */
    public Set<GameObject> getNeighbors(GameObject gameObject, FilterGameObjectCallback filter) {
        Set<GameObject> neighbors = new HashSet<>();
        if (!gameObject.hasComponent(PositionComponent.class) || !gameObject.hasComponent(ConnectionsComponent.class))
            return neighbors;

        TerrainPosition fromPosition = gameObject.getComponent(PositionComponent.class).getPosition()
                .toTerrainPosition();

        ConnectionsComponent<?> connectionsComponent = gameObject.getComponent(ConnectionsComponent.class);

        Direction[] connectedDirections = getConnectedDirections(fromPosition, filter);
        for (Direction direction : connectedDirections)
            neighbors.add(getGameObjectFromId(connectionsComponent.getConnection(direction)));

        return neighbors;
    }

    /**
     * Get Neighbor in chosen direction
     *
     * @param gameObject GameObject reference
     * @param direction  direction in which to search for neighbor
     * @param filter     filter on neighbor
     * @return neighbor
     */
    public GameObject getNeighbor(GameObject gameObject, Direction direction, FilterGameObjectCallback filter) {
        if (gameObject == null || !gameObject.hasComponent(ConnectionsComponent.class))
            return null;

        GameObject neighbor = getGameObjectFromId(
                gameObject.getComponent(ConnectionsComponent.class).getConnection(direction));
        return filter.onFilter(neighbor) ? neighbor : null;
    }

    public RoadGraph getRoadGraphCopy() {
        return this.roadGraph.copy();
    }

    public RoadGraph getRoadGraph() {
        return this.roadGraph;
    }

    public void addBuildingRequirement(GameObject gameObject) {
        System.out.println("Updating " + gameObject.getClass().getSimpleName());
//        //TODO: If new road is not connected to anything, stop
        BuildingRequirementsService service = new BuildingRequirementsService(Set.of(gameObject), result -> {
            PathRenderer pathRenderer = PathRenderer.getInstance();
            if (result.keySet().isEmpty()) // No new paths
                return;

            pathRenderer.addToTempPathsList(result);
        });

        this.serviceManager.setServiceRunning(false);
        this.serviceManager.addService(service);
        this.serviceManager.execute();
    }

    public void updateBuildingRequirements() {
        System.out.println("Updating everything");
        //TODO: If new road is not connected to anything, stop
        BuildingRequirementsService service = new BuildingRequirementsService(
                Scene.getInstance().getGameObjectsForComponent(ResourceRequirementComponent.class, false), result -> {
            PathRenderer pathRenderer = PathRenderer.getInstance();
            if (result.keySet().equals(pathRenderer.getTempPathsList().keySet())) // No new paths
                return;

            pathRenderer.setTempPathsList(result);
        });
        this.serviceManager.setServiceRunning(false);
        this.serviceManager.addService(service);
        this.serviceManager.execute();
    }

    public void updateHighlightedPaths() {
        if (PathRenderer.getInstance().isUpdateNeeded()) {
            removePreviousHighlightedPaths();
            createNewHightlightedPaths();

            PathRenderer.getInstance().setUpdateNeeded(false);
        }
    }

    private void removePreviousHighlightedPaths() {
        Set<GameObject> routes = getGameObjectsOfType(Route.class, false);
        routes.forEach(gameObject -> removeGameObject(gameObject.getId()));
    }

    private void createNewHightlightedPaths() {
        PathRenderer.getInstance().getTempPathsList().forEach((path, color) -> {
            Vao vao = path.createVao();
            if (vao != null) {
                Model model = new Model(vao);
//                model.setModelTexture(new ModelTexture(Utils.encodeColor(color)));
                Route route = new Route(model);
            }
        });
    }

    public Set<GameObject> getGameObjectsOfType(Class<? extends GameObject> gameObjectClass, boolean includePreviews) {
        Set<GameObject> collect = this.gameObjects.values().stream().filter(item -> item.getClass() == gameObjectClass)
                .collect(Collectors.toSet());
        if (includePreviews)
            collect.addAll(
                    this.previewedGameObjects.values().stream().filter(item -> item.getClass() == gameObjectClass)
                            .collect(Collectors.toSet()));

        return collect;
    }

    /**
     * Method created for testing purposes
     */
    public void resetObjects() {
        this.gameObjects.clear();
        GameObject.reset();
        this.idGameObjectsForComponents.clear();
        this.renderableGameObjects.clear();
        this.previewedGameObjects.clear();
        this.serviceManager.clear();
        this.positions = new int[500][500];
    }

    /**
     * Testing purposes
     */
    public void resetRoadGraph() {
        this.roadGraph = new RoadGraph();
    }

    public Terrain getTerrain() {
        return this.terrain;
    }

    /**
     * Offsets previewed GameObjects by given amount
     *
     * @param rotationOffset rotation to apply to all previewed GameObjects
     */
    public void rotatePreview(int rotationOffset) {
        Player.setDirection(Player.getDirection().add(rotationOffset));
        this.previewedGameObjects.values().stream()
                .filter(gameObject -> gameObject.hasComponent(DirectionComponent.class))
                .map(gameObject -> gameObject.getComponent(DirectionComponent.class))
                .forEach(directionComponent -> directionComponent
                        .setDirection(directionComponent.getDirection().add(rotationOffset)));
    }

    public boolean isPreviewed(int idGameObject) {
        return this.previewedGameObjects.containsKey(idGameObject);
    }
}
