package models;

import renderEngine.Vao;
import renderEngine.Vbo;
import renderEngine.shaders.structs.Material;
import textures.ModelTexture;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimpleModel extends AbstractModel {

    public SimpleModel(Vao vao) {
        super(vao);
    }

    public SimpleModel(Vao vao, ModelTexture modelTexture) {
        this(vao);
        setModelTexture(modelTexture);
    }

    public void setModelTexture(ModelTexture modelTexture) {
        Map.Entry<Material, Vbo> vboEntry = this.vao.getIndexVbos().entrySet().stream().findFirst().orElse(null);
        Material material = Material.DEFAULT;
        Vbo vbo = null;
        if (vboEntry != null) {
            vbo = vboEntry.getValue();
            material = new Material(vboEntry.getKey().getName());
        }

        material.setDiffuseMap(modelTexture);
        this.vao.getIndexVbos().clear();
        this.vao.getIndexVbos().put(material, vbo);
    }

    @Override
    public List<Material> getMaterials() {
        return new ArrayList<>(this.vao.getIndexVbos().keySet());
    }
}