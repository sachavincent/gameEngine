package scene.gameObjects;

import pathfinding.NodeConnection;
import pathfinding.Road;
import renderEngine.BuildingRenderer;
import renderEngine.PathRenderer;
import scene.Scene;
import scene.components.*;
import scene.components.callbacks.AddComponentCallback;
import scene.components.requirements.BuildingRoadConnectionRequirement;
import scene.components.requirements.ResourceRequirementComponent;
import terrain.TerrainPosition;
import util.math.Vector3f;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class DirtRoad extends GameObject {

    public DirtRoad() {
        addComponent(new IconComponent(GameObjectPreviews.DIRT_ROAD));
        addComponent(new ScaleComponent(0.5f));
        addComponent(new SingleModelComponent(GameObjectDatas.DIRT_ROAD.getTexture()));
//        addComponent(new BoundingBoxComponent(OBJGameObjects.DIRT_ROAD.getBoundingBox()));
        addComponent(new PreviewComponent(GameObjectDatas.DIRT_ROAD.getPreviewTexture()));
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

                Scene.getInstance().getGameObjectsForComponent(ResourceRequirementComponent.class, false).forEach(obj -> {
                    ResourceRequirementComponent resourceRequirementComponent = obj.getComponent(
                            ResourceRequirementComponent.class);
                    Set<BuildingRoadConnectionRequirement> buildingRoadConnectionRequirements = resourceRequirementComponent
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
                            .anyMatch(buildingRoadConnectionRequirement -> !resourceRequirementComponent.getAllRequirements()
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