package scene;

import static pathfinding.RoadGraph.FILTER;

import engineTester.Game;
import engineTester.Rome;
import entities.Camera.Direction;
import entities.Entity;
import entities.ModelEntity;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import models.Model;
import pathfinding.NodeRoad;
import pathfinding.RoadGraph;
import renderEngine.GameObjectRenderer;
import renderEngine.MasterRenderer;
import renderEngine.PathRenderer;
import renderEngine.SkyboxRenderer;
import renderEngine.structures.Vao;
import scene.callbacks.FilterGameObjectCallback;
import scene.components.*;
import scene.components.requirements.ResourceRequirementComponent;
import scene.gameObjects.GameObject;
import scene.gameObjects.Player;
import scene.gameObjects.Route;
import scene.gameObjects.Terrain;
import scene.preview.PreviewedModelsSupplier;
import scene.preview.SinglePreviewsSupplier;
import services.BuildingRequirementsService;
import services.ServiceManager;
import terrain.TerrainPosition;
import util.DayNightCycle;
import util.Offset;
import util.math.Vector3f;

public class Scene {

    private static final float MAX_SLOPE = 0.05f; // Max slope on which a GameObject can be placed

    private final Map<Class<? extends Component>, Set<Integer>> idGameObjectsForComponents = new HashMap<>();

    private final Map<Integer, GameObject> gameObjects = new HashMap<>();

    //    private final Map<GameObjectRenderer<?>, Set<GameObject>> renderableGameObjects = new HashMap<>();
    private final Map<GameObjectRenderer<?>, Set<Entity>> modelsToRender = new HashMap<>();

    private PreviewedModelsSupplier previewedModelsSupplier;

    private int[][] positions = new int[Game.TERRAIN_WIDTH][Game.TERRAIN_DEPTH];

    private final ServiceManager<BuildingRequirementsService> serviceManager = new ServiceManager<>();

    private boolean boundingBoxesDisplayed;

    private RoadGraph roadGraph;

    private Terrain terrain;

    public Scene() {
        this.roadGraph = createRoadGraph();
        this.previewedModelsSupplier = SinglePreviewsSupplier.getInstance();
        this.boundingBoxesDisplayed = false;
    }

    public boolean addGameObject(GameObject gameObject) {
        if (gameObject instanceof Terrain)
            this.terrain = (Terrain) gameObject;

        if (gameObject.getPosition().isTerrainPosition()) { // Objects belongs on terrain and has offset
            return placeGameObjectOnTerrain(gameObject, gameObject.getPosition().toTerrainPosition(), true);
        } else
            this.gameObjects.put(gameObject.getId(), gameObject);

        return true;
    }

    public void addPosition(int x, int z, int value) {
        assert value > 0;
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

        if (gameObject.isPlaced()) {
            Vector3f position = gameObject.getPosition();
            if (position != null && position.isTerrainPosition()) { // Object belongs on Terrain
                if (gameObject.hasComponent(OffsetsComponent.class)) {
                    OffsetsComponent offsetsComponent = gameObject.getComponent(OffsetsComponent.class);
                    Direction direction = gameObject.hasComponent(DirectionComponent.class) ? gameObject
                            .getComponent(DirectionComponent.class).getDirection() : Direction.defaultDirection();
                    Arrays.stream(offsetsComponent.getLocalOffsetPositions(direction))
                            .map(pos -> pos.add(position.toTerrainPosition()))
                            .forEach(pos -> addPosition(pos.getX(), pos.getZ(), 0));
                }
            }
        }
        removeGameObjectFromRender(gameObject);
    }

    public int[][] getPositions() {
        return this.positions;
    }

    public void addRenderableGameObject(GameObjectRenderer<?> gameObjectRenderer, GameObject gameObject,
            boolean displayBB) {
        if (!this.modelsToRender.containsKey(gameObjectRenderer))
            this.modelsToRender.put(gameObjectRenderer, new HashSet<>());

        Entity entityFromGameObject = GameObject.createEntityFromGameObject(gameObject, displayBB);
        this.modelsToRender.get(gameObjectRenderer).add(entityFromGameObject);
    }

    public void removeGameObjectFromRender(GameObject gameObject) {
        if (gameObject.hasComponent(RendererComponent.class)) {
            RendererComponent gameObjectRenderer = gameObject.getComponent(RendererComponent.class);

            GameObjectRenderer<?> renderer = gameObjectRenderer.getRenderer();
            renderer.removeGameObject(gameObject);
            if (this.modelsToRender.containsKey(renderer))
                this.modelsToRender.get(renderer).remove(gameObject);
        }
    }

