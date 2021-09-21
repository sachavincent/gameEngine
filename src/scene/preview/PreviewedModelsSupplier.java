package scene.preview;

import entities.Camera.Direction;
import entities.ModelEntity;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import terrain.TerrainPosition;
import util.Offset;

public abstract class PreviewedModelsSupplier {

    protected final Map<ModelEntity, TerrainPosition> models = new HashMap<>();

    public abstract void offset(Offset offset);

    public abstract void add(ModelEntity modelEntity);

    public final void clear() {
        this.models.clear();
    }

    public final Set<ModelEntity> get() {
        return Collections.unmodifiableSet(this.models.keySet());
    }

    public final Set<TerrainPosition> getPositions() {
        return this.models.values().stream().collect(Collectors.toUnmodifiableSet());
    }

    public final boolean isAny() {
        return !this.models.isEmpty();
    }

    public abstract Direction getDirection();
}