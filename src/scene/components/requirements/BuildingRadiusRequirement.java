package scene.components.requirements;

import java.util.Comparator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import scene.Scene;
import scene.components.PositionComponent;
import scene.components.PreviewComponent;
import scene.components.TerrainComponent;
import scene.gameObjects.GameObject;
import util.math.Vector3f;

public class BuildingRadiusRequirement extends Requirement<Class<? extends GameObject>, Integer> {

    private GameObject building;

    public BuildingRadiusRequirement(Class<? extends GameObject> gameObjectClass, int radius,
            SetValueCallback<Integer> onSetValueCallback) {
        super(gameObjectClass, radius, onSetValueCallback);
    }

    @Override
    public <X> boolean isRequirementMet(X... obj) {
        GameObject gameObject = (GameObject) obj[0];

        Set<GameObject> gameObjects = Scene.getInstance().getGameObjectsOfType(getKey(), false);
        if (gameObjects.isEmpty() || gameObject == null)
            return false;

        Vector3f position = null;
        if (gameObject.hasComponent(PreviewComponent.class))
            position = gameObject.getComponent(PreviewComponent.class).getPreviewPosition();
        if (position == null && gameObject.hasComponent(PositionComponent.class))
            position = gameObject.getComponent(PositionComponent.class).getPosition();

        if (position == null)
            return false;

        Scene.getInstance().getGameObjectsForComponent(TerrainComponent.class, false)
                .forEach(gObj -> {
                    TerrainComponent component = gObj.getComponent(TerrainComponent.class);
                    component.getFocusPoints().clear();
                    gameObjects.forEach(building -> {
                        component.addFocusPoint(
                                building.getComponent(PositionComponent.class).getPosition().toTerrainPosition(), 15);
                    });
                });

        Vector3f finalPosition = position;
        this.building = gameObjects.stream()
                .collect(Collectors.toMap(gObj -> gObj,
                        gObj -> gObj.getComponent(PositionComponent.class).getPosition().distance(finalPosition)))
                .entrySet().stream()
                .filter(entry -> entry.getValue() <= getValue())
                .sorted(Comparator.comparingDouble(entry -> {
                    double distance = entry.getValue();
                    return distance > getValue() ? Double.MAX_VALUE : distance;
                })).map(Entry::getKey).findFirst().orElse(null);

        return this.building != null;
    }

    public GameObject getBuilding() {
        return this.building;
    }
}
