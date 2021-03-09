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
            Direction[] directionsNextRoadOnlyRoads = terrain.getConnectionsToRoadItem(nextRoadPosition, true);

            Direction oppositeDirection = direction.getOppositeDirection();

            if (directionsNextRoadOnlyRoads.length == 0) // Impossible ?
                return;
//                throw new IllegalStateException("No direction, impossible state.");

            if (directionsNextRoadOnlyRoads.length == 1) // Dead end
                continue;

            if (directionsNextRoadOnlyRoads.length > 2) {
                // Node found (directionsNextRoad.length > 3 && directionsNextRoad.length <= 4)
                RoadNode newNode = new RoadNode(nextRoadPosition);
                RouteRoad newRoute = new RouteRoad(currentRoute);
                newRoute.setEnd(newNode);
                newRoute.addRoad(newNode);

                routes.add(newRoute);
                routes.add(newRoute.invertRoute());

                Set<RouteRoad> routesToDelete = new HashSet<>();
                routes.stream().filter(route ->
                        (route.getEnd().equals(newRoute.getEnd()) && !newRoute.getStart().equals(route.getStart()) &&
                                route.getRoute().contains(newRoute.getStart())) ||
                                (route.getEnd().equals(newRoute.getStart()) &&
                                        !newRoute.getEnd().equals(route.getStart()) &&
                                        route.getRoute().contains(newRoute.getEnd())))
                        .forEach(routesToDelete::add);

                routes.removeAll(routesToDelete);

                List<Direction> directionList = new ArrayList<>(Arrays.asList(directionsNextRoadOnlyRoads));
                directionList.remove(oppositeDirection);
                if (!nodes.contains(new RoadNode(nextRoadPosition)))
                    searchForNextNode(nextRoadPosition, directionList.toArray(new Direction[0]), null);
                continue;
            }

            // 2 sides connected
            Direction nextDir = null;
            for (Direction nextDirection : directionsNextRoadOnlyRoads)
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

    /**
     * Add road to position dynamically
     */
    public void addRoad(TerrainPosition position) {
        Direction[] connectionsToRoadItem = Terrain.getInstance().getConnectionsToRoadItem(position, true);
        if (connectionsToRoadItem.length >= 3) { // New road is a node
            searchForNextNode(position, connectionsToRoadItem, null);
        } else {
            NormalRoad road = new NormalRoad(position);
            RouteRoad[] closestNodes = RouteFinder.getClosestNodes(road);
            if (closestNodes[0] != null && closestNodes[1] != null && connectionsToRoadItem.length == 2) {
                RouteRoad routeRoad1 = closestNodes[0].invertRoute();
                RouteRoad routeRoad2 = closestNodes[1];
                if (!routeRoad1.getStart().equals(routeRoad2.getEnd())) {
                    RouteRoad routeRoad = new RouteRoad(routeRoad1.getStart(), routeRoad2.getEnd());
                    routeRoad1.getRoute().forEach(routeRoad::addRoad);
                    routeRoad.addRoad(road);
                    routeRoad2.getRoute().forEach(routeRoad::addRoad);

                    nodes.add((RoadNode) routeRoad1.getStart());
                    nodes.add((RoadNode) routeRoad2.getEnd());

                    routes.add(routeRoad);
                    routes.add(routeRoad.invertRoute());
                }
            } /*else {*/
            for (Direction direction : connectionsToRoadItem) {
                TerrainPosition newPos = position.add(Direction.toRelativeDistance(direction));
                Direction[] directions = Terrain.getInstance().getConnectionsToRoadItem(newPos, true);
                if (directions.length == 3) { // New node created by this road
                    searchForNextNode(newPos, directions, null);
                }

                if (directions.length > 2 && connectionsToRoadItem.length > 1) { // See Test 19 to see why!
                    RouteRoad route = new RouteRoad(new RoadNode(newPos));
                    route.addRoad(new NormalRoad(position));

                    Direction[] newDirections = new Direction[connectionsToRoadItem.length - 1];
                    int i = 0;
                    for (Direction dir : connectionsToRoadItem) {
                        if (dir != direction)
                            newDirections[i++] = dir;
                    }
                    searchForNextNode(position, newDirections, route);
                }
            }
//            }
        }
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