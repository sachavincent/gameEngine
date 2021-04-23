package services;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import pathfinding.RouteFinder;
import pathfinding.RouteFinder.Route;
import scene.gameObjects.GameObject;
import scene.components.RequireBuildingComponent;
import scene.components.RoadConnectionsComponent;
import scene.Scene;

public class BuildingRequirementsService extends Service<List<Route>> {

    private final Set<GameObject> gameObjects;

    public BuildingRequirementsService(boolean singleton, OnServiceDone<List<Route>> onServiceDone) {
        super(singleton, onServiceDone);

//        this.buildings = Terrain.getInstance().getBuildings().stream()
//                .filter(RequireBuilding.class::isInstance)
//                .filter(buildingItem -> !((RequireBuilding) buildingItem).doesMeetAllRequirements())
//                .collect(Collectors.toSet());
        this.gameObjects = Scene.getInstance().getGameObjects().stream().filter(gameObject -> gameObject.hasComponent(
                RequireBuildingComponent.class)).collect(Collectors.toSet());
    }

    @Override
    protected synchronized List<Route> execute() {
        List<Route> paths = new ArrayList<>();
        try {
            for (GameObject gameObject : gameObjects) {
                if (!running)
                    return paths;

                Set<Route> foundRoutes = new TreeSet<>(Comparator.comparingInt(Route::getCost));
                boolean hasRoadConnections = gameObject.hasComponent(RoadConnectionsComponent.class);
                if (!hasRoadConnections)
                    continue;

                RoadConnectionsComponent component = gameObject.getComponent(RoadConnectionsComponent.class);
                RequireBuildingComponent requireComponent = gameObject.getComponent(RequireBuildingComponent.class);
                if (component.isConnected()) {
                    Map<Class<? extends GameObject>, Integer> requirements = requireComponent.getRequirements();
                    for (Entry<Class<? extends GameObject>, Integer> entry : requirements.entrySet()) {
                        Class<? extends GameObject> objectClass = entry.getKey();
                        if (!running)
                            return paths;

                        Route route = RouteFinder.findRoute(gameObject, objectClass, entry.getValue());
                        if (!route.isEmpty()) {
                            foundRoutes.add(route);

                            requireComponent.meetRequirement(objectClass, route);
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