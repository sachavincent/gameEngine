package scene.components.requirements;

import resources.ResourceManager.Resource;

public class ResourceRequirement extends Requirement<Resource, Integer> {

    public ResourceRequirement(Resource resource, Integer amount,
            SetValueCallback<Integer> onSetValueCallback) {
        super(resource, amount, onSetValueCallback);
    }
    public ResourceRequirement(Resource resource, SetValueCallback<Integer> onSetValueCallback) {
        this(resource, 0, onSetValueCallback);
    }
}
