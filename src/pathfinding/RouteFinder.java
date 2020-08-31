package pathfinding;

import entities.Camera.Direction;
import items.ConnectableItem;
import items.Item;
import items.PlaceHolderConnectableItem;
import items.buildings.BuildingItem;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import terrains.Terrain;
import util.math.Vector2f;

public class RouteFinder {

    private final RoadGraph roadGraph;

    private RoadNode startNode;
    private RoadNode endNode;

    // Optional if extremities != start or end
    private RouteRoad fromStart;
    private RouteRoad fromEnd;

    private Set<RouteRoad> route = new LinkedHashSet<>();

    public RouteFinder(RoadGraph roadGraph) {
        this.roadGraph = roadGraph;
    }

    private void findAndSetStartNode(Road startNode) {
        if (startNode instanceof RoadNode) // RoadNode
            this.startNode = (RoadNode) startNode;
        else { // Not a RoadNode
            this.fromStart = getClosestNode((NormalRoad) startNode);
            if (this.fromStart != null) {
                this.startNode = (RoadNode) this.fromStart.getEnd();
                this.route.add(this.fromStart);
            }
        }
    }

    private void findAndSetEndNode(Road endNode) {
        if (endNode instanceof RoadNode) // RoadNode
            this.endNode = (RoadNode) endNode;
        else { // Not a RoadNode
            this.fromEnd = getClosestNode((NormalRoad) endNode);
            if (this.fromEnd != null)
                this.endNode = (RoadNode) this.fromEnd.getEnd();
        }

        if (this.endNode == null)
            return;

        this.roadGraph.getRoutes().forEach(routeRoad -> {
            Road roadStart = routeRoad.getStart();
            Road roadEnd = routeRoad.getStart();

            int hScoreStart = manhattanDistance(roadStart, endNode);
            int hScoreEnd = manhattanDistance(roadEnd, endNode);
            this.roadGraph.getNodes().forEach(roadNode -> {
                if (roadNode.getPosition().equals(roadStart.getPosition()))
                    roadNode.sethScore(hScoreStart);
            });
            roadStart.sethScore(hScoreStart);
            roadEnd.sethScore(hScoreEnd);

            routeRoad.getRoute().forEach(road -> road.sethScore(manhattanDistance(road, endNode)));
        });

        if (this.startNode != null)
            this.startNode.sethScore(manhattanDistance(this.startNode, endNode));
        this.endNode.sethScore(manhattanDistance(this.endNode, endNode)); // = 0 ?
    }

    public void findRoute(Vector2f from, BuildingItem toItem, int maxRouteLength) {
        if (toItem == null || from == null || maxRouteLength < 0)
            return;

       Terrain terrain = Terrain.getInstance();

        Set<Set<RouteRoad>> foundRoutes = new TreeSet<>(
                Comparator.comparingInt(o -> o.stream().mapToInt(RouteRoad::getgScore).sum()));

        Set<Vector2f> foundBuildings = new HashSet<>();
        terrain.getItems().entrySet().stream().filter(entry -> entry.getValue() instanceof ConnectableItem &&
                entry.getValue().getName().equals(toItem.getName()) && !foundBuildings.contains(entry.getKey()))
                .forEach(entry -> {
                    Vector2f pos = entry.getKey();
                    Item item = entry.getValue();
                    ConnectableItem connectableItem = (ConnectableItem) item;
                    for (Direction direction : Direction.values()) {
                        if (connectableItem.isConnected(direction)) {
                            Vector2f offset;
                            if (item instanceof PlaceHolderConnectableItem) {
                                PlaceHolderConnectableItem placeHolder = (PlaceHolderConnectableItem) item;
                                pos = Vector2f.sub(pos, placeHolder.getRelativePosition(), null);
                                // Pos = parent coordinates
                                offset = placeHolder.getOffset(direction);
                            } else
                                offset = item.getOffset(direction);

                            // pos = parent of building position
                            foundBuildings.add(pos);
                            Vector2f to = pos.add(offset).add(Direction.toRelativeDistance(direction));
                            Set<RouteRoad> route = findRoute(from, to, maxRouteLength);
                            if (route != null && !route.isEmpty())
                                foundRoutes.add(route);
                        }
                    }
                });

        route = foundRoutes.stream().findFirst().orElse(new LinkedHashSet<>());
    }

