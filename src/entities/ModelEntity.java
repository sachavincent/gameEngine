package entities;

import entities.Camera.Direction;
import java.util.Objects;
import models.AbstractModel;
import util.math.Maths;
import util.math.Matrix4f;
import util.math.Vector3f;

public class ModelEntity {

    private   int           gameObjectId;
    protected AbstractModel model;
    protected Vector3f      rotation;
    protected Vector3f      position;
    protected float         scale;
    protected float         transparency;

    private Matrix4f transformationMatrix;

    public ModelEntity(ModelEntity modelEntity) {
        this(modelEntity.position, modelEntity.rotation, modelEntity.scale,
                modelEntity.model, modelEntity.transparency, modelEntity.gameObjectId);
    }

    private ModelEntity(Vector3f pos, Vector3f rotation, float scale,
            AbstractModel model, float transparency, int gameObjectId) {
        this.model = model;
        this.position = pos;
        this.rotation = rotation;
        this.scale = scale;
        this.transparency = transparency;
        this.gameObjectId = gameObjectId;
    }

    public ModelEntity(Vector3f pos, Vector3f rotation, float scale, AbstractModel model, int gameObjectId) {
        this(pos, rotation, scale, model, 1, gameObjectId);
    }

    public ModelEntity(Vector3f pos, Direction direction, float scale, AbstractModel model, int gameObjectId) {
        this(pos, new Vector3f(0, direction.getDegree(), 0), scale, model, 1, gameObjectId);
    }

    public ModelEntity(AbstractModel model, boolean fixedRotation) {
        this(model.toModelEntity());
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

    public float getTransparency() {
        return this.transparency;
    }

    public void setTransparency(float transparency) {
        this.transparency = transparency;
    }

    public int getGameObjectId() {
        return this.gameObjectId;
    }

    public void setGameObjectId(int gameObjectId) {
        this.gameObjectId = gameObjectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ModelEntity modelEntity = (ModelEntity) o;
        return Float.compare(modelEntity.scale, this.scale) == 0 && this.model.equals(modelEntity.model) &&
                this.rotation.equals(modelEntity.rotation) && this.position.equals(modelEntity.position) &&
                Float.compare(modelEntity.transparency, this.transparency) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.model, this.rotation, this.position, this.scale, this.transparency);
    }

    public Matrix4f getTransformationMatrix() {
        return this.transformationMatrix == null ? (this.transformationMatrix = Maths
                .createTransformationMatrix(this.position, this.rotation, this.scale)) : this.transformationMatrix;
    }

    public void resetTransformationMatrix() {
        this.transformationMatrix = null;
    }
}