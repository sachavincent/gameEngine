package pathfinding;

import static util.math.Maths.manhattanDistance;

import java.util.*;
import java.util.stream.Collectors;
import models.RawModel;
import renderEngine.Loader;
import terrains.TerrainPosition;
import util.math.Vector2f;
import util.math.Vector3f;

public class Path extends LinkedHashSet<NodeConnection> implements Comparable<Path> {

    private PathCoordinates pathCoordinates;

    public Path() {
    }

    public Path(Collection<? extends NodeConnection> c) {
        super(c.stream().map(NodeConnection::new).collect(Collectors.toCollection(Path::new)));
    }

    @Override
    public boolean add(NodeConnection nodeConnection) {
        if (nodeConnection == null)
            return false;

        if (nodeConnection.getEnd() != null)
            nodeConnection.getRoads().add(nodeConnection.getEnd());

        return super.add(nodeConnection);
    }

    public boolean compareCost(Path path) {
        return path.getCost() == getCost();
    }

    public boolean comparePaths(Path path) {
        List<Road> allRoads = getAllRoads();
        List<Road> allRoadsPath = path.getAllRoads();

        if (allRoads.size() != allRoadsPath.size())
            return false;

        for (int i = 0; i < allRoads.size(); i++)
            if (allRoads.get(i).getClass() != allRoadsPath.get(i).getClass())
                return false;

        List<TerrainPosition> collect = allRoads.stream().map(Road::getPosition).collect(Collectors.toList());
        List<TerrainPosition> collect1 = allRoadsPath.stream().map(Road::getPosition).collect(Collectors.toList());

        return collect.equals(collect1);
    }

    public int getCost() {
        if (this.isEmpty())
            return Integer.MAX_VALUE;

        return this.stream().mapToInt(nodeConnection ->
                nodeConnection.getRoads().stream().mapToInt(Road::getScore).sum()).sum();
    }

    public List<Road> getAllRoads() {
        List<Road> roads = new LinkedList<>();
        this.stream().map(NodeConnection::getRoads).forEach(roads::addAll);

        return Collections.unmodifiableList(roads);
    }

    public List<Road> getAllUniqueRoads() {
        Set<Road> roads = new LinkedHashSet<>();
        this.stream().map(NodeConnection::getRoads).forEach(roads::addAll);

        return Collections.unmodifiableList(new ArrayList<>(roads));
    }

    public Path addAtStart(NodeConnection nodeConnection) {
        List<NodeConnection> tmp = new ArrayList<>(new Path(this));
        Collections.reverse(tmp);
        tmp.add(nodeConnection);
        Collections.reverse(tmp);

        return new Path(tmp);
    }

    public Path invertPath() {
        List<NodeConnection> tmp = new ArrayList<>();
        for (NodeConnection nodeConnection : this) {
            tmp.add(nodeConnection.invert());
        }
        Collections.reverse(tmp);

        return new Path(new LinkedHashSet<>(tmp));
    }

    public Road getStart() {
        if (isEmpty())
            return null;

        return this.stream().findFirst().map(NodeConnection::getStart).orElse(null);
    }

    public Road getEnd() {
        if (isEmpty())
            return null;

        return this.stream().skip(size() - 1).map(NodeConnection::getEnd).findFirst().orElse(null);
    }

    public NodeRoad getClosestNodeRoad(TerrainPosition terrainPosition) {
        return getAllRoads().stream().filter(NodeRoad.class::isInstance).map(NodeRoad.class::cast).min(
                Comparator.comparingInt(nodeRoad -> manhattanDistance(nodeRoad.getPosition(), terrainPosition)))
                .orElse(null);
    }

    public Road getClosestRoad(TerrainPosition terrainPosition) {
        return getAllRoads().stream().min(
                Comparator.comparingInt(road -> manhattanDistance(road.getPosition(), terrainPosition)))
                .orElse(null);
    }

    @Override
    public int compareTo(Path o) {
        return Integer.compare(getCost(), o.getCost());
    }

    public Road getRoadAt(TerrainPosition position) {
        return getAllRoads().stream().filter(road -> road.getPosition().equals(position)).findFirst().orElse(null);
    }

