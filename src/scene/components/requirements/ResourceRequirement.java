package scene.components.requirements;

import java.util.Map;
import resources.ResourceManager.Resource;
import resources.ResourceManager.Stock;

public class ResourceRequirement extends Requirement<Resource, Integer> {

    public ResourceRequirement(Resource resource, Integer amount,
            SetValueCallback<Integer> onSetValueCallback) {
        super(resource, amount, onSetValueCallback);
    }

    public ResourceRequirement(Resource resource, SetValueCallback<Integer> onSetValueCallback) {
        this(resource, 0, onSetValueCallback);
    }

    @Override
    public <X> boolean isRequirementMet(X... obj) {
        Map<Resource, Stock> resources = (Map<Resource, Stock>) obj[0];
        return resources.get(getKey()).getAmount() > getValue();
    }

}
