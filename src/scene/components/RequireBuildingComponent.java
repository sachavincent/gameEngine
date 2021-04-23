package scene.components;

import java.util.HashMap;
import java.util.Map;
import pathfinding.RouteFinder.Route;
import scene.gameObjects.GameObject;

public class RequireBuildingComponent implements Component {

    private final Map<Class<? extends GameObject>, Integer> requirements;

    protected Map<Class<? extends GameObject>, Route> requirementsRoute = new HashMap<>();

    public RequireBuildingComponent(Map<Class<? extends GameObject>, Integer> requirements) {
        this.requirements = requirements;
    }

    public Map<Class<? extends GameObject>, Integer> getRequirements() {
        return this.requirements;
    }

    public void meetRequirement(Class<? extends GameObject> gameObjectClass, Route routeToItem) {
        if (this.requirements.containsKey(gameObjectClass))
            this.requirementsRoute.put(gameObjectClass, routeToItem);
    }

    public boolean doesMeetRequirement(Class<? extends GameObject> gameObject) {
        return this.requirementsRoute.containsKey(gameObject);
    }

    public Route getRouteToItem(Class<? extends GameObject> gameObject) {
        return this.requirementsRoute.get(gameObject);
    }

    public boolean doesMeetAllRequirements() {
        return this.requirements.equals(this.requirementsRoute.keySet());
    }
}
