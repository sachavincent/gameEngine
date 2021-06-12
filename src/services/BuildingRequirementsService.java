package services;

import java.awt.Color;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import pathfinding.Path;
import pathfinding.PathFinder;
import scene.components.RoadConnectionsComponent;
import scene.components.requirements.BuildingRequirement;
import scene.components.requirements.RequirementComponent;
import scene.gameObjects.GameObject;

public class BuildingRequirementsService extends Service<Map<Path, Color>> {

    private final Set<GameObject> gameObjects;

    public BuildingRequirementsService(boolean singleton, Set<GameObject> gameObjects, OnServiceDone<Map<Path, Color>> onServiceDone) {
        super(singleton, onServiceDone);

        this.gameObjects = gameObjects;
    }

    @Override
    protected synchronized Map<Path, Color> execute() {
        Map<Path, Color> paths = new HashMap<>();
        try {
            for (GameObject gameObject : this.gameObjects) {
                Color color = new Color((int) (Math.random() * 0x1000000));
                if (!this.running)
                    return paths;

                Set<Path> foundPaths = new TreeSet<>(Comparator.comparingInt(Path::getCost));
                boolean hasRoadConnections = gameObject.hasComponent(RoadConnectionsComponent.class);
                if (!hasRoadConnections)
                    continue;

                RoadConnectionsComponent component = gameObject.getComponent(RoadConnectionsComponent.class);
//                RequireBuildingComponent requireComponent = gameObject.getComponent(RequireBuildingComponent.class);
                RequirementComponent requireComponent = gameObject.getComponent(RequirementComponent.class);
                if (component.isConnected()) {
                    Set<BuildingRequirement> buildingRequirements = requireComponent
                            .getRequirementsOfType(BuildingRequirement.class);
                    for (BuildingRequirement buildingRequirement : buildingRequirements) {
                        requireComponent.clearRequirement(buildingRequirement);
                    }
                    for (BuildingRequirement buildingRequirement : buildingRequirements) {
                        Class<? extends GameObject> objectClass = buildingRequirement.getKey();
                        if (!this.running)
                            return paths;

                        Path path = PathFinder.findPath(gameObject, objectClass, buildingRequirement.getValue(), true);
                        if (!path.isEmpty()) {
                            foundPaths.add(path);
                            buildingRequirement.setPath(path);
                            requireComponent.meetRequirement(buildingRequirement);
                        }
                    }
                }
                if (!foundPaths.isEmpty()) { // Path found
                    for (Path path : foundPaths) {
                        path.savePathCoordinates();
                        paths.put(path, color);
                    }
                }
            }
        } catch (ConcurrentModificationException e) {
            this.running = false;

            return paths;
        }
        return paths;
    }

}