package entities;

import entities.Camera.Direction;
import java.util.Objects;
import models.Model;
import util.math.Vector3f;

public class ModelEntity {

    protected Model    model;
    protected Vector3f rotation;
    protected Vector3f position;
    protected float    scale;

    private boolean fixedRotation;

    private int textureIndex;

    public ModelEntity(ModelEntity modelEntity) {
        this(modelEntity.position, modelEntity.rotation, modelEntity.scale, modelEntity.model, modelEntity.textureIndex,
                modelEntity.fixedRotation);
    }

    private ModelEntity(Vector3f pos, Vector3f rotation, float scale, Model model, int textureIndex,
            boolean fixedRotation) {
        this.model = model;
        this.position = pos;
        this.rotation = rotation;
        this.scale = scale;
        this.textureIndex = textureIndex;
        this.fixedRotation = fixedRotation;
    }

    public ModelEntity(Vector3f pos, Vector3f rotation, float scale, Model model) {
        this(pos, rotation, scale, model, 0, false);
    }

    public ModelEntity(Vector3f pos, Direction direction, float scale, Model model) {
        this(pos, new Vector3f(0, direction.getDegree(), 0), scale, model, 0, false);
    }

    public ModelEntity(Model model, boolean fixedRotation) {
        this(model.toModelEntity());
        this.fixedRotation = fixedRotation;
    }

    public int getTextureIndex() {
        return this.textureIndex;
    }

    public void setTextureIndex(int textureIndex) {
        this.textureIndex = textureIndex;
    }

    public Model getModel() {
        return this.model;
    }

    public void setModel(Model model) {
        this.model = model;
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

//    public float getTextureXOffset() {
//        ModelTexture modelTexture = this.model.getModelTexture();
//        if (modelTexture == null)
//            return 0;
//
//        int column = this.textureIndex % modelTexture.getNumberOfRows();
//        return (float) column / (float) modelTexture.getNumberOfRows();
//    }
//
//    public float getTextureYOffset() {
//        ModelTexture modelTexture = this.texturedModel.getModelTexture();
//        if (modelTexture == null)
//            return 0;
//
//        int row = this.textureIndex / modelTexture.getNumberOfRows();
//        return (float) row / (float) modelTexture.getNumberOfRows();
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ModelEntity modelEntity = (ModelEntity) o;
        return Float.compare(modelEntity.scale, this.scale) == 0 && this.model.equals(modelEntity.model) &&
                this.rotation.equals(modelEntity.rotation) && this.position.equals(modelEntity.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.model, this.rotation, this.position, this.scale);
    }

}