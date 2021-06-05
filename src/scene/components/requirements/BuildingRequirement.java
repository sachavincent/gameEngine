package scene.components.requirements;

import pathfinding.Path;
import scene.gameObjects.GameObject;

public class BuildingRequirement extends Requirement<Class<? extends GameObject>, Integer> {

    protected Path path;

    public BuildingRequirement(Class<? extends GameObject> gameObjectClass, int maxLength,
            SetValueCallback<Integer> onSetValueCallback) {
        super(gameObjectClass, maxLength, onSetValueCallback);
    }

    public BuildingRequirement(Class<? extends GameObject> gameObjectClass,
            SetValueCallback<Integer> onSetValueCallback) {
        this(gameObjectClass, 0, onSetValueCallback);
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return this.path;
    }
}