    public Set<RouteRoad> findRoute(Vector2f from, Vector2f to, int maxRouteLength) {
        Road start = Terrain.getInstance().getRoad(from);
        Road end = Terrain.getInstance().getRoad(to);

        if (start == null || end == null) // One of the coordinates given != road
            return null;

        boolean commonRoute = false;

        findAndSetStartNode(start);
        findAndSetEndNode(end);

        if (this.startNode != null && this.startNode.equals(this.endNode)) {
            if (fromStart != null)
                route.add(fromStart);
            if (fromEnd != null)
                route.add(fromEnd.invertRoute());

            if (maxRouteLength > 0 && maxRouteLength < route.stream().mapToInt(RouteRoad::getgScore).sum())
                route.clear();
            return route;
        }

        if (this.startNode != null || this.endNode != null)
            commonRoute = roadGraph.getRoutes().stream()
                    .anyMatch(routeRoad -> routeRoad.getRoute().contains(start) && routeRoad.getRoute().contains(end));
        if ((this.startNode == null && this.endNode == null) || commonRoute) {
            // Both start and end belong to the same route, no need to check for nodes
            // That or there are no nodes connected to either

            // Need to find the end from start without nodes
            RouteRoad routeWithoutNodes = findRouteWithoutNodes((NormalRoad) start, (NormalRoad) end);
            if (routeWithoutNodes != null)
                route.add(routeWithoutNodes);

            if (maxRouteLength > 0 && maxRouteLength < route.stream().mapToInt(RouteRoad::getgScore).sum())
                route.clear();
            return route;
        }

        if (this.startNode == null || this.endNode == null) { // No node near start or end
            NormalRoad normalRoad;
            if (this.startNode == null)
                normalRoad = (NormalRoad) start;
            else
                normalRoad = (NormalRoad) end;

            RouteRoad closestNode = getClosestNode(normalRoad);
            if (closestNode != null)
                route.add(closestNode);

            if (maxRouteLength > 0 && maxRouteLength < route.stream().mapToInt(RouteRoad::getgScore).sum())
                route.clear();
            return route;
        }

        LinkedList<SortedSet<RouteRoad>> listPossibleRoutes = new LinkedList<>();
        Set<RoadNode> usedNodes = new HashSet<>();

        RoadNode node = this.startNode;
        while (usedNodes.size() < roadGraph.getNodes().size()) {
            usedNodes.add(node);

            Set<RouteRoad> routes = roadGraph.getRoutes(node);
            SortedSet<RouteRoad> possibleRoutes = new TreeSet<>(routes);
            listPossibleRoutes.add(possibleRoutes);

            // Get cheapest road
            SortedSet<RouteRoad> cheapestRoutes = new TreeSet<>();
            listPossibleRoutes.forEach(routeRoads -> {
                routeRoads.stream()
                        .filter(routeRoad -> routeRoad.getEnd() instanceof RoadNode)
                        .filter(routeRoad -> !usedNodes.contains(routeRoad.getEnd())).findFirst()
                        .ifPresent(cheapestRoutes::add);
            });
            RouteRoad cheapestRoute = cheapestRoutes.stream().findFirst().orElse(null);
            if (cheapestRoute == null)
                return null;

            Set<SortedSet<RouteRoad>> removedRoutes = listPossibleRoutes.stream().filter(routeRoads -> {
                RouteRoad routeRoad = routeRoads.stream().findFirst().orElse(null);
                if (routeRoad != null) {
                    return (routeRoad.getEnd().gethScore() + routeRoad.getgScore()) <
                            (cheapestRoute.getgScore() + cheapestRoute.getEnd().gethScore());
                }
                return false;
            }).collect(Collectors.toSet());
            removedRoutes.forEach(routeRoads -> route.removeAll(routeRoads));
            listPossibleRoutes.removeAll(removedRoutes);
            // End getting cheapest road

            node = (RoadNode) cheapestRoute.getEnd();

            route.add(cheapestRoute);
            if (node.equals(this.endNode)) {
                usedNodes.add(this.endNode);

                if (fromEnd != null)
                    route.add(fromEnd.invertRoute());

                if (maxRouteLength > 0 && maxRouteLength < route.stream().mapToInt(RouteRoad::getgScore).sum())
                    route.clear();
                return route;
            }
        }
        return null;
    }

