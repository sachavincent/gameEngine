package models;

import textures.ModelTexture;

public class TexturedModel {

    private RawModel rawModel;
    private ModelTexture modelTexture;

    public TexturedModel(RawModel rawModel, ModelTexture modelTexture) {
        this.rawModel = rawModel;
        this.modelTexture = modelTexture;
    }

    public RawModel getRawModel() {
        return this.rawModel;
    }

    public ModelTexture getModelTexture() {
        return this.modelTexture;
    }
}
