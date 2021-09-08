package scene.components;

import display.DisplayManager;
import entities.Camera.Direction;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;
import pathfinding.NodeRoad;
import pathfinding.NormalRoad;
import pathfinding.Path;
import pathfinding.PathFinder;
import pathfinding.Road;
import pathfinding.RoadGraph;
import scene.Scene;
import scene.components.PedestrianComponent.Behavior;
import scene.gameObjects.GameObject;
import terrain.TerrainPosition;
import util.math.Vector2f;
import util.math.Vector3f;

public class PathComponent extends Component {

    private PedestrianComponent   pedestrianComponent;
    private TransparencyComponent transparencyComponent;
    private PositionComponent     positionComponent;

    //    // Can be null if the path starts on a road
//    private GameObject startBuilding;
//
//    // Can be null if the path ends on a road
//    private GameObject endBuilding;
    private boolean atEndBuilding;

    private PathType  pathType;
    private Path      path;
    private Road      currentRoad;
    private Direction direction;

    public PathComponent() {
    }

    public void createPathComponent(GameObject gameObject, Vector3f position, PathType pathType) {
        gameObject.addComponent(this.positionComponent = new PositionComponent(position));

        this.direction = null;

        this.pedestrianComponent = gameObject.getComponent(PedestrianComponent.class);
        this.transparencyComponent = gameObject.getComponent(TransparencyComponent.class);
        this.pathType = pathType;
    }

    public Path getPath() {
        return this.path;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public PathType getPathType() {
        return this.pathType;
    }

    public void setPath(Path path) {
        if (path == null || this.positionComponent.position == null)
            return;

        TerrainPosition terrainPosition = this.positionComponent.position.toTerrainPosition(); // Current position
        Road closestRoad = path.getClosestRoad(terrainPosition);
        if (closestRoad != null) { // Found closest road
            RoadGraph roadGraph = new RoadGraph(
                    path.getAllRoads().stream().filter(NodeRoad.class::isInstance).map(NodeRoad.class::cast)
                            .collect(Collectors.toSet()), new TreeSet<>(path));

            PathFinder pathFinder = new PathFinder(roadGraph);
            Path bestPath = pathFinder.findBestPath(closestRoad.getPosition(), path.getEnd().getPosition(), 0);

            pathFinder = new PathFinder(Scene.getInstance().getRoadGraph());
            Path NPCToRoad = pathFinder.findBestPath(terrainPosition, closestRoad.getPosition(), 0);

            this.path = Path.mergePaths(NPCToRoad, bestPath);
        }
    }

    public void moveForward() {
        if (this.path == null || this.positionComponent.position == null)
            return;

        if (this.currentRoad == null)
            this.currentRoad = this.path.getRoadAt(this.positionComponent.position.toTerrainPosition());

        if (this.currentRoad == null)
            return;

        List<Road> roadList = this.path.getAllUniqueRoads();
        int index = roadList.indexOf(this.currentRoad);
        if (index < 0 && !this.atEndBuilding)
            return;

        if (this.pedestrianComponent == null)
            return;

        float speed = this.pedestrianComponent.getBehavior().getSpeed();
        Road nextRoad;
        if (index >= roadList.size() - 1 || this.atEndBuilding) { // End
            GameObject endBuilding = this.pathType.getEndBuilding();
            if (endBuilding == null)
                return;

            if (!this.atEndBuilding && endBuilding.hasComponent(ConnectionsComponent.class) &&
                    endBuilding.getComponent(ConnectionsComponent.class).getConnectionTypeClass() == Road.class) {
                ConnectionsComponent<Road> roadCnsCmpnt = endBuilding.getComponent(ConnectionsComponent.class);
                if (roadCnsCmpnt == null)
                    return;

                int x = this.currentRoad.getPosition().getX();
                int z = this.currentRoad.getPosition().getZ();

                Direction toBuildingDirection = null;
                int[][] positions = Scene.getInstance().getPositions();
                for (Direction direction : Direction.values()) {
                    TerrainPosition d = Direction.toRelativeDistance(direction);
                    int id = positions[x + d.getX()][z + d.getZ()];
                    if (id == endBuilding.getId()) {
                        if (roadCnsCmpnt.getAccessPoints()[direction.toOppositeDirection().ordinal()]) {
                            // Building can be entered
//                        System.out.println("found endbuilding");
                            toBuildingDirection = direction;
                            this.atEndBuilding = true;
                            break;
                        }
                    }
                }
                if (toBuildingDirection == null) // No viable connection found
                    return;

                this.direction = toBuildingDirection;
                nextRoad = new NormalRoad(this.currentRoad.getPosition()
                        .add(Direction.toRelativeDistance(toBuildingDirection))); // False road to move towards it
                this.currentRoad = nextRoad;
            } else {
                nextRoad = this.currentRoad;
            }
            if (this.transparencyComponent != null) {
                this.transparencyComponent
                        .setAlpha(this.transparencyComponent.getAlpha() - speed / DisplayManager.TPS);

                if (this.transparencyComponent.getAlpha() == 0) {
                    Scene.getInstance().removeGameObject(this.idGameObject);
                }
            }
            Vector3f diff = nextRoad.getPosition().toVector3f().sub(this.positionComponent.position);
            if (diff.getX() < 0.5f && diff.getZ() < 0.5f)
                this.pedestrianComponent.setBehavior(Behavior.WALKING); // Approches building
        } else {
            nextRoad = roadList.get(index + 1);

            Vector3f vector3f = nextRoad.getPosition().toVector3f().sub(this.positionComponent.position);
            Direction newDirection = Direction.getDirectionFromVector(new Vector2f(vector3f.getX(), vector3f.getZ()));
            if (this.direction == null)
                this.direction = newDirection;
            else if (newDirection == this.direction.toOppositeDirection()) {
                // Now on next road
                this.direction = null;
            } else {
                this.direction = newDirection;
            }
        }
        if (this.direction == null) {
            this.currentRoad = nextRoad;
            return;
        }

        Vector3f totalDistanceLeft = nextRoad.getPosition().toVector3f().sub(this.positionComponent.position);
        Vector3f distance;
        if (DisplayManager.IS_DEBUG)
            distance = Direction.toRelativeDistance(this.direction, speed / 20);
        else
            distance = Direction.toRelativeDistance(this.direction, speed / DisplayManager.TPS);

        distance.setX(Math.min(distance.getX(), totalDistanceLeft.getX()));
        distance.setZ(Math.min(distance.getZ(), totalDistanceLeft.getZ()));

        if (Direction.getDirectionFromVector(new Vector2f(distance.getX(), distance.getZ())) == this.direction)
            this.positionComponent.position.add(distance);
    }

    public enum PathType {
        ROAD_TO_ROAD,
        ROAD_TO_BUILDING,
        BUILDING_TO_BUILDING,
        BUILDING_TO_ROAD;

        protected GameObject startBuilding;
        protected GameObject endBuilding;

        public void setStartBuilding(GameObject startBuilding) {
            this.startBuilding = startBuilding;
        }

        public void setEndBuilding(GameObject endBuilding) {
            this.endBuilding = endBuilding;
        }

        public GameObject getStartBuilding() {
            return this.startBuilding;
        }

        public GameObject getEndBuilding() {
            return this.endBuilding;
        }
    }
}