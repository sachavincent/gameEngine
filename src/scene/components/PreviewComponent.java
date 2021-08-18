package scene.components;

import entities.ModelEntity;
import models.AbstractModel;
import util.math.Vector3f;

public class PreviewComponent extends Component {

    private final ModelEntity previewTexture;

    private Vector3f previewPosition;

    public PreviewComponent(AbstractModel previewTexture) {
        this(previewTexture.toModelEntity());
    }

    public PreviewComponent(ModelEntity previewTexture) {
        this.previewTexture = previewTexture;
    }

    public ModelEntity getTexture() {
        return this.previewTexture;
    }

    public Vector3f getPreviewPosition() {
        return this.previewPosition;
    }

    public void setPreviewPosition(Vector3f previewPosition) {
//        System.out.println("setting preview position: " + previewPosition);
        this.previewPosition = previewPosition;
        update();
    }
}
