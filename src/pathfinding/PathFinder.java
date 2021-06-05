package pathfinding;

import static util.math.Maths.manhattanDistance;

import entities.Camera.Direction;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import scene.Scene;
import scene.components.PositionComponent;
import scene.gameObjects.GameObject;
import terrains.TerrainPosition;
import util.math.Maths;
import util.math.Vector2f;

public class PathFinder {

    private final static Scene     scene = Scene.getInstance();
    private final        RoadGraph roadGraph;

    private NodeRoad startNode;
    private NodeRoad endNode;

    private Road startRoad;
    private Road endRoad;

    // Optional if extremities != start or end
    // Represent the paths from the normalRoad start to the closest 2 nodes
    private NodeConnection[] fromStart = new NodeConnection[2];
    private NodeConnection[] fromEnd   = new NodeConnection[2];

    private boolean ambigueousStart, ambigueousEnd;

    private Path path = new Path();

    public PathFinder(RoadGraph roadGraph) {
        this.roadGraph = roadGraph.copy();
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
        this.startRoad = startRoad;

        if (startRoad instanceof NodeRoad) { // NodeRoad
            this.startNode = (NodeRoad) startRoad;
        } else { // Not a NodeRoad
            this.fromStart = getClosestNodes((NormalRoad) startRoad);
            if (this.fromStart[0] != null) {
                if (this.fromStart[1] == null) {
                    this.startNode = (NodeRoad) this.fromStart[0].getEnd();
                } else
                    this.ambigueousStart = true;
            }
        }
    }

    private void findAndSetEndNode(Road endRoad) {
        this.endRoad = endRoad;

        if (endRoad instanceof NodeRoad) // NodeRoad
            this.endNode = (NodeRoad) endRoad;
        else { // Not a NodeRoad
            this.fromEnd = getClosestNodes((NormalRoad) endRoad);
            if (this.fromEnd[0] != null) {
                if (this.fromEnd[1] == null) {
                    this.endNode = (NodeRoad) this.fromEnd[0].getEnd();
                } else
                    ambigueousEnd = true;
            }
        }

        this.roadGraph.getNodeConnections().forEach(nodeConnection -> {
            Road roadStart = nodeConnection.getStart();
            Road roadEnd = nodeConnection.getEnd();

            int hScoreStart = manhattanDistance(roadStart.position, endRoad.position);
            int hScoreEnd = manhattanDistance(roadEnd.position, endRoad.position);
            this.roadGraph.getNodes().forEach(nodeRoad -> {
                if (nodeRoad.getPosition().equals(roadStart.getPosition()))
                    nodeRoad.sethScore(hScoreStart);
            });
            roadStart.sethScore(hScoreStart);
            roadEnd.sethScore(hScoreEnd);

            nodeConnection.getRoads().stream().filter(Objects::nonNull)
                    .forEach(road -> road.sethScore(manhattanDistance(road.position, endRoad.position)));
        });

        this.startRoad.sethScore(manhattanDistance(this.startRoad.position, endRoad.position));

        if (this.startNode != null) {
            this.startNode.sethScore(manhattanDistance(this.startNode.position, endRoad.position));
            int dirs = scene.getRoadConnections(this.startRoad.position).length;
            if (dirs > 1 && this.startNode.gethScore() > this.startRoad.gethScore() &&
                    this.startNode.equals(this.endNode)) {
                this.startNode = null;
                this.fromStart = new NodeConnection[2];
                this.path.clear();
            }
        } else if (this.ambigueousStart) {
            if (this.fromStart[0].getEnd().equals(endRoad)) {
                this.fromStart[1] = null;
                this.ambigueousStart = false;
            } else if (this.fromStart[1].getEnd().equals(endRoad)) {
                this.fromStart[0] = this.fromStart[1];
                this.fromStart[1] = null;
                this.ambigueousStart = false;
            }
        }

        if (this.endNode != null) {
            this.endNode.sethScore(manhattanDistance(this.endNode.position, endRoad.position));
            if (this.endNode.gethScore() > this.startRoad.gethScore() && this.fromEnd[0] != null &&
                    this.fromEnd[0].getRoads().contains(this.startRoad)) {
                this.endNode = null;
                this.fromEnd = new NodeConnection[2];
            }
        }
    }

