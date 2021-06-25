package entities;

import entities.Camera.Direction;
import java.util.Objects;
import models.TexturedModel;
import textures.ModelTexture;
import util.math.Vector3f;

public class Model {

    protected TexturedModel texturedModel;
    protected Vector3f      rotation;
    protected Vector3f      position;
    protected float         scale;

    private final boolean fixedRotation;

    private int textureIndex;

    public Model(Model model) {
        this(model.position, model.rotation, model.scale, model.texturedModel, model.textureIndex,
                model.fixedRotation);
    }

    public Model(TexturedModel model) {
        this(model, false);
    }

    public Model(TexturedModel model, boolean fixedRotation) {
        this(new Vector3f(), new Vector3f(), 0, model, 0, fixedRotation);
    }

    public Model(Vector3f pos, Vector3f rotation, float scale, TexturedModel model, int textureIndex,
            boolean fixedRotation) {
        this.texturedModel = model;
        this.position = pos;
        this.rotation = rotation;
        this.scale = scale;
        this.textureIndex = textureIndex;
        this.fixedRotation = fixedRotation;
    }

    public Model(Vector3f pos, Vector3f rotation, float scale, TexturedModel model) {
        this(pos, rotation, scale, model, 0, false);
    }

    public Model(Vector3f pos, Direction direction, float scale, TexturedModel model) {
        this(pos, new Vector3f(0, direction.getDegree(), 0), scale, model, 0, false);
    }

    public int getTextureIndex() {
        return this.textureIndex;
    }

    public void setTextureIndex(int textureIndex) {
        this.textureIndex = textureIndex;
    }

    public TexturedModel getTexturedModel() {
        return this.texturedModel;
    }

    public void setTexturedModel(TexturedModel texturedModel) {
        this.texturedModel = texturedModel;
    }

    public Vector3f getRotation() {
        return this.rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public float getScale() {
        return this.scale;
    }

    public boolean isFixedRotation() {
        return this.fixedRotation;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getTextureXOffset() {
        ModelTexture modelTexture = this.texturedModel.getModelTexture();
        if (modelTexture == null)
            return 0;

        int column = this.textureIndex % modelTexture.getNumberOfRows();
        return (float) column / (float) modelTexture.getNumberOfRows();
    }

    public float getTextureYOffset() {
        ModelTexture modelTexture = this.texturedModel.getModelTexture();
        if (modelTexture == null)
            return 0;

        int row = this.textureIndex / modelTexture.getNumberOfRows();
        return (float) row / (float) modelTexture.getNumberOfRows();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Model model1 = (Model) o;
        return Float.compare(model1.scale, scale) == 0 && texturedModel.equals(model1.texturedModel) &&
                rotation.equals(model1.rotation) && position.equals(model1.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(texturedModel, rotation, position, scale);
    }

}