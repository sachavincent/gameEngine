package services;

import pathfinding.Path;
import pathfinding.PathFinder;
import pathfinding.RoadGraph;
import terrains.TerrainPosition;

public class PathfindingService extends Service<Path> {

    private final TerrainPosition from;
    private final TerrainPosition end;
    private final RoadGraph       roadGraph;
    private final int             maxLength;

    public PathfindingService(boolean singleton, RoadGraph roadGraph, TerrainPosition from, TerrainPosition end,
            int maxLength,
            OnServiceDone<Path> onServiceDone) {
        super(singleton, onServiceDone);

        this.from = from;
        this.end = end;
        this.maxLength = maxLength;
        this.roadGraph = roadGraph;
    }

    @Override
    protected synchronized Path execute() {
        PathFinder pathFinder = new PathFinder(this.roadGraph);

        return pathFinder.findBestPath(this.from, this.end, this.maxLength);
    }
}