    /**
     * Finds a path between a building (A) and any building of given type (B)
     *
     * @param startGameObject (A)
     * @param gameObjectClass (B) given type
     * @param maxPathLength if path exceeds given length, it is discarded
     * @param bestPath if true, the path with the lowest cost is returned
     * @return path or empty path if none found
     */
    public static Path findPath(GameObject startGameObject, Class<? extends GameObject> gameObjectClass,
            int maxPathLength, boolean bestPath) {
        Set<GameObject> possibleGameObjects = new TreeSet<>((o1, o2) -> manhattanDistance(
                o1.getComponent(PositionComponent.class).getPosition().toTerrainPosition(),
                o2.getComponent(PositionComponent.class).getPosition().toTerrainPosition()));
        possibleGameObjects.addAll(scene.getGameObjectsOfType(gameObjectClass));

        Path bestPathFound = new Path();
        for (GameObject possibleGameObject : possibleGameObjects) {
            Path path = findBestPath(startGameObject, possibleGameObject, maxPathLength);
            if (!path.isEmpty() && !bestPath)
                return path;

            if (!path.isEmpty() && path.getCost() < bestPathFound.getCost()) {
                bestPathFound = path;
            }
        }
        return bestPathFound;
    }

    public static Path findBestPath(GameObject fromGameObject, GameObject toGameObject, int maxPathLength) {
        if (maxPathLength <= 0)
            maxPathLength = Integer.MAX_VALUE;

        if (toGameObject == null || fromGameObject == null)
            return new Path();

        final PathFinder pathFinder = new PathFinder(scene.getRoadGraph());

        Set<Path> foundPaths = new TreeSet<>(
                Comparator.comparingInt(o -> o.stream().mapToInt(NodeConnection::getgScore).sum()));

        List<TerrainPosition> fromRoads = scene.getRoadsConnectedToGameObject(fromGameObject)
                .stream()
                .map(gameObject -> gameObject.getComponent(PositionComponent.class).getPosition().toTerrainPosition())
                .collect(Collectors.toList());
        List<TerrainPosition> toRoads = scene.getRoadsConnectedToGameObject(toGameObject)
                .stream()
                .map(gameObject -> gameObject.getComponent(PositionComponent.class).getPosition().toTerrainPosition())
                .collect(Collectors.toList());

        for (TerrainPosition fromRoad : fromRoads) {
            for (TerrainPosition toRoad : toRoads) {
                Path path = pathFinder.findBestPath(fromRoad, toRoad, maxPathLength);
                if (!path.isEmpty())
                    foundPaths.add(path);

                pathFinder.reset();
            }
        }

        return foundPaths.stream().findFirst().orElse(new Path());
    }

    public Road getRoad(TerrainPosition roadPosition) {
        Direction[] roadDirections = scene.getRoadConnections(roadPosition);

        return roadDirections.length > 2 ? new NodeRoad(roadPosition) : new NormalRoad(roadPosition);
    }

