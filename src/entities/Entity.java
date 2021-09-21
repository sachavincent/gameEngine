package entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class Entity {

    private List<ModelEntity> modelEntities;

    public Entity(Entity entity) {
        this(entity.modelEntities);
    }

    public Entity(Collection<ModelEntity> modelEntities) {
        this.modelEntities = new ArrayList<>(modelEntities);
    }

    public Entity(ModelEntity modelEntity) {
        this(List.of(modelEntity));
    }

    public void setModel(ModelEntity modelEntity) {
        this.modelEntities = new ArrayList<>();
        this.modelEntities.add(modelEntity);
    }

    public void setModels(List<ModelEntity> modelEntities) {
        this.modelEntities = modelEntities;
    }

    public List<ModelEntity> getModelEntities() {
        return this.modelEntities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Entity entity = (Entity) o;
        return Objects.equals(entity.modelEntities, this.modelEntities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.modelEntities);
    }
}