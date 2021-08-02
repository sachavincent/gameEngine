package pathfinding;

import entities.Camera.Direction;
import java.util.*;
import java.util.stream.Collectors;
import scene.Scene;
import scene.callbacks.FilterGameObjectCallback;
import scene.components.RoadComponent;
import terrains.TerrainPosition;

public class RoadGraph {

    private       Set<NodeRoad>           nodes;
    private final TreeSet<NodeConnection> nodeConnections;

    public static final FilterGameObjectCallback FILTER = gameObject -> gameObject.hasComponent(RoadComponent.class);

    public RoadGraph() {
        this.nodes = new HashSet<>();
        this.nodeConnections = new TreeSet<>((nc1, nc2) -> {
                    int gScore = nc1.getgScore();
        int othergScore = nc2.getgScore();
        int fScore = gScore + nc2.getEnd().gethScore();
        int fScoreOther = othergScore + nc2.getEnd().gethScore();

        int compare = Integer.compare(fScore, fScoreOther);
        if (compare != 0)
            return compare;

        compare = Integer.compare(gScore, othergScore);
        if (compare != 0)
            return compare;

        compare = nc1.getEnd().getPosition().compareTo(nc2.getEnd().getPosition());
        if (compare != 0)
            return compare;

        compare = nc1.getStart().getPosition().compareTo(nc2.getStart().getPosition());
        if (compare != 0)
            return compare;

        return Integer.compare(nc1.getRoads().hashCode(), nc2.getRoads().hashCode());
        });
    }

    public RoadGraph(Set<NodeRoad> nodes, TreeSet<NodeConnection> nodeConnections) {
        this.nodes = nodes;
        this.nodeConnections = nodeConnections;
    }

    public RoadGraph copy() {
        return new RoadGraph(this.nodes.stream().map(NodeRoad::new).collect(Collectors.toSet()),
                this.nodeConnections.stream().map(NodeConnection::new)
                        .collect(Collectors.toCollection(TreeSet<NodeConnection>::new)));
    }

    public void searchForNextNode(TerrainPosition position, Direction[] directions,
            NodeConnection currentNodeConnection) {
        if (currentNodeConnection == null) {
            NodeRoad start = new NodeRoad(position);
            currentNodeConnection = new NodeConnection(start);

            nodes.add(start);
        }

        assert currentNodeConnection.getEnd() == null;

        Scene scene = Scene.getInstance();
        for (Direction direction : directions) {
            TerrainPosition nextRoadPosition = position.add(Direction.toRelativeDistance(direction));

//            Direction[] directionsNextRoad = terrain.getRoadDirections(nextRoadPosition);
            Direction[] directionsNextRoadOnlyRoads = scene.getConnectedDirections(nextRoadPosition, FILTER);

            Direction oppositeDirection = direction.toOppositeDirection();

            if (directionsNextRoadOnlyRoads.length == 0) // Impossible ?
                return;
//                throw new IllegalStateException("No direction, impossible state.");

            if (directionsNextRoadOnlyRoads.length == 1) // Dead end
                continue;

            if (directionsNextRoadOnlyRoads.length > 2) {
                // Node found (directionsNextRoad.length > 3 && directionsNextRoad.length <= 4)
                NodeRoad newNode = new NodeRoad(nextRoadPosition);
                NodeConnection newNodeConnection = new NodeConnection(currentNodeConnection);
                newNodeConnection.setEnd(newNode);
                newNodeConnection.addRoad(newNode);

                this.nodeConnections.add(newNodeConnection);
                this.nodeConnections.add(newNodeConnection.invert());

                Set<NodeConnection> nodeConnectionsToDelete = new HashSet<>();
                this.nodeConnections.stream().filter(nodeConnection ->
                        (nodeConnection.getEnd().equals(newNodeConnection.getEnd()) &&
                                !newNodeConnection.getStart().equals(nodeConnection.getStart()) &&
                                nodeConnection.getRoads().contains(newNodeConnection.getStart())) ||
                                (nodeConnection.getEnd().equals(newNodeConnection.getStart()) &&
                                        !newNodeConnection.getEnd().equals(nodeConnection.getStart()) &&
                                        nodeConnection.getRoads().contains(newNodeConnection.getEnd())))
                        .forEach(nodeConnectionsToDelete::add);

                this.nodeConnections.removeAll(nodeConnectionsToDelete);

                List<Direction> directionList = new ArrayList<>(Arrays.asList(directionsNextRoadOnlyRoads));
                directionList.remove(oppositeDirection);
                if (!this.nodes.contains(new NodeRoad(nextRoadPosition)))
                    searchForNextNode(nextRoadPosition, directionList.toArray(new Direction[0]), null);
                continue;
            }

            // 2 sides connected
            Direction nextDir = null;
            for (Direction nextDirection : directionsNextRoadOnlyRoads)
                if (!oppositeDirection.equals(nextDirection))
                    nextDir = nextDirection;

            if (nextDir != null) {
                NodeConnection nextNodeConnection = new NodeConnection(currentNodeConnection);
                nextNodeConnection.addRoad(new NormalRoad(nextRoadPosition)); // No node yet, add road

                searchForNextNode(nextRoadPosition, new Direction[]{nextDir}, nextNodeConnection);
            }
        }
    }