    public void render() {
        MasterRenderer.getInstance().prepare();
        SkyboxRenderer.getInstance().render();

        Map<GameObjectRenderer<?>, Set<Entity>> modelsToRender = this.modelsToRender.entrySet().stream()
                .collect(Collectors.toMap(Entry::getKey, e -> new HashSet<>(e.getValue())));

        this.previewedModelsSupplier.get().stream().map(Entity::new).forEach(entity -> {
            GameObject previewedGameObj = GameObject.getGameObjectFromClass(Player.getSelectedGameObject());
            if (!previewedGameObj.hasComponent(RendererComponent.class))
                return;

            GameObjectRenderer<?> renderer = previewedGameObj.getComponent(RendererComponent.class).getRenderer();
            if (!modelsToRender.containsKey(renderer))
                modelsToRender.put(renderer, new HashSet<>());
            modelsToRender.get(renderer).add(entity);
        });

        modelsToRender.forEach((renderer, lEntities) -> {
            lEntities.forEach(renderer::addToRender);
            renderer.render();
        });
        DayNightCycle.incrementCycleTime();
    }

    public Set<GameObject> getGameObjects() {
        return new HashSet<>(this.gameObjects.values());
    }

    public GameObject getGameObjectAtPosition(int x, int z) {
        int id = getIdFromPosition(x, z);
        if (id == -1)
            return null;

        return getGameObjectFromId(id);
    }

    public GameObject getGameObjectFromId(int id) {
        if (this.gameObjects.containsKey(id))
            return this.gameObjects.get(id);
        return null;
    }

    /**
     * Add given position to the list of previewed positions
     * Doesn't check if the previewed element can actually fit at terrainPoint
     *
     * @param gameObjClass class of the previewed game object
     * @param position position
     * @param direction rotation
     */
    public void addToPreview(Class<? extends GameObject> gameObjClass, TerrainPosition position,
            Direction direction) {
        GameObject gameObject = GameObject.getGameObjectFromClass(gameObjClass);
        if (gameObject == null)
            return;

        if (gameObject.hasComponent(PreviewComponent.class)) {
            PreviewComponent previewComponent = gameObject.getComponent(PreviewComponent.class);
            float scale = 1.0f;
            if (gameObject.hasComponent(ScaleComponent.class))
                scale = gameObject.getComponent(ScaleComponent.class).getScale();
            Vector3f pos = position.toVector3f();
            if (gameObject.hasComponent(OffsetComponent.class))
                pos.add(gameObject.getComponent(OffsetComponent.class).getOffset());
            this.previewedModelsSupplier = previewComponent.getPreviewSupplier();
            this.previewedModelsSupplier.add(new ModelEntity(pos, direction, scale,
                    previewComponent.getModel(), -1));
        }
    }

    /**
     * Applies the offset to the previewed models
     *
     * @param offset the offset applied
     */
    public void offsetPreview(Offset offset) {
        this.previewedModelsSupplier.offset(offset);
    }

    /**
     * Resets all the positions that were previously previewed
     */
    public void resetPreviewedPositions() {
        this.previewedModelsSupplier.clear();
    }

    /**
     * @return preview direction
     */
    public Direction getPreviewDirection() {
        return this.previewedModelsSupplier.getDirection();
    }

    /**
     * @return true is there's any position previewed
     */
    public boolean isAnyPreviewedPositions() {
        return this.previewedModelsSupplier.isAny();
    }

    /**
     * @return the unmodifiable previewed positions
     */
    public Set<TerrainPosition> getPreviewedPositions() {
        return this.previewedModelsSupplier.getPositions();
    }

    public Map<Class<? extends Component>, Set<Integer>> getIdGameObjectsForComponents() {
        return this.idGameObjectsForComponents;
    }

    public Set<Integer> getIdGameObjectsForComponentClass(Class<? extends Component> clazz) {
        if (!this.idGameObjectsForComponents.containsKey(clazz))
            return new HashSet<>();

        return this.idGameObjectsForComponents.get(clazz);
    }

