package services;

import items.ConnectableItem;
import items.abstractItem.AbstractItem;
import items.buildings.BuildingItem;
import items.buildings.houses.RequireBuilding;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import pathfinding.RouteFinder;
import pathfinding.RouteFinder.Route;
import terrains.Terrain;

public class BuildingRequirementsService extends Service<List<Route>> {

    private final Set<BuildingItem> buildings;

    public BuildingRequirementsService(boolean singleton, OnServiceDone<List<Route>> onServiceDone) {
        super(singleton, onServiceDone);

        this.buildings = Terrain.getInstance().getBuildings().stream()
                .filter(RequireBuilding.class::isInstance)
                .filter(buildingItem -> !((RequireBuilding) buildingItem).doesMeetAllRequirements())
                .collect(Collectors.toSet());
    }

    @Override
    protected synchronized List<Route> execute() {
        List<Route> paths = new ArrayList<>();
        try {
            for (BuildingItem item : buildings) {
                if (!running)
                    return paths;

                final RequireBuilding requiringBuilding = (RequireBuilding) item;

                Set<Route> foundRoutes = new TreeSet<>(Comparator.comparingInt(Route::getCost));

                if (((ConnectableItem) requiringBuilding).isConnected()) {
                    Map<AbstractItem, Integer> requirements = requiringBuilding.getRequirements();
                    for (Entry<AbstractItem, Integer> entry : requirements.entrySet()) {
                        AbstractItem neededBuilding = entry.getKey();
                        if (!running)
                            return paths;

                        Route route = RouteFinder.findRoute(item, neededBuilding, entry.getValue());
                        if (!route.isEmpty()) {
                            foundRoutes.add(route);

                            requiringBuilding.meetRequirement(neededBuilding, route);
//                                try {
//                                    sleep(200);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
                        }
                    }
                }
//                    System.out.println(bestRoute);
                if (!foundRoutes.isEmpty()) { // Route found
                    paths.addAll(foundRoutes);
                }
            }
        } catch (ConcurrentModificationException e) {
            running = false;

            return paths;
        }
        return paths;
    }

}