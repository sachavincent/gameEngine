package pathfinding;

import com.sun.istack.internal.NotNull;
import entities.Camera.Direction;
import items.ConnectableItem;
import items.Item;
import items.PlaceHolderConnectableItem;
import items.buildings.BuildingItem;
import items.roads.RoadItem;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import terrains.Terrain;
import util.math.Vector2f;

public class RouteFinder {

    private final RoadGraph roadGraph;

    private RoadNode startNode;
    private RoadNode endNode;

    // Optional if extremities != start or end
    // Represent the routes from the normalRoad start to the closest 2 nodes
    private RouteRoad[] fromStart = new RouteRoad[2];
    private RouteRoad[] fromEnd   = new RouteRoad[2];

    private boolean ambigueousStart, ambigueousEnd;

    private Route<RouteRoad> route = new Route<>();

    public RouteFinder(RoadGraph roadGraph) {
        this.roadGraph = roadGraph;
    }

    /**
     * true if fromStart is not empty
     */
    private boolean isStartAmbigeous() {
        return ambigueousStart;
    }

    /**
     * true if fromEnd is not empty
     */
    private boolean isEndAmbigeous() {
        return ambigueousEnd;
    }

    private void findAndSetStartNode(Road startRoad) {
        if (startRoad instanceof RoadNode) {// RoadNode
            this.startNode = (RoadNode) startRoad;
        } else { // Not a RoadNode
            this.fromStart = getClosestNodes((NormalRoad) startRoad);
            if (this.fromStart[0] != null) {
                if (this.fromStart[1] == null) {
                    this.startNode = (RoadNode) this.fromStart[0].getEnd();
                    this.route.add(this.fromStart[0]);
                } else
                    ambigueousStart = true;
            }
        }
    }

    private void findAndSetEndNode(Road endNode) {
        if (endNode instanceof RoadNode) // RoadNode
            this.endNode = (RoadNode) endNode;
        else { // Not a RoadNode
            this.fromEnd = getClosestNodes((NormalRoad) endNode);
            if (this.fromEnd[0] != null) {
                if (this.fromEnd[1] == null) {
                    this.endNode = (RoadNode) this.fromEnd[0].getEnd();
                } else
                    ambigueousEnd = true;
            }
        }

        if (this.endNode == null)
            return;

        this.roadGraph.getRoutes().forEach(routeRoad -> {
            Road roadStart = routeRoad.getStart();
            Road roadEnd = routeRoad.getStart();

            int hScoreStart = manhattanDistance(roadStart.position, endNode.position);
            int hScoreEnd = manhattanDistance(roadEnd.position, endNode.position);
            this.roadGraph.getNodes().forEach(roadNode -> {
                if (roadNode.getPosition().equals(roadStart.getPosition()))
                    roadNode.sethScore(hScoreStart);
            });
            roadStart.sethScore(hScoreStart);
            roadEnd.sethScore(hScoreEnd);

            routeRoad.getRoute().stream().filter(Objects::nonNull)
                    .forEach(road -> road.sethScore(manhattanDistance(road.position, endNode.position)));
        });

        if (this.startNode != null)
            this.startNode.sethScore(manhattanDistance(this.startNode.position, endNode.position));

        if (this.endNode != null)
            this.endNode.sethScore(manhattanDistance(this.endNode.position, endNode.position)); // = 0 ?
    }