    public Set<GameObject> getGameObjectsForComponent(Class<? extends Component> clazz) {
        return getIdGameObjectsForComponentClass(clazz).stream()
                .map(this::getGameObjectFromId).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public boolean isPositionOccupied(int x, int z) {
        int idFound = getIdFromPosition(x, z);
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
        for (var preview : this.previewedModelsSupplier.get()) {
            System.out.println("Placing at " + preview.getPosition());
            Direction direction = Direction.getDirectionFromDegree((int) preview.getRotation().getY());
            GameObject.newInstance(Player.getSelectedGameObject(),
                    preview.getPosition().toTerrainPosition(), direction, true);
        }
        resetPreviewedPositions();
    }

    public int getMaxPeopleCapacity() {
        return this.gameObjects.values().stream()
                .map(gameObject -> gameObject.getComponent(ResidenceComponent.class))
                .filter(Objects::nonNull).mapToInt(ResidenceComponent::getMaxPeopleCapacity).sum();
    }

    public Set<GameObject> getRoads() {
        return new HashSet<>(getGameObjectsForComponent(RoadComponent.class));
    }

    public boolean canGameObjectClassBePlaced(Class<? extends GameObject> gameObjectClass,
            TerrainPosition pos, Direction direction) {
        if (gameObjectClass == null)
            return false;

        GameObject objectFromClass = GameObject.getGameObjectFromClass(gameObjectClass);
        if (objectFromClass == null)
            return false;
        if (objectFromClass.hasComponent(DirectionComponent.class))
            objectFromClass.getComponent(DirectionComponent.class).setDirection(direction);
        else
            objectFromClass.addComponent(new DirectionComponent(direction));
        if (objectFromClass.hasComponent(PreviewComponent.class))
            objectFromClass.getComponent(PreviewComponent.class).setPreviewPosition(pos);

        return canGameObjectBePlaced(objectFromClass, pos);
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
                return !isPositionOccupied(pos.getX(), pos.getZ());
            GameObject gameObjectAtPosition = getGameObjectAtPosition(pos.getX(), pos.getZ());
            if (gameObjectAtPosition == null)
                return false;
            return layerableComponent.getLayerableGameObjectsClasses()
                    .contains(gameObjectAtPosition.getClass()) || !isPositionOccupied(pos.getX(), pos.getZ());
        }

        Direction direction = objectToPlace.hasComponent(DirectionComponent.class) ? objectToPlace
                .getComponent(DirectionComponent.class).getDirection() : Direction.defaultDirection();

        if (!isTerrainFitForGameObject(objectToPlace, pos.getX(), pos.getZ()))
            return false;

        List<TerrainPosition> relativePositions = Arrays.asList(offsetsComponent.getLocalOffsetPositions(direction));
        List<TerrainPosition> positions = relativePositions.stream().map(p -> p.add(pos))
                .collect(Collectors.toList());

        if (!objectToPlace.hasComponent(LayerableComponent.class))
            return positions.stream().noneMatch(p -> isPositionOccupied(p.getX(), p.getZ()));

        return positions.stream().noneMatch(position -> {
            GameObject gameObjectAtPosition = getGameObjectAtPosition(position.getX(), position.getZ());
            if (gameObjectAtPosition != null)
                return !layerableComponent.getLayerableGameObjectsClasses()
                        .contains(gameObjectAtPosition.getClass()) && isPositionOccupied(pos.getX(), pos.getZ());
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
        List<TerrainPosition> offsetPositions = Arrays.asList(
                offsetsComponent.getOffsetPositions(direction, pos.getX(), pos.getZ()));

        if (checkIfSpace && !canGameObjectBePlaced(gameObject, pos))
            return false;

        // Now placing...
        offsetPositions.forEach(p -> {
            int idFromPosition = getIdFromPosition(p.getX(), p.getZ());
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

        float totalHeights = 0.0f;
        for (TerrainPosition offsetPosition : offsetPositions) {
            if (!isPositionOnTerrain(offsetPosition.getX(), offsetPosition.getZ()))
                return false;

            totalHeights += offsetPosition.getY();
        }

        float avgHeight = totalHeights / (float) offsetPositions.size();
        //TODO: Lisser le terrain
        HeightMapComponent heightMapComponent = this.terrain.getComponent(HeightMapComponent.class);
        for (TerrainPosition offsetPosition : offsetPositions) {
            heightMapComponent.setHeight(offsetPosition.getX(), offsetPosition.getZ(), avgHeight);
        }
        return true;
    }

    /**
     * Get id of GameObject at given position
     * returns -1 if the position is wrong and 0 if the position is empty
     */
    public int getIdFromPosition(int x, int z) {
        try {
            return this.positions[x][z];
        } catch (IndexOutOfBoundsException ignored) {
        }
        return -1;
    }

    public void enableBoundingBoxes() {
        if (this.boundingBoxesDisplayed)
            return;

        this.boundingBoxesDisplayed = true;
        this.modelsToRender.clear();
        this.gameObjects.forEach((id, gameObject) -> {
            if (gameObject.hasComponent(RendererComponent.class)) {
                RendererComponent rendererComponent = gameObject.getComponent(RendererComponent.class);
                GameObjectRenderer<?> renderer = rendererComponent.getRenderer();
                addRenderableGameObject(renderer, gameObject, true);
            }
        });
    }

    public void disableBoundingBoxes() {
        if (!this.boundingBoxesDisplayed)
            return;

        this.boundingBoxesDisplayed = false;
        this.modelsToRender.clear();
        this.gameObjects.forEach((id, gameObject) -> {
            if (gameObject.hasComponent(RendererComponent.class)) {
                RendererComponent rendererComponent = gameObject.getComponent(RendererComponent.class);
                GameObjectRenderer<?> renderer = rendererComponent.getRenderer();
                addRenderableGameObject(renderer, gameObject, false);
            }
        });
    }

    public boolean areBoundingBoxesDisplayed() {
        return this.boundingBoxesDisplayed;
    }

    private RoadGraph createRoadGraph() {
        final RoadGraph roadGraph = new RoadGraph();

        Map<NodeRoad, Direction[]> nodes = new HashMap<>();

        getRoads().forEach(road -> {
            TerrainPosition pos = road.getPosition().toTerrainPosition();
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
     * @param filter filter on connections
     * @return the directions in which the GameObject is connected
     */
    public Direction[] getConnectedDirections(TerrainPosition gameObjectPosition, FilterGameObjectCallback filter) {
        GameObject gameObjectAtPosition = getGameObjectAtPosition(gameObjectPosition.getX(), gameObjectPosition.getZ());

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
     * @param filter filter on neighbors
     * @return set of neighbors : 0 <= size <= 4
     */
    public Set<GameObject> getNeighbors(GameObject gameObject, FilterGameObjectCallback filter) {
        Set<GameObject> neighbors = new HashSet<>();
        if (!gameObject.isPlaced() || !gameObject.hasComponent(ConnectionsComponent.class))
            return neighbors;

        TerrainPosition fromPosition = gameObject.getPosition().toTerrainPosition();

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
     * @param direction direction in which to search for neighbor
     * @param filter filter on neighbor
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
                Rome.getGame().getScene().getGameObjectsForComponent(ResourceRequirementComponent.class), result -> {
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
        Set<GameObject> routes = getGameObjectsOfType(Route.class);
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

    public Set<GameObject> getGameObjectsOfType(Class<? extends GameObject> gameObjectClass) {
        return this.gameObjects.values().stream()
                .filter(item -> item.getClass() == gameObjectClass).collect(Collectors.toSet());
    }

    /**
     * Method created for testing purposes
     */
    public void resetObjects() {
        this.gameObjects.clear();
        GameObject.reset();
        this.idGameObjectsForComponents.clear();
        this.modelsToRender.clear();
        this.serviceManager.clear();
        this.previewedModelsSupplier.clear();
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
     * Calculates the height difference on the Terrain at given coordinate and for the GameObject offset positions
     *
     * @param gameObject GameObject
     * @param x coordinate on Terrain
     * @param z coordinate on Terrain
     * @return true if the maximum slope under the GameObject is inferior to {@link Scene#MAX_SLOPE}
     * If any of the offset positions goes beyond the Terrain, return false
     */
    public boolean isTerrainFitForGameObject(GameObject gameObject, int x, int z) {
        if (!isPositionOnTerrain(x, z))
            return false;

        if (!gameObject.hasComponent(OffsetsComponent.class))
            return true;

        OffsetsComponent offsetsComponent = gameObject.getComponent(OffsetsComponent.class);
        Direction direction = Direction.defaultDirection();
        if (gameObject.hasComponent(DirectionComponent.class))
            direction = gameObject.getComponent(DirectionComponent.class).getDirection();

        // First, check if any of the positions goes beyond the Terrain
        TerrainPosition[] offsetPositions = offsetsComponent.getOffsetPositions(direction, x, z);
        float minHeight = Float.MAX_VALUE;
        float maxHeight = Float.MIN_VALUE;

        for (TerrainPosition pos : offsetPositions) {
            if (!isPositionOnTerrain(pos.getX(), pos.getZ()))
                return false;

            float y = pos.getY();
            if (y < minHeight)
                minHeight = y;
            if (y > maxHeight)
                maxHeight = y;
        }

        // Terrain too steep to place GameObject?
        return maxHeight - minHeight <= MAX_SLOPE;
    }

    /**
     * @return true if position is on Terrain
     */
    public boolean isPositionOnTerrain(double x, double z) {
        return x >= 0 && z >= 0 && x < Game.TERRAIN_WIDTH - 1 && z < Game.TERRAIN_DEPTH - 1;
    }
}