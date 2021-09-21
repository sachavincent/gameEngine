package scene.components;

import models.AbstractModel;
import scene.preview.PreviewedModelsSupplier;
import scene.preview.SinglePreviewsSupplier;
import terrain.TerrainPosition;

public class PreviewComponent extends Component {

    private final AbstractModel           previewModel;
    private final PreviewedModelsSupplier previewSupplier;

    private TerrainPosition previewPosition;

    public PreviewComponent(AbstractModel previewModel) {
        this(previewModel, SinglePreviewsSupplier.getInstance());
    }

    public PreviewComponent(AbstractModel previewModel, PreviewedModelsSupplier previewedModelsSupplier) {
        this.previewModel = previewModel;
        this.previewSupplier = previewedModelsSupplier;
    }

    public AbstractModel getModel() {
        return this.previewModel;
    }

    public TerrainPosition getPreviewPosition() {
        return this.previewPosition;
    }

    public void setPreviewPosition(TerrainPosition previewPosition) {
        this.previewPosition = previewPosition;
        update();
    }

    public PreviewedModelsSupplier getPreviewSupplier() {
        return this.previewSupplier;
    }
}