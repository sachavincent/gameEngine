package scene.preview;

import entities.Camera.Direction;
import entities.ModelEntity;
import util.Offset;
import util.math.Vector3f;

public class SinglePreviewsSupplier extends PreviewedModelsSupplier {

    private Direction direction;

    private static SinglePreviewsSupplier instance;

    public static SinglePreviewsSupplier getInstance() {
        return instance == null ? (instance = new SinglePreviewsSupplier()) : instance;
    }

    private SinglePreviewsSupplier() {
        this.direction = Direction.defaultDirection();
    }

    @Override
    public final void offset(Offset offset) {
        if (!isAny())
            return;

        ModelEntity modelEntity = get().stream().findFirst().orElse(null);
        modelEntity.setPosition(
                Vector3f.add(modelEntity.getPosition(), offset.getOffsetPosition(), null));
        modelEntity.setScale(modelEntity.getScale() + offset.getOffsetScale());
        this.direction = this.direction.add(offset.getOffsetRotation());
        modelEntity.setRotation(new Vector3f(0, this.direction.getDegree(), 0));
        add(modelEntity);
    }

    public final void add(ModelEntity modelEntity) {
        if (isAny())
            clear();
        modelEntity.setRotation(new Vector3f(0, this.direction.getDegree(), 0));
        this.models.put(modelEntity, modelEntity.getPosition().toTerrainPosition());
    }

    @Override
    public final Direction getDirection() {
        return this.direction;
    }
}