    public void setNodes(Set<NodeRoad> nodes) {
        this.nodes = nodes;
    }

    public Set<NodeRoad> getNodes() {
        return this.nodes;
    }

    public Set<NodeConnection> getNodeConnections() {
        return this.nodeConnections;
    }

    public Set<NodeConnection> getNodeConnections(NodeRoad node) {
        return this.nodeConnections.stream()
                .filter(nodeConnection -> nodeConnection.getStart().getPosition().equals(node.getPosition()))
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Add road to position dynamically
     */
    public void addRoad(TerrainPosition position) {
        Direction[] connectionsToRoadItem = Scene.getInstance().getConnectedDirections(position, FILTER);
        if (connectionsToRoadItem.length >= 3) { // New road is a node
            searchForNextNode(position, connectionsToRoadItem, null);
        } else {
            NormalRoad road = new NormalRoad(position);
            NodeConnection[] closestNodes = PathFinder.getClosestNodes(road);
            if (closestNodes[0] != null && closestNodes[1] != null && connectionsToRoadItem.length == 2) {
                NodeConnection nodeConnection1 = closestNodes[0].invert();
                NodeConnection nodeConnection2 = closestNodes[1];
                if (!nodeConnection1.getStart().equals(nodeConnection2.getEnd())) {
                    NodeConnection nodeConnection = new NodeConnection(nodeConnection1.getStart(),
                            nodeConnection2.getEnd());
                    nodeConnection1.getRoads().forEach(nodeConnection::addRoad);
                    nodeConnection.addRoad(road);
                    nodeConnection2.getRoads().forEach(nodeConnection::addRoad);

                    this.nodes.add((NodeRoad) nodeConnection1.getStart());
                    this.nodes.add((NodeRoad) nodeConnection2.getEnd());

                    this.nodeConnections.add(nodeConnection);
                    this.nodeConnections.add(nodeConnection.invert());
                }
            } /*else {*/
            for (Direction direction : connectionsToRoadItem) {
                TerrainPosition newPos = position.add(Direction.toRelativeDistance(direction));
                Direction[] directions = Scene.getInstance().getConnectedDirections(newPos, FILTER);
                if (directions.length == 3) { // New node created by this road
                    searchForNextNode(newPos, directions, null);
                }

                if (directions.length > 2 && connectionsToRoadItem.length > 1) { // See Test 19 to see why!
                    NodeConnection nodeConnection = new NodeConnection(new NodeRoad(newPos));
                    nodeConnection.addRoad(new NormalRoad(position));

                    Direction[] newDirections = new Direction[connectionsToRoadItem.length - 1];
                    int i = 0;
                    for (Direction dir : connectionsToRoadItem) {
                        if (dir != direction)
                            newDirections[i++] = dir;
                    }
                    searchForNextNode(position, newDirections, nodeConnection);
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RoadGraph roadGraph = (RoadGraph) o;
        return Objects.equals(this.nodes, roadGraph.nodes) &&
                Objects.equals(this.nodeConnections, roadGraph.nodeConnections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodes, nodeConnections);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        this.nodeConnections.forEach(nodeConnection -> stringBuilder.append(nodeConnection).append("\n"));
        StringBuilder stringBuilder2 = new StringBuilder();
        this.nodes.forEach(node -> stringBuilder2.append(node).append("\n"));
        return "Graph{" +
                "nodes (" + this.nodes.size() + ") =" + stringBuilder2 +
                "\t, nodeConnections(" + this.nodeConnections.size() + ") =" + stringBuilder +
                '}';
    }
}