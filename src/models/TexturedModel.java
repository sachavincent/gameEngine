package models;

import java.util.Objects;
import textures.ModelTexture;
import util.Vao;

public class TexturedModel {

    protected ModelTexture modelTexture;

    protected Vao vao;

    public TexturedModel(Vao vao, ModelTexture modelTexture) {
        this.vao = vao;
        this.modelTexture = modelTexture;
    }


    public TexturedModel(Vao vao) {
        this(vao, ModelTexture.DEFAULT_MODEL);
    }

    public TexturedModel() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TexturedModel that = (TexturedModel) o;
        return Objects.equals(this.vao, that.vao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.vao);
    }
}
