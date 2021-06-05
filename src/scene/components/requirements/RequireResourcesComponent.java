package scene.components.requirements;

import java.util.HashMap;
import java.util.Map;
import resources.ResourceManager.Resource;
import scene.components.Component;

@Deprecated
public class RequireResourcesComponent extends Component {

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
