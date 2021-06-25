package services;

import java.awt.Color;
import java.util.*;
import java.util.stream.Collectors;
import pathfinding.Path;
import pathfinding.PathFinder;
import pathfinding.Road;
import renderEngine.PathRenderer;
import scene.Scene;
import scene.components.ConnectionsComponent;
import scene.components.requirements.BuildingRoadConnectionRequirement;
import scene.components.requirements.RequirementComponent;
import scene.gameObjects.GameObject;
import terrains.TerrainPosition;

public class BuildingRequirementsService extends Service<Map<Path, Color>> {

    private final Set<GameObject> gameObjects;
    private final PathFinder      pathFinder;

    public BuildingRequirementsService(Set<GameObject> gameObjects, OnServiceDone<Map<Path, Color>> onServiceDone) {
        super(onServiceDone);

        this.gameObjects = gameObjects;
        this.pathFinder = new PathFinder(Scene.getInstance().getRoadGraph());
    }

    @Override
    public void setServiceManager(ServiceManager<?> serviceManager) {
        super.setServiceManager(serviceManager);
        this.pathFinder.setServiceManager(serviceManager);
    }

    @Override
    protected synchronized Map<Path, Color> execute() {
        Map<Path, Color> paths = new HashMap<>();
        try {
            List<TerrainPosition> allRoadsPositions = PathRenderer.getInstance().getTempPathsList().keySet()
                    .stream()
                    .map(Path::getAllUniqueRoads)
                    .flatMap(Collection::stream).map(Road::getPosition).collect(Collectors.toList());
            for (GameObject gameObject : this.gameObjects) {
//                TerrainPosition position = gameObject.getComponent(PositionComponent.class).getPosition()
//                        .toTerrainPosition();
//                if (allRoadsPositions.stream().anyMatch(roadPos -> roadPos.equals(position)))
//                    continue;
                Color color = new Color((int) (Math.random() * 0x1000000));
                if (!this.serviceManager.isServiceRunning()) {
                    System.err.println("Cancelling n°" + getId());
                    return null;
                }

                Set<Path> foundPaths = new TreeSet<>(Comparator.comparingInt(Path::getCost));
                boolean hasRoadConnections = gameObject.hasComponent(ConnectionsComponent.class) &&
                        gameObject.getComponent(ConnectionsComponent.class).getConnectionTypeClass() == Road.class;
                if (!hasRoadConnections)
                    continue;

                ConnectionsComponent<?> component = gameObject.getComponent(ConnectionsComponent.class);
                RequirementComponent requireComponent = gameObject.getComponent(RequirementComponent.class);
                if (component.isConnected()) {
                    Set<BuildingRoadConnectionRequirement> buildingRoadConnectionRequirements = requireComponent
                            .getRequirementsOfType(BuildingRoadConnectionRequirement.class);
                    for (BuildingRoadConnectionRequirement buildingRoadConnectionRequirement : buildingRoadConnectionRequirements) {
                        requireComponent.clearRequirement(buildingRoadConnectionRequirement);
                    }
                    for (BuildingRoadConnectionRequirement buildingRequirement : buildingRoadConnectionRequirements) {
                        if (!this.serviceManager.isServiceRunning()) {
                            System.err.println("Cancelling n°" + getId());
                            return null;
                        }

//                        Class<? extends GameObject> objectClass = buildingRequirement.getKey();
//
//                        Path path = this.pathFinder
//                                .findPath(gameObject, objectClass, buildingRequirement.getValue(), true);
                        if(buildingRequirement.isRequirementMet(gameObject, this.pathFinder)) {
                            foundPaths.add(buildingRequirement.getPath());
                            requireComponent.meetRequirement(buildingRequirement);
                        }
//                        if (!path.isEmpty()) {
//                            buildingRequirement.setPath(path);
//                        }
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
            this.serviceManager.setServiceRunning(false);
            System.err.println("Cancelling n°" + getId());

            return null;
        }
        return paths;
    }

}