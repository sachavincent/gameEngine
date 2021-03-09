package services;

import pathfinding.RoadGraph;
import pathfinding.RouteFinder;
import pathfinding.RouteFinder.Route;
import terrains.TerrainPosition;

public class PathfindingService extends Service<Route> {

    private final TerrainPosition from;
    private final TerrainPosition end;
    private final RoadGraph       roadGraph;
    private final int             maxLength;

    public PathfindingService(boolean singleton, RoadGraph roadGraph, TerrainPosition from, TerrainPosition end, int maxLength,
            OnServiceDone<Route> onServiceDone) {
        super(singleton, onServiceDone);

        this.from = from;
        this.end = end;
        this.maxLength = maxLength;
        this.roadGraph = roadGraph;
    }

    @Override
    protected synchronized Route execute() {
        RouteFinder routeFinder = new RouteFinder(roadGraph);

        return routeFinder.findBestRoute(from, end, maxLength);
    }
}