package scene.components;

import entities.ModelEntity;
import models.AbstractModel;
import terrain.TerrainPosition;

public class PreviewComponent extends Component {

    private final ModelEntity previewTexture;

    private TerrainPosition previewPosition;

    public PreviewComponent(AbstractModel previewTexture) {
        this(previewTexture.toModelEntity());
    }

    public PreviewComponent(ModelEntity previewTexture) {
        this.previewTexture = previewTexture;
    }

    public ModelEntity getTexture() {
        return this.previewTexture;
    }

    public TerrainPosition getPreviewPosition() {
        return this.previewPosition;
    }

    public void setPreviewPosition(TerrainPosition previewPosition) {
//        System.out.println("setting preview position: " + previewPosition);
        this.previewPosition = previewPosition;
        update();
    }
}
