package scene.components.requirements;

import pathfinding.Path;
import pathfinding.PathFinder;
import scene.gameObjects.GameObject;

public class BuildingRoadConnectionRequirement extends Requirement<Class<? extends GameObject>, Integer> {

    protected Path path;

    public BuildingRoadConnectionRequirement(Class<? extends GameObject> gameObjectClass, int maxLength,
            SetValueCallback<Integer> onSetValueCallback) {
        super(gameObjectClass, maxLength, onSetValueCallback);
    }

    public BuildingRoadConnectionRequirement(Class<? extends GameObject> gameObjectClass,
            SetValueCallback<Integer> onSetValueCallback) {
        this(gameObjectClass, 0, onSetValueCallback);
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return this.path;
    }

    @Override
    public <X> boolean isRequirementMet(X... object) {
        GameObject gameObject = (GameObject) object[0];
        PathFinder pathFinder = (PathFinder) object[1];
        Class<? extends GameObject> objectClass = getKey();

        Path path = pathFinder.findPath(gameObject, objectClass, getValue(), true);
        if (!path.isEmpty()) {
            setPath(path);
            return true;
        }
        return false;
    }
}