    /**
     * @param maxPathLength 0 = infinite
     */
    public Path findBestPath(TerrainPosition from, TerrainPosition to, int maxPathLength) {
        if (maxPathLength <= 0)
            maxPathLength = Integer.MAX_VALUE;


        // Makes sure everything is default
        assert this.path.isEmpty();
        assert !this.ambigueousStart;
        assert !this.ambigueousEnd;
        assert this.startNode == null;
        assert this.endNode == null;
        assert this.fromStart[0] == null && this.fromStart[1] == null;
        assert this.fromEnd[0] == null && this.fromEnd[1] == null;

        Road start = getRoad(from);
        Road end = getRoad(to);

        if (from.equals(to)) {
            Path path = new Path();
            NodeConnection nodeConnection = new NodeConnection(start, end);

            path.add(nodeConnection);
            return path;
        }

        if (start == null || end == null) // One of the coordinates given != road
            return new Path();

        boolean commonPath = false;

        findAndSetStartNode(start);
        findAndSetEndNode(end);

        if (!this.ambigueousStart && this.fromStart[0] != null) {
            if (!this.fromStart[0].getRoads().contains(this.endRoad))
                this.path.add(this.fromStart[0]);
            else {
                this.path.clear();
                this.fromEnd = new NodeConnection[2];
                this.endNode = null;
                NodeConnection nodeConnection = new NodeConnection(this.startRoad, this.endRoad);
                for (Road road : this.fromStart[0].getRoads()) {
                    nodeConnection.addRoad(road);
                    if (road.equals(this.endRoad))
                        break;
                }
                this.fromStart[0] = nodeConnection;
                if (this.ambigueousStart && this.fromStart[1].getRoads().contains(this.endRoad)) {
                    NodeConnection nodeConnection2 = new NodeConnection(this.startRoad, this.endRoad);
                    for (Road road : this.fromStart[1].getRoads()) {
                        nodeConnection2.addRoad(road);
                        if (road.equals(this.endRoad))
                            break;
                    }
                    this.fromStart[1] = nodeConnection;
                    this.path.add(this.fromStart[0].getgScore() < this.fromStart[1].getgScore() ? this.fromStart[0]
                            : this.fromStart[1]);
                } else {
                    this.path.add(this.fromStart[0]);
                }
                return this.path;
            }
        }
        if (isStartAmbigeous() && isEndAmbigeous()) {
            if (fromStart[0].getEnd().equals(fromEnd[0].getEnd()))
                commonPath = true;
            else if (fromEnd.length > 1 && fromStart[0].getEnd().equals(fromEnd[1].getEnd()))
                commonPath = true;
            else if (fromStart.length > 1 && fromStart[1].getEnd().equals(fromEnd[0].getEnd()))
                commonPath = true;
            else if (fromStart.length > 1 && fromEnd.length > 1 && fromStart[1].getEnd().equals(fromEnd[1].getEnd()))
                commonPath = true;
        }

        if (!commonPath && isStartAmbigeous()) {
            NodeConnection nodeConnectionFromStart1 = this.fromStart[0];
            NodeConnection nodeConnectionFromStart2 = this.fromStart[1];

            Path pathFromStartNode1, pathFromStartNode2;

            int cost1 = 0;
            int cost2 = 0;
            if (nodeConnectionFromStart1.getEnd().equals(start) && nodeConnectionFromStart2.getEnd().equals(start)) {
                cost1 = nodeConnectionFromStart1.getgScore();
                cost2 = nodeConnectionFromStart2.getgScore();

                pathFromStartNode1 = new Path();
                pathFromStartNode2 = new Path();
                pathFromStartNode1 = pathFromStartNode1.addAtStart(nodeConnectionFromStart1);
                pathFromStartNode2 = pathFromStartNode2.addAtStart(nodeConnectionFromStart2);
            } else {
                PathFinder pathFinder = new PathFinder(roadGraph);

                int newMaxLength1 = maxPathLength - nodeConnectionFromStart1.getgScore();
                int newMaxLength2 = maxPathLength - nodeConnectionFromStart2.getgScore();

                assert newMaxLength1 >= 0;
                assert newMaxLength2 >= 0;

                if (start.equals(nodeConnectionFromStart1.getEnd())) {
                    pathFromStartNode1 = new Path();
                    pathFromStartNode1.add(nodeConnectionFromStart1);
                } else {
                    pathFromStartNode1 = pathFinder
                            .findBestPath(nodeConnectionFromStart1.getEnd().getPosition(), end.getPosition(),
                                    newMaxLength1);
                    pathFinder.reset();
                }
                if (start.equals(nodeConnectionFromStart2.getEnd())) {
                    pathFromStartNode2 = new Path();
                    pathFromStartNode2.add(nodeConnectionFromStart2);
                } else {
                    pathFromStartNode2 = pathFinder
                            .findBestPath(nodeConnectionFromStart2.getEnd().getPosition(), end.getPosition(),
                                    newMaxLength2);
                }

                // - for each cause of the node in the middle counted twice
                List<Road> allRoads1 = pathFromStartNode1.getAllRoads();
                if (!allRoads1.isEmpty()) {
                    cost1 = pathFromStartNode1.getCost() + nodeConnectionFromStart1.getgScore();
                    cost1 -= allRoads1.get(allRoads1.size() - 1).getScore();

                    pathFromStartNode1 = pathFromStartNode1.addAtStart(nodeConnectionFromStart1);
                }

                List<Road> allRoads2 = pathFromStartNode2.getAllRoads();
                if (!allRoads2.isEmpty()) {
                    cost2 = pathFromStartNode2.getCost() + nodeConnectionFromStart2.getgScore();
                    cost2 -= allRoads2.get(allRoads2.size() - 1).getScore();

                    pathFromStartNode2 = pathFromStartNode2.addAtStart(nodeConnectionFromStart2);
                }

                if (allRoads1.isEmpty() && allRoads2.isEmpty())
                    return new Path();

                if (allRoads1.isEmpty()) {
                    if (cost2 <= maxPathLength)
                        return pathFromStartNode2;
                    return new Path();
                }

                if (allRoads2.isEmpty()) {
                    if (cost1 <= maxPathLength)
                        return pathFromStartNode1;
                    return new Path();
                }
            }

            if (cost1 > maxPathLength && cost2 > maxPathLength)
                return new Path(); // empty route
            if (cost1 > maxPathLength)
                return pathFromStartNode2;
            if (cost2 > maxPathLength)
                return pathFromStartNode1;

            return cost1 < cost2 ? pathFromStartNode1 : pathFromStartNode2; // min global cost
        }

        if (!commonPath && isEndAmbigeous()) {
            NodeConnection nodeConnectionFromEnd1 = this.fromEnd[0];
            NodeConnection nodeConnectionFromEnd2 = this.fromEnd[1];

            int cost1 = 0;
            int cost2 = 0;
            Path pathFromEndNode1, pathFromEndNode2;
            if (nodeConnectionFromEnd1.getEnd().equals(start) && nodeConnectionFromEnd2.getEnd().equals(start)) {
                cost1 = nodeConnectionFromEnd1.getgScore();
                cost2 = nodeConnectionFromEnd2.getgScore();

                pathFromEndNode1 = new Path();
                pathFromEndNode2 = new Path();
                pathFromEndNode1.add(nodeConnectionFromEnd1.invert());
                pathFromEndNode2.add(nodeConnectionFromEnd2.invert());
            } else {
                PathFinder pathFinder = new PathFinder(roadGraph);

                int newMaxLength1 = maxPathLength - nodeConnectionFromEnd1.getgScore();
                int newMaxLength2 = maxPathLength - nodeConnectionFromEnd2.getgScore();

                assert newMaxLength1 >= 0;
                assert newMaxLength2 >= 0;

                if (start.equals(nodeConnectionFromEnd1.getEnd())) {
                    pathFromEndNode1 = new Path();
                    pathFromEndNode1.add(nodeConnectionFromEnd1.invert());
                } else {
                    pathFromEndNode1 = pathFinder
                            .findBestPath(start.getPosition(), nodeConnectionFromEnd1.getEnd().getPosition(),
                                    newMaxLength1);
                    pathFinder.reset();
                }
                if (start.equals(nodeConnectionFromEnd2.getEnd())) {
                    pathFromEndNode2 = new Path();
                    pathFromEndNode2.add(nodeConnectionFromEnd2.invert());
                } else {
                    pathFromEndNode2 = pathFinder
                            .findBestPath(start.getPosition(), nodeConnectionFromEnd2.getEnd().getPosition(),
                                    newMaxLength2);
                }
                // - for each cause of the node in the middle counted twice
                List<Road> allRoads1 = pathFromEndNode1.getAllRoads();
                if (!allRoads1.isEmpty()) {
                    cost1 = pathFromEndNode1.getCost() + nodeConnectionFromEnd1.getgScore();
                    cost1 -= allRoads1.get(allRoads1.size() - 1).getScore();

                    pathFromEndNode1.add(nodeConnectionFromEnd1.invert());
                }

                List<Road> allRoads2 = pathFromEndNode2.getAllRoads();
                if (!allRoads2.isEmpty()) {
                    cost2 = pathFromEndNode2.getCost() + nodeConnectionFromEnd2.getgScore();
                    cost2 -= allRoads2.get(allRoads2.size() - 1).getScore();

                    pathFromEndNode2.add(nodeConnectionFromEnd2.invert());
                }

                if (allRoads1.isEmpty() && allRoads2.isEmpty())
                    return new Path();

                if (allRoads1.isEmpty()) {
                    if (cost2 <= maxPathLength)
                        return pathFromEndNode2;
                    return new Path();
                }

                if (allRoads2.isEmpty()) {
                    if (cost1 <= maxPathLength)
                        return pathFromEndNode1;
                    return new Path();
                }
            }

            if (cost1 > maxPathLength && cost2 > maxPathLength)
                return new Path(); // empty route
            if (cost1 > maxPathLength)
                return pathFromEndNode2;
            if (cost2 > maxPathLength)
                return pathFromEndNode1;

            return cost1 < cost2 ? pathFromEndNode1 : pathFromEndNode2; // min global cost
        }
//
//        if (this.fromStart[0] != null || this.fromEnd[0] != null)
//            commonPath = roadGraph.getRoutes().stream()
//                    .anyMatch(routeRoad -> routeRoad.getRoute().contains(start) && routeRoad.getRoute().contains(end));
        if ((this.startNode == null && this.endNode == null) || commonPath) {
            // Both start and end belong to the same route, no need to check for nodes
            // That or there are no nodes connected to either

            // Need to find the end from start without nodes
            NodeConnection pathWithoutNodes = findPathWithoutNodes((NormalRoad) start, (NormalRoad) end);
            if (pathWithoutNodes != null)
                this.path.add(pathWithoutNodes);

            if (maxPathLength < path.getCost())
                this.path.clear();

            if (!this.path.isLegal())
                this.path.clear();
            return this.path;
        }

        if (this.startNode == null || this.endNode == null) { // No node near start or end
            NormalRoad normalRoad;
            if (this.startNode == null)
                normalRoad = (NormalRoad) start;
            else
                normalRoad = (NormalRoad) end;

            NodeConnection[] closestNodes = getClosestNodes(normalRoad);
            if (normalRoad.position.equals(end.position)) {
                if (closestNodes[0] != null)
                    closestNodes[0] = closestNodes[0].invert();
                if (closestNodes[1] != null)
                    closestNodes[1] = closestNodes[1].invert();
            }
            NodeConnection toAdd;
            if (closestNodes[0] == null && closestNodes[1] == null)
                return new Path(); // Empty route
            if (closestNodes[0] == null || (closestNodes[1] != null && closestNodes[1].getEnd().equals(end)))
                toAdd = closestNodes[1];
            else if (closestNodes[1] == null || (closestNodes[0] != null && closestNodes[0].getEnd().equals(end)))
                toAdd = closestNodes[0];
            else
                toAdd = closestNodes[0].getgScore() < closestNodes[1].getgScore() ?
                        closestNodes[0] : closestNodes[1];

            this.path.add(toAdd);
            if (maxPathLength < this.path.getCost())
                this.path.clear();

            if (!this.path.isLegal())
                this.path.clear();
            return this.path;
        }

        // Not ambigueous, startNode != null && endNode != null
        assert this.startNode != null;
        assert this.endNode != null;

        if (this.startNode.equals(this.endNode)) {
            if (this.path.isEmpty()) {
                if (this.fromStart[0] != null)
                    this.path.add(this.fromStart[0]);
                if (this.fromEnd[0] != null)
                    this.path.add(this.fromEnd[0].invert());
            } else if (path.size() == 1 && fromStart[0] != null && this.fromEnd[0] != null) {
                if (this.fromStart[0].getEnd().equals(this.fromEnd[0].getEnd())) { // Specifically for case 12
                    this.path.add(this.fromEnd[0].invert()); // enough for now //todo check with rest of tests
                }
            }

            if (maxPathLength < this.path.getCost())
                this.path.clear();

            if (!this.path.isLegal())
                this.path.clear();
            return this.path;
        }

        LinkedList<SortedSet<NodeConnection>> listPossibleNodeConnections = new LinkedList<>();
        Set<NodeRoad> usedNodes = new HashSet<>();

        NodeRoad node = this.startNode;
        while (usedNodes.size() < this.roadGraph.getNodes().size()) {
            usedNodes.add(node);

            Set<NodeConnection> nodeConnections = this.roadGraph.getNodeConnections(node);
            listPossibleNodeConnections.add(new TreeSet<>(nodeConnections));

            // Get cheapest road
            SortedSet<NodeConnection> cheapestNodeConnections = new TreeSet<>();
            listPossibleNodeConnections.forEach(lNodeConnections -> { // Second check if end is not found
                lNodeConnections.stream()
                        .filter(nodeConnection -> nodeConnection.getEnd() instanceof NodeRoad)
                        .filter(nodeConnection -> !usedNodes.contains((NodeRoad) nodeConnection.getEnd())).findFirst()
                        .ifPresent(cheapestNodeConnections::add);
            });

            NodeConnection cheapestNodeConnection = cheapestNodeConnections.stream().findFirst().orElse(null);
            if (cheapestNodeConnection == null)
                return new Path();

            Set<SortedSet<NodeConnection>> removedNodeConnectionsList = listPossibleNodeConnections.stream()
                    .filter(lNodeConections -> {
                        NodeConnection nodeConnection = lNodeConections.stream().findFirst().orElse(null);
                        if (nodeConnection != null) {
                            return (nodeConnection.getEnd().gethScore() + nodeConnection.getgScore()) <
                                    (cheapestNodeConnection.getgScore() + cheapestNodeConnection.getEnd().gethScore());
                        }
                        return false;
                    }).collect(Collectors.toSet());
            removedNodeConnectionsList.forEach(nodeConnectionsList -> this.path.removeAll(nodeConnectionsList));
            listPossibleNodeConnections.removeAll(removedNodeConnectionsList);
            // End getting cheapest road

            node = (NodeRoad) cheapestNodeConnection.getEnd();

            this.path.add(cheapestNodeConnection);
            if (node.equals(this.endNode)) {
                usedNodes.add(this.endNode);

                if (fromEnd[0] != null)
                    path.add(fromEnd[0].invert());

                if (maxPathLength < path.stream().mapToInt(NodeConnection::getgScore).sum())
                    path.clear();
                return path;
            }
            listPossibleNodeConnections.clear();
        }
        return new Path();
    }

