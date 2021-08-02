package scene.components;

import entities.ModelEntity;
import models.AbstractModel;
import models.AnimatedModel;
import models.Model;

public class AnimatedModelComponent extends SingleModelComponent {

    public AnimatedModelComponent(AbstractModel model) {
        this(model.toModelEntity());
    }

    public AnimatedModelComponent(ModelEntity modelEntity) {
        super(modelEntity);

        setOnFrameRenderedCallback((gameObject, nbFrames) -> {
            AnimatedModel model = (AnimatedModel) modelEntity.getModel();
            model.update();
            return true;
        });
    }
}
