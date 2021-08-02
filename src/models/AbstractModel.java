package models;

import entities.ModelEntity;
import renderEngine.Vao;
import util.math.Vector3f;
import util.parsing.Material;

import java.util.List;
import java.util.Objects;

public abstract class AbstractModel {

    protected Vao vao;

    protected AbstractModel() {
    }

    public AbstractModel(Vao vao) {
        this.vao = vao;
    }

    public final Vao getVao() {
        return this.vao;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AbstractModel that = (AbstractModel) o;
        return Objects.equals(this.vao, that.vao);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(this.vao);
    }

    public final ModelEntity toModelEntity() {
        return new ModelEntity(new Vector3f(), new Vector3f(), 1, this);
    }

    public abstract List<Material> getMaterials();
}