    /**
     * Merge 2 routes together
     *
     * @param path1 end must equal
     * @param path2 start
     * @return new route
     */
    public static Path mergePaths(Path path1, Path path2) {
        if (path1.isEmpty())
            return path2;
        if (path2.isEmpty())
            return path1;

        final Road endPath1 = path1.getEnd();
        final Road startPath2 = path2.getStart();
        if (endPath1.equals(startPath2)) {
            if (endPath1 instanceof NodeRoad) { // 2 different paths
                Path newPath = new Path(path1);
                newPath.addAll(path2);
                return newPath;
            }

            // One single path
            final List<Road> roads = new LinkedList<>();
            Path newPath = new Path();
            path1.forEach(nodeConnection -> {
                if (!endPath1.equals(nodeConnection.getEnd()) && path1.size() > 1) { // Not last
                    newPath.add(nodeConnection);
                } else if (endPath1.equals(nodeConnection.getEnd()) || path1.size() == 1) // Last
                    roads.addAll(nodeConnection.getRoads());
            });
            path2.forEach(nodeConnection -> {
                if ((startPath2.equals(nodeConnection.getStart()) && path1.size() > 1) || !roads.isEmpty()) { // First
                    if (path2.size() > 1) {
                        nodeConnection.getRoads().removeAll(roads);
                        roads.addAll(nodeConnection.getRoads());
                    }

                    final Road start = roads.isEmpty() ? path1.getEnd() : roads.get(0);

                    if (!startPath2.equals(nodeConnection.getStart()) || path2.size() == 1)
                        roads.addAll(path2.getAllRoads());
                    NodeConnection newNodeConnection = new NodeConnection(start, nodeConnection.getEnd(), roads);
                    newPath.add(newNodeConnection);
                    roads.clear();
                } else if (startPath2.equals(nodeConnection.getStart()) || path2.size() > 1) // Not first
                    newPath.add(nodeConnection);
            });

            return newPath;
        } else
            return path1;
    }

    public void savePathCoordinates() {
        Set<Vector2f> positions = new LinkedHashSet<>();
        forEach(nodeConnection -> {
            List<Road> roads = new ArrayList<>(nodeConnection.getRoads());

            if (roads.size() == 1) {
                TerrainPosition position = roads.get(0).getPosition();
                Vector3f centerPos = position.toVector3f().add(new Vector3f(0.5, 0, 0.5));
                positions.add(new Vector2f(centerPos.getX(), centerPos.getZ() - 0.5));
                positions.add(new Vector2f(centerPos.getX(), centerPos.getZ() + 0.5));
                positions.add(new Vector2f(centerPos.getX(), centerPos.getZ()));
                positions.add(new Vector2f(centerPos.getX() - 0.5, centerPos.getZ()));
                positions.add(new Vector2f(centerPos.getX() + 0.5, centerPos.getZ()));
            } else
                roads.stream().map(Road::getPosition)
                        .forEach(pos -> positions.add(new Vector2f(pos.getX() + .5f, pos.getZ() + .5f)));
        });

        if (positions.isEmpty())
            return;

        float[] positionsFloat = new float[positions.size() * 3];
        int i = 0;
        for (Vector2f pos : positions) {
            positionsFloat[i++] = pos.x;
            positionsFloat[i++] = .5f;
            positionsFloat[i++] = pos.y;
        }
        int[] indicesTab = new int[positions.size() * 2 - 2];
        int j = 0;
        for (i = 0; i < indicesTab.length; i++) {
            indicesTab[i++] = j++;
            indicesTab[i] = j;
        }

        this.pathCoordinates = new PathCoordinates(positionsFloat, indicesTab);
    }

    public RawModel createRawModel() {
        if (this.pathCoordinates != null)
            return Loader.getInstance()
                    .loadToVAO(this.pathCoordinates.getPositions(), new float[]{0}, new float[]{0, 1, 0},
                            this.pathCoordinates.getIndices());
        return null;
    }

    public boolean isLegal() {
        Road start;
        Road end = null;
        for (NodeConnection nodeConnection : this) {
            start = nodeConnection.getStart();
            if (end != null && !start.equals(end))
                return false;

            end = nodeConnection.getEnd();
        }
        return true;
    }

    public static class PathCoordinates {

        final private float[] positions;
        final private int[]   indices;

        public PathCoordinates(float[] positions, int[] indices) {
            this.positions = positions;
            this.indices = indices;
        }

        public float[] getPositions() {
            return this.positions;
        }

        public int[] getIndices() {
            return this.indices;
        }
    }
}