    public static @NotNull
    Route<RouteRoad> findRoute(Vector2f from, BuildingItem toItem, int maxRouteLength) {
        if (toItem == null || from == null || maxRouteLength < 0)
            return new Route<>();

        Terrain terrain = Terrain.getInstance();
        final RouteFinder routeFinder = new RouteFinder(terrain.getRoadGraph());

        Set<Route<RouteRoad>> foundRoutes = new TreeSet<>(
                Comparator.comparingInt(o -> o.stream().mapToInt(RouteRoad::getgScore).sum()));

        Set<Vector2f> foundRoads = new HashSet<>();
        terrain.getItems().entrySet().stream()
                .filter(entry -> !(entry.getValue() instanceof RoadItem))
                .filter(entry -> entry.getValue() instanceof ConnectableItem &&
                        entry.getValue().getName().equals(toItem.getName()))
                .filter(entry -> ((ConnectableItem) entry.getValue()).isConnected())
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
                            Vector2f to = pos.add(offset).add(Direction.toRelativeDistance(direction));

                            if (foundRoads.contains(to))
                                return;

                            foundRoads.add(to);
                            Route<RouteRoad> route = routeFinder.findRoute(from, to, maxRouteLength);
                            if (route != null && !route.isEmpty())
                                foundRoutes.add(route);

                            routeFinder.reset();
                        }
                    }
                });

        return foundRoutes.stream().findFirst().orElse(new Route<>());
    }

    public @NotNull
    Route<RouteRoad> findRoute(Vector2f from, Vector2f to, int maxRouteLength) {
        if (maxRouteLength <= 0)
            maxRouteLength = Integer.MAX_VALUE;

        // Makes sure everything is default
        assert this.route.isEmpty();
        assert !this.ambigueousStart;
        assert !this.ambigueousEnd;
        assert this.startNode == null;
        assert this.endNode == null;
        assert this.fromStart[0] == null && this.fromStart[1] == null;
        assert this.fromEnd[0] == null && this.fromEnd[1] == null;

        Road start = Terrain.getInstance().getRoad(from);
        Road end = Terrain.getInstance().getRoad(to);

        if (start == null || end == null) // One of the coordinates given != road
            return new Route<>();

        boolean commonRoute = false;

        findAndSetStartNode(start);
        findAndSetEndNode(end);

        if (isStartAmbigeous() && isEndAmbigeous()) {
            if (fromStart[0].getEnd().equals(fromEnd[0].getEnd()))
                commonRoute = true;
            else if (fromEnd.length > 1 && fromStart[0].getEnd().equals(fromEnd[1].getEnd()))
                commonRoute = true;
            else if (fromStart.length > 1 && fromStart[1].getEnd().equals(fromEnd[0].getEnd()))
                commonRoute = true;
            else if (fromStart.length > 1 && fromEnd.length > 1 && fromStart[1].getEnd().equals(fromEnd[1].getEnd()))
                commonRoute = true;
        }

        if (!commonRoute && isStartAmbigeous()) {
            RouteRoad routeRoadFromStart1 = fromStart[0];
            RouteRoad routeRoadFromStart2 = fromStart[1];

            Route<RouteRoad> routeFromStartNode1, routeFromStartNode2;

            int cost1 = 0;
            int cost2 = 0;
            if (routeRoadFromStart1.getEnd().equals(start) && routeRoadFromStart2.getEnd().equals(start)) {
                cost1 = routeRoadFromStart1.getgScore();
                cost2 = routeRoadFromStart2.getgScore();

                routeFromStartNode1 = new Route<>();
                routeFromStartNode2 = new Route<>();
                routeFromStartNode1 = routeFromStartNode1.addAtStart(routeRoadFromStart1);
                routeFromStartNode2 = routeFromStartNode2.addAtStart(routeRoadFromStart2);
            } else {
                RouteFinder routeFinder = new RouteFinder(roadGraph);

                int newMaxLength1 = maxRouteLength - routeRoadFromStart1.getgScore();
                int newMaxLength2 = maxRouteLength - routeRoadFromStart2.getgScore();

                assert newMaxLength1 >= 0;
                assert newMaxLength2 >= 0;

                routeFromStartNode1 = routeFinder
                        .findRoute(routeRoadFromStart1.getEnd().getPosition(), to, newMaxLength1);
                routeFinder.reset();

                routeFromStartNode2 = routeFinder
                        .findRoute(routeRoadFromStart2.getEnd().getPosition(), to, newMaxLength2);

                // - for each cause of the node in the middle counted twice
                List<Road> allRoads1 = routeFromStartNode1.getAllRoads();
                if (!allRoads1.isEmpty()) {
                    cost1 = routeFromStartNode1.getCost() + routeRoadFromStart1.getgScore();
                    cost1 -= allRoads1.get(allRoads1.size() - 1).getScore();

                    routeFromStartNode1 = routeFromStartNode1.addAtStart(routeRoadFromStart1);
                }

                List<Road> allRoads2 = routeFromStartNode2.getAllRoads();
                if (!allRoads2.isEmpty()) {
                    cost2 = routeFromStartNode2.getCost() + routeRoadFromStart2.getgScore();
                    cost2 -= allRoads2.get(allRoads2.size() - 1).getScore();

                    routeFromStartNode2 = routeFromStartNode2.addAtStart(routeRoadFromStart2);
                }

                if (allRoads1.isEmpty() && allRoads2.isEmpty())
                    return new Route<>();

                if (allRoads1.isEmpty()) {
                    if (cost2 <= maxRouteLength)
                        return routeFromStartNode2;
                    return new Route<>();
                }

                if (allRoads2.isEmpty()) {
                    if (cost1 <= maxRouteLength)
                        return routeFromStartNode1;
                    return new Route<>();
                }
            }

            if (cost1 > maxRouteLength && cost2 > maxRouteLength)
                return new Route<>(); // empty route
            if (cost1 > maxRouteLength)
                return routeFromStartNode2;
            if (cost2 > maxRouteLength)
                return routeFromStartNode1;

            return cost1 < cost2 ? routeFromStartNode1 : routeFromStartNode2; // min global cost
        }

        if (!commonRoute && isEndAmbigeous()) {
            RouteRoad routeRoadFromEnd1 = fromEnd[0];
            RouteRoad routeRoadFromEnd2 = fromEnd[1];

            int cost1 = 0;
            int cost2 = 0;
            Route<RouteRoad> routeFromEndNode1, routeFromEndNode2;
            if (routeRoadFromEnd1.getEnd().equals(start) && routeRoadFromEnd2.getEnd().equals(start)) {
                cost1 = routeRoadFromEnd1.getgScore();
                cost2 = routeRoadFromEnd2.getgScore();

                routeFromEndNode1 = new Route<>();
                routeFromEndNode2 = new Route<>();
                routeFromEndNode1.add(routeRoadFromEnd1.invertRoute());
                routeFromEndNode2.add(routeRoadFromEnd2.invertRoute());
            } else {
                RouteFinder routeFinder = new RouteFinder(roadGraph);

                int newMaxLength1 = maxRouteLength - routeRoadFromEnd1.getgScore();
                int newMaxLength2 = maxRouteLength - routeRoadFromEnd2.getgScore();

                assert newMaxLength1 >= 0;
                assert newMaxLength2 >= 0;

                routeFromEndNode1 = routeFinder
                        .findRoute(from, routeRoadFromEnd1.getEnd().getPosition(), newMaxLength1);
                routeFinder.reset();

                routeFromEndNode2 = routeFinder
                        .findRoute(from, routeRoadFromEnd2.getEnd().getPosition(), newMaxLength2);

                // - for each cause of the node in the middle counted twice
                List<Road> allRoads1 = routeFromEndNode1.getAllRoads();
                if (!allRoads1.isEmpty()) {
                    cost1 = routeFromEndNode1.getCost() + routeRoadFromEnd1.getgScore();
                    cost1 -= allRoads1.get(allRoads1.size() - 1).getScore();

                    routeFromEndNode1.add(routeRoadFromEnd1.invertRoute());
                }

                List<Road> allRoads2 = routeFromEndNode2.getAllRoads();
                if (!allRoads2.isEmpty()) {
                    cost2 = routeFromEndNode2.getCost() + routeRoadFromEnd2.getgScore();
                    cost2 -= allRoads2.get(allRoads2.size() - 1).getScore();

                    routeFromEndNode2.add(routeRoadFromEnd2.invertRoute());
                }

                if (allRoads1.isEmpty() && allRoads2.isEmpty())
                    return new Route<>();

                if (allRoads1.isEmpty()) {
                    if (cost2 <= maxRouteLength)
                        return routeFromEndNode2;
                    return new Route<>();
                }

                if (allRoads2.isEmpty()) {
                    if (cost1 <= maxRouteLength)
                        return routeFromEndNode1;
                    return new Route<>();
                }
            }

            if (cost1 > maxRouteLength && cost2 > maxRouteLength)
                return new Route<>(); // empty route
            if (cost1 > maxRouteLength)
                return routeFromEndNode2;
            if (cost2 > maxRouteLength)
                return routeFromEndNode1;

            return cost1 < cost2 ? routeFromEndNode1 : routeFromEndNode2; // min global cost
        }
//
//        if (this.fromStart[0] != null || this.fromEnd[0] != null)
//            commonRoute = roadGraph.getRoutes().stream()
//                    .anyMatch(routeRoad -> routeRoad.getRoute().contains(start) && routeRoad.getRoute().contains(end));
        if ((this.startNode == null && this.endNode == null) || commonRoute) {
            // Both start and end belong to the same route, no need to check for nodes
            // That or there are no nodes connected to either

            // Need to find the end from start without nodes
            RouteRoad routeWithoutNodes = findRouteWithoutNodes((NormalRoad) start, (NormalRoad) end);
            if (routeWithoutNodes != null)
                route.add(routeWithoutNodes);

            if (maxRouteLength < route.getCost())
                route.clear();

            return route;
        }

        if (this.startNode == null || this.endNode == null) { // No node near start or end
            NormalRoad normalRoad;
            if (this.startNode == null)
                normalRoad = (NormalRoad) start;
            else
                normalRoad = (NormalRoad) end;

            RouteRoad[] closestNodes = getClosestNodes(normalRoad);
            if (closestNodes[0] == null && closestNodes[1] == null)
                return route; // Empty route
            if (closestNodes[0] == null)
                route.add(closestNodes[1]);
            else if (closestNodes[1] == null)
                route.add(closestNodes[0]);
            else
                route.add(closestNodes[0].getgScore() < closestNodes[1].getgScore() ?
                        closestNodes[0] : closestNodes[1]);

            if (maxRouteLength < route.getCost())
                route.clear();

            return route;
        }

        // Not ambigueous, startNode != null && endNode != null
        assert startNode != null;
        assert endNode != null;

        if (this.startNode.equals(this.endNode)) {
            if (route.isEmpty()) {
                if (fromStart[0] != null)
                    route.add(fromStart[0]);
                if (fromEnd[0] != null)
                    route.add(fromEnd[0].invertRoute());
            } else if (route.size() == 1 && fromStart[0] != null && fromEnd[0] != null) {
                if (fromStart[0].getEnd().equals(fromEnd[0].getEnd())) { // Specifically for case 12
//                    RouteRoad routeRoad = route.contains(fromStart[0]) ? fromEnd[0].invertRoute()
//                            : (route.contains(fromEnd[0]) ? fromStart[0] : null);
//                    if (routeRoad != null)
//                        route.add(routeRoad);
                    route.add(fromEnd[0].invertRoute()); // enough for now //todo check with rest of tests
                }
            }

            if (maxRouteLength < route.getCost())
                route.clear();

            return route;
        }

        LinkedList<SortedSet<RouteRoad>> listPossibleRoutes = new LinkedList<>();
        Set<RoadNode> usedNodes = new HashSet<>();

        RoadNode node = startNode;
        while (usedNodes.size() < roadGraph.getNodes().size()) {
            usedNodes.add(node);

            listPossibleRoutes.add(new TreeSet<>(roadGraph.getRoutes(node)));

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
                return new Route<>();

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

                if (fromEnd[0] != null)
                    route.add(fromEnd[0].invertRoute());

                if (maxRouteLength < route.stream().mapToInt(RouteRoad::getgScore).sum())
                    route.clear();
                return route;
            }
            listPossibleRoutes.clear();
        }
        return new Route<>();
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
                    roads.add(start);

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

            if (possibleRoutes[0] == null)
                return null;

            if (nbDir == 1)
                return possibleRoutes[0];

            if (possibleRoutes[1] == null)
                return possibleRoutes[0];

            return (possibleRoutes[0].getgScore() + possibleRoutes[0].getEnd().gethScore()) <
                    (possibleRoutes[1].getgScore() + possibleRoutes[1].getEnd().gethScore()) ? possibleRoutes[0]
                    : possibleRoutes[1];
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Route<RouteRoad> getRoute() {
        return this.route;
    }

    private int manhattanDistance(Vector2f startPos, Vector2f endPos) {
        if (startPos == null || endPos == null)
            return Integer.MAX_VALUE;

        return Math.abs((int) endPos.x - (int) startPos.x) + Math.abs((int) endPos.y - (int) startPos.y);
    }

    /**
     * Get both nodes from each side of the road
     *
     * @param road the road
     * @return the nodes (can be 0 to 2)
     */
    public @NotNull
    RouteRoad[] getClosestNodes(NormalRoad road) {
        final Terrain terrain = Terrain.getInstance();
        //Direction[] connectionsToRoadItem = terrain.getConnectionsToRoadItem(road.getPosition(), false); WHY?
        final Direction[] directions = terrain.getConnectionsToRoadItem(road.getPosition(), true);

        final int nbDir = directions.length;
        if (nbDir == 0 /*|| connectionsToRoadItem.length == 0 || connectionsToRoadItem.length > 2*/ || nbDir > 2)
            return new RouteRoad[2];

        // nbDir = 1 or nbDir = 2
        final CountDownLatch latch = new CountDownLatch(nbDir);
        final Runnable[] runnables = new Runnable[nbDir];
        final RouteRoad[] possibleRoutes = new RouteRoad[2];

        try {
            for (int i = 0; i < nbDir; i++) {
                int finalI = i;
                runnables[i] = () -> {
                    Set<Road> roads = new LinkedHashSet<>();
                    roads.add(road);

                    Direction dir = directions[finalI];
                    Vector2f nextPos = road.getPosition().add(Direction.toRelativeDistance(dir));
                    Direction[] nextDirs = terrain.getConnectionsToRoadItem(nextPos, true);
                    int nbDirections = nextDirs.length;

                    while (nbDirections == 2 &&
                            !nextPos.equals(road.getPosition())) { // avoid looping forever (case 21)
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
                    if (nbDirections > 2) { // Node found
                        RoadNode node = new RoadNode(nextPos);
                        roads.add(node); // Adding last road
                        possibleRoutes[finalI] = new RouteRoad(road, node, roads); // Node found, route added
                    }
                    latch.countDown();
                };
                runnables[i].run();
            }
            latch.await();

//            if (nbDir == 1)
//                return possibleRoutes[0];
//
//            if (possibleRoutes[0] == null || possibleRoutes[1] == null)
//                return null;
//
//            return (possibleRoutes[0].getgScore() + possibleRoutes[0].getEnd().gethScore()) <
//                    (possibleRoutes[1].getgScore() + possibleRoutes[1].getEnd().gethScore()) ? possibleRoutes[0]
//                    : possibleRoutes[1];
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return possibleRoutes;
    }

    public void reset() {
        this.ambigueousEnd = false;
        this.ambigueousStart = false;
        this.fromStart = new RouteRoad[2];
        this.fromEnd = new RouteRoad[2];
        this.route = new Route<>();
        this.endNode = null;
        this.startNode = null;
    }

    public @NotNull
    Route<RouteRoad> findUnobstructedRouteV1(final Vector2f startPos, final Vector2f endPos) {
//        int manhattanDistance = manhattanDistance(startPos, endPos);
//        final int MAX = 15 + manhattanDistance;
//
//        Terrain terrain = Terrain.getInstance();
//        Set<Vector2f> emptyPositions = terrain.getEmptyPositions();
//        Map<Vector2f, Integer> positionValues = new HashMap<>();
//        positionValues.put(startPos, manhattanDistance);
//        positionValues.put(endPos, 0);
//
//        for (Vector2f emptyPos : emptyPositions) {
//            int distance = manhattanDistance(emptyPos, endPos);
//            if (distance <= MAX)
//                positionValues.put(emptyPos, distance);
//        }
//
//        return findUnobstructedRoute(startPos, endPos, null, positionValues, startPos, MAX);
        Route<RouteRoad> routeRoads = new Route<>();
        if (startPos == null || endPos == null || startPos.equals(endPos))
            return routeRoads;

        Map<Vector2f, Item> items = Terrain.getInstance().getItems();
        Set<Vector2f> previewItemPositions = Terrain.getInstance().getPreviewItemPositions();
        if ((items.containsKey(startPos) && !previewItemPositions.contains(startPos)) ||
                (items.containsKey(endPos) && !previewItemPositions.contains(endPos)))
            return routeRoads;

        Road start = new NormalRoad(startPos);
        RouteRoad routeRoad = new RouteRoad(start);
        Vector2f currPos = new Vector2f(startPos);
        while (currPos.x != endPos.x) {
            if (currPos.x < endPos.x) {
                currPos = currPos.add(new Vector2f(1, 0));
            } else
                currPos = currPos.add(new Vector2f(-1, 0));

            if (items.containsKey(currPos) && !previewItemPositions.contains(currPos))
                return new Route<>();
            routeRoad.addRoad(new NormalRoad(currPos));
        }

        if (currPos.x != startPos.x) { // x changed
            NormalRoad crossing = new NormalRoad(currPos);
            routeRoad.setEnd(crossing);
            routeRoads.add(routeRoad);

            if (currPos.equals(endPos))
                return routeRoads;

            routeRoad = new RouteRoad(crossing);
        }

        while (currPos.y != endPos.y) {
            if (currPos.y < endPos.y) {
                currPos = currPos.add(new Vector2f(0, 1));
            } else
                currPos = currPos.add(new Vector2f(0, -1));

            if (items.containsKey(currPos) && !previewItemPositions.contains(currPos))
                return new Route<>();

            routeRoad.addRoad(new NormalRoad(currPos));
        }

        assert currPos.equals(endPos);
        routeRoad.setEnd(new NormalRoad(endPos));
        routeRoads.add(routeRoad);

        return routeRoads;
    }

    public @NotNull
    Route<RouteRoad> findUnobstructedRouteV2(final Vector2f startPos, final Vector2f endPos) {
        Route<RouteRoad> routeRoads = new Route<>();
        if (startPos == null || endPos == null || startPos.equals(endPos))
            return routeRoads;

        Map<Vector2f, Item> items = Terrain.getInstance().getItems();
        Set<Vector2f> previewItemPositions = Terrain.getInstance().getPreviewItemPositions();
        if ((items.containsKey(startPos) && !previewItemPositions.contains(startPos)) ||
                (items.containsKey(endPos) && !previewItemPositions.contains(endPos)))
            return routeRoads;

        Road start = new NormalRoad(startPos);
        RouteRoad routeRoad = new RouteRoad(start);
        Vector2f currPos = new Vector2f(startPos);

        while (currPos.y != endPos.y) {
            if (currPos.y < endPos.y) {
                currPos = currPos.add(new Vector2f(0, 1));
            } else
                currPos = currPos.add(new Vector2f(0, -1));

            if (items.containsKey(currPos) && !previewItemPositions.contains(currPos))
                return new Route<>();

            routeRoad.addRoad(new NormalRoad(currPos));
        }

        if (currPos.y != startPos.y) { // y changed
            NormalRoad crossing = new NormalRoad(currPos);
            routeRoad.setEnd(crossing);
            routeRoads.add(routeRoad);

            if (currPos.equals(endPos))
                return routeRoads;

            routeRoad = new RouteRoad(crossing);
        }

        while (currPos.x != endPos.x) {
            if (currPos.x < endPos.x) {
                currPos = currPos.add(new Vector2f(1, 0));
            } else
                currPos = currPos.add(new Vector2f(-1, 0));

            if (items.containsKey(currPos) && !previewItemPositions.contains(currPos))
                return new Route<>();

            routeRoad.addRoad(new NormalRoad(currPos));
        }

        assert currPos.equals(endPos);
        routeRoad.setEnd(new NormalRoad(endPos));
        routeRoads.add(routeRoad);

        return routeRoads;
    }

    @Deprecated
    private @NotNull
    Route<RouteRoad> findUnobstructedRoute(final Vector2f startPos, final Vector2f endPos,
            Route<RouteRoad> currentRoute, Map<Vector2f, Integer> positionValues, final Vector2f finalStartPos,
            final int MAX) {
        Terrain terrain = Terrain.getInstance();

        if (!startPos.equals(endPos)) {
            Map<Vector2f, Integer> nextPositions = new TreeMap<>(Comparator.comparing(positionValues::get));
            for (Direction direction : Direction.values()) {
                Vector2f nextPos = startPos.add(Direction.toRelativeDistance(direction));
                if (positionValues.containsKey(nextPos)) {
                    // No road on this position
                    nextPositions.put(nextPos, positionValues.get(nextPos));
                }
            }
            // nextPositions = sorted map containing empty tiles around currPos, value=hScore
            // Sorted by closest to the end

            if (nextPositions.isEmpty())
                return new Route<>();

            for (Entry<Vector2f, Integer> entry : nextPositions.entrySet()) {
                Vector2f nextPos = entry.getKey();

                if (nextPos == null || entry.getValue() == MAX) // End, too far from endPos
                    return new Route<>();
                else {
                    if (currentRoute == null)
                        currentRoute = new Route<>();
                    Road road = terrain.getRoad(startPos);
                    positionValues.remove(startPos);
                    RouteRoad routeRoad = new RouteRoad(road == null ? new NormalRoad(startPos) : road);
                    road = terrain.getRoad(nextPos);
                    Road nextRoad = road == null ? new NormalRoad(nextPos) : road;
                    routeRoad.setEnd(nextRoad);
                    routeRoad.addRoad(nextRoad);

                    currentRoute.add(routeRoad);
                    if (finalStartPos.equals(currentRoute.getStart().position) &&
                            nextPos.equals(endPos)) // End, endPos found
                        return currentRoute;

                    return findUnobstructedRoute(nextPos, endPos,
                            currentRoute, positionValues, finalStartPos, MAX);
                }
            }
        }

        return new Route<>();
    }

    public static class Route<R> extends LinkedHashSet<RouteRoad> implements Comparable<R> {

        public Route() {
        }

        public Route(Collection<? extends RouteRoad> c) {
            super(c);
        }

        @Override
        public boolean add(RouteRoad routeRoad) {
            if (routeRoad.getEnd() != null)
                routeRoad.getRoute().add(routeRoad.getEnd());
            return super.add(routeRoad);
        }

        public boolean compareCost(Route<R> route) {
            return route.getCost() == getCost();
        }

        public boolean compareRoutes(Route<R> route) {
            List<Road> allRoads = getAllRoads();
            List<Road> allRoadsRoute = route.getAllRoads();

            if (allRoads.size() != allRoadsRoute.size())
                return false;

            for (int i = 0; i < allRoads.size(); i++)
                if (allRoads.get(i).getClass() != allRoadsRoute.get(i).getClass())
                    return false;

            List<Vector2f> collect = allRoads.stream().map(Road::getPosition).collect(Collectors.toList());
            List<Vector2f> collect1 = allRoadsRoute.stream().map(Road::getPosition).collect(Collectors.toList());

            return collect.equals(collect1);
        }

        public int getCost() {
            return this.stream().mapToInt(routeRoad ->
                    routeRoad.getRoute().stream().mapToInt(Road::getScore).sum()).sum();
        }

        public List<Road> getAllRoads() {
            List<Road> roads = new LinkedList<>();
            this.stream().map(RouteRoad::getRoute).forEach(roads::addAll);

            return roads;
        }

        public Route<R> addAtStart(RouteRoad route) {
            List<RouteRoad> tmp = new ArrayList<>(this);
            Collections.reverse(tmp);
            tmp.add(route);
            Collections.reverse(tmp);

            return new Route<>(new LinkedHashSet<>(tmp));
        }

        public Route<R> invertRoute() {
            List<RouteRoad> tmp = new ArrayList<>();
            for (RouteRoad routeRoad : this) {
                tmp.add(routeRoad.invertRoute());
            }
            Collections.reverse(tmp);

            return new Route<>(new LinkedHashSet<>(tmp));
        }

        public Road getStart() {
            return this.stream().findFirst().map(RouteRoad::getStart).orElse(null);
        }

        public Road getEnd() {
            return this.stream().skip(size() - 1).map(RouteRoad::getEnd).findFirst().orElse(null);
        }

        @Override
        public int compareTo(Object o) {
            return Integer.compare(getCost(), ((Route<?>) o).getCost());
        }
    }
}