    private NodeConnection findPathWithoutNodes(NormalRoad start, NormalRoad end) {
        final Direction[] directions = scene.getRoadConnections(start.getPosition());

        final int nbDir = directions.length;
        if (nbDir == 0 || nbDir > 2)
            return null;

//        final CountDownLatch latch = new CountDownLatch(nbDir);
//        final Runnable[] runnables = new Runnable[nbDir];
        final NodeConnection[] possibleNodeConnections = new NodeConnection[nbDir];

//        try {
        for (int i = 0; i < nbDir; i++) {
            int finalI = i;
//                runnables[i] = () -> {
            Set<Road> roads = new LinkedHashSet<>();
            roads.add(start);

            Direction dir = directions[finalI];
            TerrainPosition nextPos = start.getPosition().add(Direction.toRelativeDistance(dir));
            Direction[] nextDirs = scene.getRoadConnections(nextPos);
            int nbDirections = nextDirs.length;

            while (!nextPos.equals(end.getPosition()) && nbDirections > 1) {
                roads.add(new NormalRoad(nextPos));

                for (Direction direction : nextDirs)
                    if (!direction.equals(dir.getOppositeDirection())) {
                        dir = direction;
                        break;
                    }

                nextPos = nextPos.add(Direction.toRelativeDistance(dir));
                nextDirs = scene.getRoadConnections(nextPos);
                nbDirections = nextDirs.length;
            }
            if (nextPos.equals(end.getPosition())) {
                possibleNodeConnections[finalI] = new NodeConnection(start, end, roads); // Node found, route added
            }
//                    latch.countDown();
//                };
//                runnables[i].run();
        }
//            latch.await();

        if (nbDir == 1)
            return possibleNodeConnections[0];

        if (possibleNodeConnections[1] == null)
            return possibleNodeConnections[0];

        if (possibleNodeConnections[0] == null)
            return null;

        return (possibleNodeConnections[0].getgScore() + possibleNodeConnections[0].getEnd().gethScore()) <
                (possibleNodeConnections[1].getgScore() + possibleNodeConnections[1].getEnd().gethScore())
                ? possibleNodeConnections[0]
                : possibleNodeConnections[1];
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        return null;
    }

