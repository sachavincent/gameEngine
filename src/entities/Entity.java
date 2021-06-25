package entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class Entity {

    private List<Model> models;
    private boolean     preview;

    public Entity(Entity entity) {
        this(entity.models);
    }

    public Entity(Collection<Model> models) {
        this.models = new ArrayList<>(models);
    }

    public Entity(Model model) {
        this(List.of(model));
    }

    public boolean isPreview() {
        return this.preview;
    }

    public void setPreview(boolean preview) {
        this.preview = preview;
    }

    public void setModel(Model model) {
        this.models = new ArrayList<>();
        this.models.add(model);
    }

    public void setModels(List<Model> models) {
        this.models = models;
    }

    public List<Model> getModels() {
        return this.models;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Entity entity = (Entity) o;
        return                 Objects.equals(entity.models, this.models);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.models);
    }
}