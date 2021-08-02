package pathfinding;

import java.util.*;
import java.util.stream.Collectors;

public class NodeConnection implements Comparable<NodeConnection> {

    private Set<Road> roads = new LinkedHashSet<>();

    private final Road start;
    private       Road end;

    public NodeConnection(Road start) {
        this(start, null);
    }

    public NodeConnection(Road start, Road end, Collection<Road> roads) {
        this.roads = new LinkedHashSet<>(roads);
        this.start = start;
        this.end = end;
    }

    public NodeConnection(Road start, Road end) {
        this.start = start;
        this.end = end;

        this.roads.add(start);
    }

    public NodeConnection(NodeConnection currentNodeConnection) {
        this.start = currentNodeConnection.start != null ? currentNodeConnection.start.clone() : null;
        this.end = currentNodeConnection.end != null ? currentNodeConnection.end.clone() : null;

        this.roads = new LinkedHashSet<>();
        if (currentNodeConnection.getRoads() != null) {
            this.roads = currentNodeConnection.getRoads().stream().map(Road::clone)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }
    }

    public Set<Road> getRoads() {
        return this.roads;
    }

    @Override
    public int compareTo(NodeConnection other) {
        int gScore = getgScore();
        int othergScore = other.getgScore();
        int fScore = gScore + end.gethScore();
        int fScoreOther = othergScore + other.getEnd().gethScore();

        int compare = Integer.compare(fScore, fScoreOther);
        if (compare != 0)
            return compare;

//        compare = Integer.compare(gScore, othergScore);
//        if (compare != 0)
//            return compare;

        compare = end.getPosition().compareTo(other.getEnd().getPosition());
        if (compare != 0)
            return compare;

        compare = start.getPosition().compareTo(other.getStart().getPosition());
        if (compare != 0)
            return compare;

        return Integer.compare(roads.hashCode(), other.roads.hashCode());
    }


    public void setEnd(Road end) {
        this.end = end;
    }

    public Road getStart() {
        return this.start;
    }

    public Road getEnd() {
        return this.end;
    }

    public int getgScore() {
        if (start == null || end == null)
            return 0; // Connection not completed

        return roads.stream().filter(Objects::nonNull).mapToInt(Road::getScore).sum();
    }

    public boolean addRoad(Road road) {
        return this.roads.add(road);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        NodeConnection nodeConnection = (NodeConnection) o;
        return Objects.equals(new ArrayList<>(roads), new ArrayList<>(nodeConnection.roads)) &&
                Objects.equals(start, nodeConnection.start) &&
                Objects.equals(end, nodeConnection.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.roads, this.start, this.end);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        this.roads.forEach(road -> stringBuilder.append("\t\t\t\t").append(road).append("\n"));
        return "NodeConnection{" +
                "start=" + this.start +
                ", end=" + this.end +
                ", gScore=" + getgScore() +
                "\n\t\t, roads=\n" + stringBuilder +
                '}';
    }

    public NodeConnection invert() {
        List<Road> invertedConnection = new ArrayList<>(this.roads);
        Collections.reverse(invertedConnection);

        return new NodeConnection(this.end, this.start, new LinkedHashSet<>(invertedConnection));
    }
}
