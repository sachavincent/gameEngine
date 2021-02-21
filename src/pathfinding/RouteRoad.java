package pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class RouteRoad implements Comparable<RouteRoad> {

    private Set<Road> route = new LinkedHashSet<>();

    private final Road start;
    private       Road end;

    public RouteRoad(Road start) {
        this(start, null);
    }

    public RouteRoad(Road start, Road end, Set<Road> route) {
        this.route = route;
        this.start = start;
        this.end = end;
    }

    public RouteRoad(Road start, Road end) {
        this.start = start;
        this.end = end;

        this.route.add(start);
    }

    public RouteRoad(RouteRoad currentRoute) {
        this.start = currentRoute.start;
        this.end = currentRoute.end;
        this.route = new LinkedHashSet<>(currentRoute.getRoute());
    }

    public Set<Road> getRoute() {
        return this.route;
    }

    @Override
    public int compareTo(RouteRoad other) {
        int fScore = getgScore() + end.gethScore();
        int fScoreOther = other.getgScore() + other.getEnd().gethScore();

        int compare = Integer.compare(fScore, fScoreOther);
        if (compare != 0)
            return compare;

        compare = end.getPosition().compareTo(other.getEnd().getPosition());
        if (compare != 0)
            return compare;

        compare = start.getPosition().compareTo(other.getStart().getPosition());
        if (compare != 0)
            return compare;

        compare = Integer.compare(route.size(), other.route.size());
        if (compare != 0)
            return compare;

        if (route.equals(other.route))
            return 0;

        return Integer.compare(route.hashCode(), other.route.hashCode());
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
            return 0; // Route not completed

        return route.stream().filter(Objects::nonNull).mapToInt(Road::getScore).sum();
    }

    public boolean addRoad(Road road) {
        return this.route.add(road);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RouteRoad routeRoad = (RouteRoad) o;
        return Objects.equals(route, routeRoad.route) &&
                Objects.equals(start, routeRoad.start) &&
                Objects.equals(end, routeRoad.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(route, start, end);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        route.forEach(road -> stringBuilder.append("\t\t\t").append(road).append("\n"));
        return "RouteRoad{" +
                "start=" + start +
                ", end=" + end +
                ", gScore=" + getgScore() +
                "\n\t\t, roads=" + stringBuilder.toString() +
                '}';
    }

    public RouteRoad invertRoute() {
        List<Road> invertedRoute = new ArrayList<>(this.route);
        Collections.reverse(invertedRoute);

        return new RouteRoad(end, start, new LinkedHashSet<>(invertedRoute));
    }
}
