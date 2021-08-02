package entities;

import entities.Camera.Direction;
import models.AbstractModel;
import util.math.Maths;
import util.math.Matrix4f;
import util.math.Vector3f;

import java.util.Objects;

public class ModelEntity {

    protected AbstractModel model;
    protected Vector3f rotation;
    protected Vector3f position;
    protected float scale;

    private int textureIndex;
    private Matrix4f transformationMatrix;

    public ModelEntity(ModelEntity modelEntity) {
        this(modelEntity.position, modelEntity.rotation, modelEntity.scale, modelEntity.model, modelEntity.textureIndex);
    }

    private ModelEntity(Vector3f pos, Vector3f rotation, float scale, AbstractModel model, int textureIndex) {
        this.model = model;
        this.position = pos;
        this.rotation = rotation;
        this.scale = scale;
        this.textureIndex = textureIndex;
    }

    public ModelEntity(Vector3f pos, Vector3f rotation, float scale, AbstractModel model) {
        this(pos, rotation, scale, model, 0);
    }

    public ModelEntity(Vector3f pos, Direction direction, float scale, AbstractModel model) {
        this(pos, new Vector3f(0, direction.getDegree(), 0), scale, model, 0);
    }

    public ModelEntity(AbstractModel model, boolean fixedRotation) {
        this(model.toModelEntity());
    }

    public int getTextureIndex() {
        return this.textureIndex;
    }

    public void setTextureIndex(int textureIndex) {
        this.textureIndex = textureIndex;
    }

    public AbstractModel getModel() {
        return this.model;
    }

    public void setModel(AbstractModel model) {
        this.model = model;
    }

    public Vector3f getRotation() {
        return this.rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
        resetTransformationMatrix();
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
        resetTransformationMatrix();
    }

    public float getScale() {
        return this.scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
        resetTransformationMatrix();
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

    public Matrix4f getTransformationMatrix() {
        return this.transformationMatrix == null ? (this.transformationMatrix = Maths
                .createTransformationMatrix(this.position, this.rotation, this.scale)) : this.transformationMatrix;
    }

    public void resetTransformationMatrix() {
        this.transformationMatrix = null;
    }
}