    private RouteRoad findRouteWithoutNodes(NormalRoad start, NormalRoad end) {
        final Terrain terrain = Terrain.getInstance();
        Direction[] connectionsToRoadItem = terrain.getConnectionsToRoadItem(start.getPosition(), false);
        final Direction[] directions = terrain.getConnectionsToRoadItem(start.getPosition(), true);

        final int nbDir = directions.length;
        if (nbDir == 0 || connectionsToRoadItem.length == 0 || connectionsToRoadItem.length > 2 || nbDir > 2)
            return null;

        final CountDownLatch latch = new CountDownLatch(nbDir);
        final Runnable[] runnables = new Runnable[nbDir];
        final RouteRoad[] possibleRoutes = new RouteRoad[nbDir];

        try {
            for (int i = 0; i < nbDir; i++) {
                int finalI = i;
                runnables[i] = () -> {
                    Set<Road> roads = new LinkedHashSet<>();

                    Direction dir = directions[finalI];
                    Vector2f nextPos = start.getPosition().add(Direction.toRelativeDistance(dir));
                    Direction[] nextDirs = terrain.getConnectionsToRoadItem(nextPos, true);
                    int nbDirections = nextDirs.length;

                    while (!nextPos.equals(end.getPosition()) && nbDirections > 1) {
                        roads.add(new NormalRoad(nextPos));

                        for (Direction direction : nextDirs)
                            if (!direction.equals(dir.getOppositeDirection())) {
                                dir = direction;
                                break;
                            }

                        nextPos = nextPos.add(Direction.toRelativeDistance(dir));
                        nextDirs = terrain.getConnectionsToRoadItem(nextPos, true);
                        nbDirections = nextDirs.length;
                    }
                    if (nextPos.equals(end.getPosition())) {
                        possibleRoutes[finalI] = new RouteRoad(start, end, roads); // Node found, route added
                    }
                    latch.countDown();
                };
                runnables[i].run();
            }
            latch.await();

            if (nbDir == 1)
                return possibleRoutes[0];

            if (possibleRoutes[0] == null || possibleRoutes[1] == null)
                return null;

            return (possibleRoutes[0].getgScore() + possibleRoutes[0].getEnd().gethScore()) <
                    (possibleRoutes[1].getgScore() + possibleRoutes[1].getEnd().gethScore()) ? possibleRoutes[0]
                    : possibleRoutes[1];
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Set<RouteRoad> getRoute() {
        return this.route;
    }


    private int manhattanDistance(Road from, Road to) {
        Vector2f startPos = from.getPosition();
        Vector2f endPos = to.getPosition();

        return Math.abs((int) endPos.x - (int) startPos.x) + Math.abs((int) endPos.y - (int) startPos.y);
    }

    public RouteRoad getClosestNode(NormalRoad road) {
        final Terrain terrain = Terrain.getInstance();
        Direction[] connectionsToRoadItem = terrain.getConnectionsToRoadItem(road.getPosition(), false);
        final Direction[] directions = terrain.getConnectionsToRoadItem(road.getPosition(), true);

        final int nbDir = directions.length;
        if (nbDir == 0 || connectionsToRoadItem.length == 0 || connectionsToRoadItem.length > 2 || nbDir > 2)
            return null;

        final CountDownLatch latch = new CountDownLatch(nbDir);
        final Runnable[] runnables = new Runnable[nbDir];
        final RouteRoad[] possibleRoutes = new RouteRoad[nbDir];

        try {
            for (int i = 0; i < nbDir; i++) {
                int finalI = i;
                runnables[i] = () -> {
                    Set<Road> roads = new LinkedHashSet<>();

                    Direction dir = directions[finalI];
                    Vector2f nextPos = road.getPosition().add(Direction.toRelativeDistance(dir));
                    Direction[] nextDirs = terrain.getConnectionsToRoadItem(nextPos, false);
                    int nbDirections = nextDirs.length;

                    while (nbDirections == 2) {
                        roads.add(new NormalRoad(nextPos));

                        for (Direction direction : nextDirs)
                            if (!direction.equals(dir.getOppositeDirection())) {
                                dir = direction;
                                break;
                            }

                        nextPos = nextPos.add(Direction.toRelativeDistance(dir));
                        nextDirs = terrain.getConnectionsToRoadItem(nextPos, false);
                        nbDirections = nextDirs.length;
                    }
                    if (nbDirections > 2) {
                        RoadNode node = new RoadNode(nextPos);

                        possibleRoutes[finalI] = new RouteRoad(road, node, roads); // Node found, route added
                    }
                    latch.countDown();
                };
                runnables[i].run();
            }
            latch.await();

            if (nbDir == 1)
                return possibleRoutes[0];

            if (possibleRoutes[0] == null || possibleRoutes[1] == null)
                return null;

            return (possibleRoutes[0].getgScore() + possibleRoutes[0].getEnd().gethScore()) <
                    (possibleRoutes[1].getgScore() + possibleRoutes[1].getEnd().gethScore()) ? possibleRoutes[0]
                    : possibleRoutes[1];
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

}