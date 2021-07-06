package scene.components;

import entities.Model;
import models.AnimatedTexturedModel;
import models.TexturedModel;

public class AnimatedModelComponent extends SingleModelComponent {

    public AnimatedModelComponent(Model model) {
        super(model);

        setOnFrameRenderedCallback((gameObject, nbFrames) -> {
            TexturedModel texturedModel = model.getTexturedModel();
            ((AnimatedTexturedModel) texturedModel).update();
            return true;
        });
    }
}
