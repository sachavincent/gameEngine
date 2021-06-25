package scene.gameObjects;

import entities.Model;
import items.GameObjectPreviews;
import items.OBJGameObjects;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import pathfinding.NodeConnection;
import pathfinding.Road;
import renderEngine.BuildingRenderer;
import renderEngine.PathRenderer;
import scene.Scene;
import scene.components.*;
import scene.components.callbacks.AddComponentCallback;
import scene.components.requirements.BuildingRoadConnectionRequirement;
import scene.components.requirements.RequirementComponent;
import terrains.TerrainPosition;
import util.math.Vector3f;

public class DirtRoad extends GameObject {

    public DirtRoad() {
        addComponent(new IconComponent(GameObjectPreviews.DIRT_ROAD));
        addComponent(new ScaleComponent(0.5f));
        addComponent(new SingleModelComponent(new Model(OBJGameObjects.DIRT_ROAD.getTexture())));
//        addComponent(new BoundingBoxComponent(OBJGameObjects.DIRT_ROAD.getBoundingBox()));
        addComponent(new PreviewComponent(OBJGameObjects.DIRT_ROAD.getPreviewTexture()));
        addComponent(new ConnectionsComponent<>(Road.class, new AddComponentCallback() {
            @Override
            public void onAddComponent(GameObject gameObject, Vector3f position) {
                List<TerrainPosition> affectedRoads = Scene.getInstance().getRoadGraph().getNodeConnections()
                        .stream()
                        .map(NodeConnection::getRoads)
                        .filter(roads -> roads.stream().map(Road::getPosition).collect(
                                Collectors.toList()).contains(position.toTerrainPosition()))
                        .flatMap(Collection::stream)
                        .distinct()
                        .map(Road::getPosition)
                        .collect(Collectors.toList());

                Scene.getInstance().getGameObjectsForComponent(RequirementComponent.class, false).forEach(obj -> {
                    RequirementComponent requirementComponent = obj.getComponent(RequirementComponent.class);
                    Set<BuildingRoadConnectionRequirement> buildingRoadConnectionRequirements = requirementComponent
                            .getRequirementsOfType(BuildingRoadConnectionRequirement.class);
                    if (buildingRoadConnectionRequirements.isEmpty())
                        return;
                    AtomicBoolean connectionFound = new AtomicBoolean(false);

                    buildingRoadConnectionRequirements.stream()
                            .filter(buildingRoadConnectionRequirement -> buildingRoadConnectionRequirement.getPath() != null)
                            .filter(buildingRoadConnectionRequirement -> {
                                List<TerrainPosition> pathRoads = buildingRoadConnectionRequirement.getPath()
                                        .getAllUniqueRoads().stream()
                                        .map(Road::getPosition)
                                        .collect(Collectors.toList());
                                return pathRoads.stream().anyMatch(affectedRoads::contains);
                            }).forEach(buildingRoadConnectionRequirement -> {
                        PathRenderer.getInstance().removePath(buildingRoadConnectionRequirement.getPath());
                        Scene.getInstance().addBuildingRequirement(obj);
                        connectionFound.set(true);
                    });
                    if (!connectionFound.get() && buildingRoadConnectionRequirements.stream()
                            .anyMatch(buildingRoadConnectionRequirement -> !requirementComponent.getAllRequirements()
                                    .get(buildingRoadConnectionRequirement))) {
                        Scene.getInstance().addBuildingRequirement(obj);
                        connectionFound.set(true);
                    }
                });
            }

            @Override
            public boolean isForEach() {
                return false;
            }
        }));
        addComponent(new OffsetComponent(new Vector3f(.5f, 0, .5f)));

        addComponent(new RoadComponent());

        addComponent(new RendererComponent(BuildingRenderer.getInstance()));
    }
}