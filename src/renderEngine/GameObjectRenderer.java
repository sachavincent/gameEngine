package renderEngine;


import entities.ModelEntity;
import models.AbstractModel;
import renderEngine.shaders.IGameObjectShader;
import renderEngine.shaders.ShaderProgram;
import scene.gameObjects.GameObject;

import java.util.*;

public abstract class GameObjectRenderer {

    protected final Set<GameObject> gameObjects;
    private final int id;
    protected boolean displayBoundingBoxes;

    protected ShaderProgram shader;

    private static int ID;

    protected GameObjectRenderer(ShaderProgram shader) {
        this();
        this.shader = shader;
    }

    protected GameObjectRenderer() {
        this.id = ++ID;
        this.gameObjects = new HashSet<>();
    }

    public Set<GameObject> getGameObjects() {
        return this.gameObjects;
    }

    public ShaderProgram getShader() {
        return this.shader;
    }

    public abstract void render();

    public abstract void prepareRender(GameObject gameObject);

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        GameObjectRenderer gameObjectRenderer = (GameObjectRenderer) o;
        return id == gameObjectRenderer.id;
    }

    public void switchDisplayBoundingBoxes() {
        this.displayBoundingBoxes = !this.displayBoundingBoxes;
    }

    protected void handleTexture(Map<AbstractModel, List<ModelEntity>> models, ModelEntity modelEntity) {
        if (modelEntity == null || modelEntity.getModel() == null || modelEntity.getModel().getVao() == null)
            return;

        if (!models.containsKey(modelEntity.getModel()))
            models.put(modelEntity.getModel(), new ArrayList<>());

        models.get(modelEntity.getModel()).add(modelEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    protected void cleanUp() {
        this.shader.cleanUp();
    }

    public void doZPass(Set<GameObject> gameObjects) {
    }

    protected void prepareInstance(ModelEntity modelEntity) {
        ((IGameObjectShader) this.shader).loadTransformationMatrix(modelEntity.getTransformationMatrix());
    }
}