    public Path getPath() {
        return this.path;
    }

    /**
     * Get both nodes from each side of the road
     *
     * @param road the road
     * @return the nodes (can be 0 to 2)
     */
    public static NodeConnection[] getClosestNodes(NormalRoad road) {
        //Direction[] connectionsToRoadItem = terrain.getConnectionsToRoadItem(road.getPosition(), false); WHY?
        final Direction[] directions = scene.getRoadConnections(road.getPosition());

        final int nbDir = directions.length;
        if (nbDir == 0 /*|| connectionsToRoadItem.length == 0 || connectionsToRoadItem.length > 2*/ || nbDir > 2)
            return new NodeConnection[2];

        // nbDir = 1 or nbDir = 2
        final CountDownLatch latch = new CountDownLatch(nbDir);
        final Runnable[] runnables = new Runnable[nbDir];
        final NodeConnection[] possibleNodeConnections = new NodeConnection[2];

//        try {
        for (int i = 0; i < nbDir; i++) {
            int finalI = i;
//                runnables[i] = () -> {
            Set<Road> roads = new LinkedHashSet<>();
            roads.add(road);

            Direction dir = directions[finalI];
            TerrainPosition nextPos = road.getPosition().add(Direction.toRelativeDistance(dir));
            Direction[] nextDirs = scene.getRoadConnections(nextPos);
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
                nextDirs = scene.getRoadConnections(nextPos);
                nbDirections = nextDirs.length;
            }
            if (nbDirections > 2) { // Node found
                NodeRoad node = new NodeRoad(nextPos);
                roads.add(node); // Adding last road
                possibleNodeConnections[finalI] = new NodeConnection(road, node, roads); // Node found, route added
            }
//                    latch.countDown();
//                };
//                runnables[i].run();
        }
//            latch.await();


