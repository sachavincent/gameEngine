package models;

import entities.ModelEntity;
import java.util.Objects;
import textures.ModelTexture;
import util.Vao;
import util.math.Vector3f;
import util.parsing.objParser.OBJFile;

public class Model {

    protected OBJFile modelFile;

    protected ModelTexture modelTexture;

    protected Vao vao;

    protected Model() {
    }

    public Model(Vao vao) {
        this.vao = vao;
    }

    public Model(Vao vao, OBJFile modelFile) {
        this(vao);
        this.modelFile = modelFile;
    }

    public Model(Vao vao, ModelTexture modelTexture) {
        this(vao);
        this.modelTexture = modelTexture;
    }

    public Vao getVao() {
        return this.vao;
    }

    public ModelTexture getModelTexture() {
        return this.modelTexture;
    }

    public void setModelTexture(ModelTexture modelTexture) {
        this.modelTexture = modelTexture;
    }

    public OBJFile getModelFile() {
        return this.modelFile;
    }

    public void setModelFile(OBJFile modelFile) {
        this.modelFile = modelFile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Model that = (Model) o;
        return Objects.equals(this.vao, that.vao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.vao);
    }

    public ModelEntity toModelEntity() {
        return new ModelEntity(new Vector3f(), new Vector3f(), 1, this);
    }
}
