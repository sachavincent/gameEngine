package scene.components;

import java.util.HashMap;
import java.util.Map;
import resources.ResourceManager.Resource;

public class RequireResourcesComponent implements Component {

    private final Map<Resource, Integer> resourcesNeeded;

    private final Map<Resource, Integer> currentResources;

    public RequireResourcesComponent(Map<Resource, Integer> resourcesNeeded) {
        this.resourcesNeeded = resourcesNeeded;
        this.currentResources = new HashMap<>();
    }

    public Map<Resource, Integer> getResourcesNeeded() {
        return this.resourcesNeeded;
    }

    public Map<Resource, Integer> getCurrentResources() {
        return this.currentResources;
    }
}
