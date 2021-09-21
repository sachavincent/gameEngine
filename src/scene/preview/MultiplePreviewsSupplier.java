package scene.preview;

import entities.Camera.Direction;
import entities.ModelEntity;
import util.Offset;

public class MultiplePreviewsSupplier extends PreviewedModelsSupplier {

    private static MultiplePreviewsSupplier instance;

    public static MultiplePreviewsSupplier getInstance() {
        return instance == null ? (instance = new MultiplePreviewsSupplier()) : instance;
    }

    private MultiplePreviewsSupplier() {

    }

    @Override
    public void offset(Offset offset) {

    }

    @Override
    public final void add(ModelEntity modelEntity) {
        this.models.put(modelEntity, modelEntity.getPosition().toTerrainPosition());
    }

    @Override
    public final Direction getDirection() {
        return Direction.defaultDirection();
    }
}