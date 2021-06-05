package scene.components.requirements;

import java.util.HashMap;
import java.util.Map;
import pathfinding.Path;
import scene.components.Component;
import scene.gameObjects.GameObject;

@Deprecated
public class RequireBuildingComponent extends Component {

    private final Map<Class<? extends GameObject>, Integer> requirements;

    protected Map<Class<? extends GameObject>, Path> requirementsPath = new HashMap<>();

    public RequireBuildingComponent(Map<Class<? extends GameObject>, Integer> requirements) {
        this.requirements = requirements;
    }

    public Map<Class<? extends GameObject>, Integer> getRequirements() {
        return this.requirements;
    }

    public void meetRequirement(Class<? extends GameObject> gameObjectClass, Path pathToItem) {
        if (this.requirements.containsKey(gameObjectClass))
            this.requirementsPath.put(gameObjectClass, pathToItem);
    }

    public boolean doesMeetRequirement(Class<? extends GameObject> gameObject) {
        return this.requirementsPath.containsKey(gameObject);
    }

    public Path getPathToItem(Class<? extends GameObject> gameObject) {
        return this.requirementsPath.get(gameObject);
    }

    public boolean doesMeetAllRequirements() {
        return this.requirements.equals(this.requirementsPath.keySet());
    }
}
