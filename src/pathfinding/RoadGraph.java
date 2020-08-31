package pathfinding;

import entities.Camera.Direction;
import java.util.*;
import java.util.stream.Collectors;
import terrains.Terrain;
import util.math.Vector2f;

public class RoadGraph {

    private Set<RoadNode>        nodes;
    private SortedSet<RouteRoad> routes;

    public RoadGraph() {
        this.nodes = new HashSet<>();
        this.routes = new TreeSet<>();
    }

    public void searchForNextNode(Vector2f position, Direction[] directions, RouteRoad currentRoute) {
        if (currentRoute == null) {
            RoadNode start = new RoadNode(position);
            currentRoute = new RouteRoad(start);
            nodes.add(start);
        }

        for (Direction direction : directions) {
            Vector2f nextRoadPosition = position.add(Direction.toRelativeDistance(direction));

            Terrain terrain = Terrain.getInstance();
            Direction[] directionsNextRoad = terrain.getRoadDirections(nextRoadPosition);
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
                currentRoute.setEnd(newNode);

                routes.add(currentRoute);
                RouteRoad e = currentRoute.invertRoute();
                routes.add(e);

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