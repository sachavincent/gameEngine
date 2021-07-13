package scene.components;

import entities.ModelEntity;
import models.AnimatedModel;
import models.Model;

public class AnimatedModelComponent extends SingleModelComponent {

    public AnimatedModelComponent(ModelEntity modelEntity) {
        super(modelEntity);

        setOnFrameRenderedCallback((gameObject, nbFrames) -> {
            Model model = modelEntity.getModel();
            ((AnimatedModel) model).update();
            return true;
        });
    }
}
