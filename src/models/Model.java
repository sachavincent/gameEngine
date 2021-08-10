package models;

import renderEngine.Vao;
import renderEngine.shaders.structs.Material;
import util.parsing.objParser.OBJFile;

import java.util.List;

public class Model extends AbstractModel {

    protected OBJFile modelFile;

    public Model(Vao vao) {
        super(vao);
    }

    public Model(Vao vao, OBJFile modelFile) {
        this(vao);
        this.modelFile = modelFile;
    }

    public OBJFile getModelFile() {
        return this.modelFile;
    }

    public void setModelFile(OBJFile modelFile) {
        this.modelFile = modelFile;
    }

    @Override
    public List<Material> getMaterials() {
        return this.modelFile.getMTLFile().getMaterials();
    }
}
