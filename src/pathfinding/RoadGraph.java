package pathfinding;

import entities.Camera.Direction;
import java.util.*;
import java.util.stream.Collectors;
import terrains.Terrain;
import terrains.TerrainPosition;

public class RoadGraph {

    private       Set<RoadNode>        nodes;
    private final SortedSet<RouteRoad> routes;

    public RoadGraph() {
        this.nodes = new HashSet<>();
        this.routes = new TreeSet<>();
    }

    public void searchForNextNode(TerrainPosition position, Direction[] directions, RouteRoad currentRoute) {
        if (currentRoute == null) {
            RoadNode start = new RoadNode(position);
            currentRoute = new RouteRoad(start);

            nodes.add(start);
        }

        assert currentRoute.getEnd() == null;

        Terrain terrain = Terrain.getInstance();
        for (Direction direction : directions) {
            TerrainPosition nextRoadPosition = position.add(Direction.toRelativeDistance(direction));

//            Direction[] directionsNextRoad = terrain.getRoadDirections(nextRoadPosition);
            Direction[] directionsNextRoad = terrain.getConnectionsToRoadItem(nextRoadPosition, true);
            Direction[] directionsNextRoadOnlyRoads = terrain.getConnectionsToRoadItem(nextRoadPosition, true);

            Direction oppositeDirection = direction.getOppositeDirection();

            if (directionsNextRoadOnlyRoads.length == 0) // Impossible ?
                return;
//                throw new IllegalStateException("No direction, impossible state.");

            if (directionsNextRoadOnlyRoads.length == 1) // Dead end
                continue;

            if (directionsNextRoad.length > 2) {
                // Node found (directionsNextRoad.length > 3 && directionsNextRoad.length <= 4)
                RoadNode newNode = new RoadNode(nextRoadPosition);
                RouteRoad newRoute = new RouteRoad(currentRoute);
                newRoute.setEnd(newNode);
                newRoute.addRoad(newNode);

                routes.add(newRoute);
                routes.add(newRoute.invertRoute());

                directionsNextRoad = directionsNextRoadOnlyRoads;
                List<Direction> directionList = new ArrayList<>(Arrays.asList(directionsNextRoad));
                directionList.remove(oppositeDirection);
                if (!nodes.contains(new RoadNode(nextRoadPosition)))
                    searchForNextNode(nextRoadPosition, directionList.toArray(new Direction[0]), null);
                continue;
            }

            // 2 sides connected
            Direction nextDir = null;
            for (Direction nextDirection : directionsNextRoad)
                if (!oppositeDirection.equals(nextDirection))
                    nextDir = nextDirection;

            if (nextDir != null) {
                RouteRoad nextRoute = new RouteRoad(currentRoute);
                nextRoute.addRoad(new NormalRoad(nextRoadPosition)); // No node yet, add road

                searchForNextNode(nextRoadPosition, new Direction[]{nextDir}, nextRoute);
            }
        }
    }

    public void setNodes(Set<RoadNode> nodes) {
        this.nodes = nodes;
    }

    public Set<RoadNode> getNodes() {
        return this.nodes;
    }

    public Set<RouteRoad> getRoutes() {
        return this.routes;
    }

    public Set<RouteRoad> getRoutes(RoadNode node) {
        return routes.stream().filter(routeRoad -> routeRoad.getStart().getPosition().equals(node.getPosition()))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RoadGraph roadGraph = (RoadGraph) o;
        return Objects.equals(nodes, roadGraph.nodes) &&
                Objects.equals(routes, roadGraph.routes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodes, routes);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        routes.forEach(route -> stringBuilder.append(route).append("\n"));
        StringBuilder stringBuilder2 = new StringBuilder();
        nodes.forEach(node -> stringBuilder2.append(node).append("\n"));
        return "Graph{" +
                "nodes (" + nodes.size() + ") =" + stringBuilder2.toString() +
                "\t, routes(" + routes.size() + ") =" + stringBuilder.toString() +
                '}';
    }
}