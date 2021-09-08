package models;

import java.util.Collections;
import java.util.List;
import renderEngine.shaders.structs.Material;
import renderEngine.structures.Vao;
import textures.ModelTexture;

public class SimpleModel extends AbstractModel {

    private Material material;

    public SimpleModel(Vao vao) {
        super(vao);
    }

    public SimpleModel(Vao vao, ModelTexture modelTexture) {
        this(vao);

        setModelTexture(modelTexture);
    }

    public SimpleModel(Vao vao, Material material) {
        this(vao);

        this.material = material;
    }

    protected void setModelTexture(ModelTexture modelTexture) {
        this.material = new Material(modelTexture.toString());
        this.material.setDiffuseMap(modelTexture);
    }

    @Override
    public List<Material> getMaterials() {
        return Collections.singletonList(this.material);
    }
}