        if (possibleNodeConnections[0] == null && possibleNodeConnections[1] != null) {
            possibleNodeConnections[0] = possibleNodeConnections[1];
            possibleNodeConnections[1] = null;
        }
//            if (nbDir == 1)
//                return possibleNodeConnections[0];
//
//            if (possibleNodeConnections[0] == null || possibleNodeConnections[1] == null)
//                return null;
//
//            return (possibleNodeConnections[0].getgScore() + possibleNodeConnections[0].getEnd().gethScore()) <
//                    (possibleNodeConnections[1].getgScore() + possibleNodeConnections[1].getEnd().gethScore()) ? possibleNodeConnections[0]
//                    : possibleNodeConnections[1];
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return possibleNodeConnections;
    }

    public void reset() {
        this.ambigueousEnd = false;
        this.ambigueousStart = false;
        this.fromStart = new NodeConnection[2];
        this.fromEnd = new NodeConnection[2];
        this.path = new Path();
        this.endNode = null;
        this.startNode = null;
    }

    /**
     * Finds the path between two positions
     * This path must be a straight line
     *
     * @param startPos starting position
     * @param endPos final position
     * @return empty path if none is found
     */
    public Path findStraightClearPath(final TerrainPosition startPos, final TerrainPosition endPos) {
        Path path = new Path();

        if (startPos == null || endPos == null)
            return path;

        if (startPos.equals(endPos)) {
            path.add(new NodeConnection(new NormalRoad(startPos)));
            return path;
        }

        if (startPos.getX() != endPos.getX() && startPos.getZ() != endPos.getZ())
            return path;

        Set<TerrainPosition> previewItemPositions = scene.getPreviewItemPositions();

        if ((scene.isPositionOccupied(startPos) && !previewItemPositions.contains(startPos)) ||
                (scene.isPositionOccupied(endPos) && !previewItemPositions.contains(endPos)))
            return path;

        Vector2f vector = new Vector2f(endPos.getX() - startPos.getX(), endPos.getZ() - startPos.getZ());
        Direction direction = Direction.getDirectionFromVector(vector);
        if (direction == null)
            return path;

        NodeConnection nodeConnection = new NodeConnection(new NormalRoad(startPos));
        TerrainPosition pos = startPos;
        for (int i = 0; i < Maths.manhattanDistance(startPos, endPos); i++) {
            pos = pos.add(Direction.toRelativeDistance(direction, 1));
            nodeConnection.addRoad(new NormalRoad(pos));

            if (scene.isPositionOccupied(pos))
                return path;
        }
        path.add(nodeConnection);
        return path;
    }


    @Deprecated
    public Path findUnobstructedPathV1(final TerrainPosition startPos, final TerrainPosition endPos) {
//        int manhattanDistance = manhattanDistance(startPos, endPos);
//        final int MAX = 15 + manhattanDistance;
//
//         
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
        Path pathRoads = new Path();
        if (startPos == null || endPos == null || startPos.equals(endPos))
            return pathRoads;


        Set<TerrainPosition> previewItemPositions = scene.getPreviewItemPositions();

        if ((scene.isPositionOccupied(startPos) && !previewItemPositions.contains(startPos)) ||
                (scene.isPositionOccupied(endPos) && !previewItemPositions.contains(endPos)))
            return pathRoads;

        Road start = new NormalRoad(startPos);
        NodeConnection nodeConnection = new NodeConnection(start);
        TerrainPosition currPos = new TerrainPosition(startPos);
        while (currPos.getX() != endPos.getX()) {
            if (currPos.getX() < endPos.getX()) {
                currPos = currPos.add(new TerrainPosition(1, 0));
            } else
                currPos = currPos.add(new TerrainPosition(-1, 0));

            if (scene.isPositionOccupied(currPos) && !previewItemPositions.contains(currPos))
                return new Path();
            nodeConnection.addRoad(new NormalRoad(currPos));
        }

        if (currPos.getX() != startPos.getX()) { // x changed
            NormalRoad crossing = new NormalRoad(currPos);
            nodeConnection.setEnd(crossing);
            pathRoads.add(nodeConnection);

            if (currPos.equals(endPos))
                return pathRoads;

            nodeConnection = new NodeConnection(crossing);
        }

        while (currPos.getZ() != endPos.getZ()) {
            if (currPos.getZ() < endPos.getZ()) {
                currPos = currPos.add(new TerrainPosition(0, 1));
            } else
                currPos = currPos.add(new TerrainPosition(0, -1));

            if (scene.isPositionOccupied(currPos) && !previewItemPositions.contains(currPos))
                return new Path();

            nodeConnection.addRoad(new NormalRoad(currPos));
        }

        assert currPos.equals(endPos);

        nodeConnection.setEnd(new NormalRoad(endPos));
        pathRoads.add(nodeConnection);

        return pathRoads;
    }

    @Deprecated
    public Path findUnobstructedPathV2(final TerrainPosition startPos, final TerrainPosition endPos) {
        Path pathRoads = new Path();
        if (startPos == null || endPos == null || startPos.equals(endPos))
            return pathRoads;


//        Set<TerrainPosition> previewItemPositions = terrain.getPreviewItemPositions();
        Set<TerrainPosition> previewItemPositions = scene.getPreviewItemPositions();
        if ((scene.isPositionOccupied(startPos) && !previewItemPositions.contains(startPos)) ||
                (scene.isPositionOccupied(endPos) && !previewItemPositions.contains(endPos)))
            return pathRoads;

        Road start = new NormalRoad(startPos);
        NodeConnection nodeConnection = new NodeConnection(start);
        TerrainPosition currPos = new TerrainPosition(startPos);

        while (currPos.getZ() != endPos.getZ()) {
            if (currPos.getZ() < endPos.getZ()) {
                currPos = currPos.add(new TerrainPosition(0, 1));
            } else
                currPos = currPos.add(new TerrainPosition(0, -1));

            if (scene.isPositionOccupied(currPos) && !previewItemPositions.contains(currPos))
                return new Path();

            nodeConnection.addRoad(new NormalRoad(currPos));
        }

        if (currPos.getZ() != startPos.getZ()) { // y changed
            NormalRoad crossing = new NormalRoad(currPos);
            nodeConnection.setEnd(crossing);
            pathRoads.add(nodeConnection);

            if (currPos.equals(endPos))
                return pathRoads;

            nodeConnection = new NodeConnection(crossing);
        }

        while (currPos.getX() != endPos.getX()) {
            if (currPos.getX() < endPos.getX()) {
                currPos = currPos.add(new TerrainPosition(1, 0));
            } else
                currPos = currPos.add(new TerrainPosition(-1, 0));

            if (scene.isPositionOccupied(currPos) && !previewItemPositions.contains(currPos))
                return new Path();

            nodeConnection.addRoad(new NormalRoad(currPos));
        }

        assert currPos.equals(endPos);
        nodeConnection.setEnd(new NormalRoad(endPos));
        pathRoads.add(nodeConnection);

        return pathRoads;
    }
}