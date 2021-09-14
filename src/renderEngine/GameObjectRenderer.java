package renderEngine;

import entities.Entity;
import entities.ModelEntity;
import java.util.*;
import models.AbstractModel;
import renderEngine.shaders.IGameObjectShader;
import renderEngine.shaders.ShaderProgram;
import scene.Scene;
import scene.gameObjects.GameObject;

public abstract class GameObjectRenderer<Shader extends ShaderProgram> extends Renderer<Shader> {

    private final Map<AbstractModel, List<ModelEntity>> renderedModels;
    private final Set<Integer>                          renderedGameObjects;

    private final int     id;
    protected     boolean displayBoundingBoxes;

    private static int ID;

    protected GameObjectRenderer(Shader shader, LoadShaderCallback<Shader> loadShaderCallback) {
        super(shader, loadShaderCallback);

        this.id = ++ID;
        this.renderedGameObjects = new HashSet<>();
        this.renderedModels = new HashMap<>();
    }

    protected GameObjectRenderer(Shader shader) {
        super(shader);

        this.id = ++ID;
        this.renderedGameObjects = new HashSet<>();
        this.renderedModels = new HashMap<>();
    }

    @Override
    public final void render() {
        this.shader.start();

        doPreRender();

        doRender(this.renderedModels.entrySet());

        this.shader.stop();
    }

    protected abstract void doPreRender();

    protected abstract void doRender(Set<Map.Entry<AbstractModel, List<ModelEntity>>> entrySet);

    public final void addToRender(GameObject gameObject) {
        if (!this.renderedGameObjects.contains(gameObject.getId())) {
            this.renderedGameObjects.add(gameObject.getId());
            Entity entityFromGameObject = GameObject.createEntityFromGameObject(gameObject, this.displayBoundingBoxes);

            entityFromGameObject.getModelEntities().forEach(modelEntity -> {
                AbstractModel model = modelEntity.getModel();
                if (!this.renderedModels.containsKey(model))
                    this.renderedModels.put(model, new ArrayList<>());
                this.renderedModels.get(model).add(modelEntity);
            });
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        GameObjectRenderer<?> gameObjectRenderer = (GameObjectRenderer<?>) o;
        return this.id == gameObjectRenderer.id;
    }

    public final void switchDisplayBoundingBoxes() {
        this.displayBoundingBoxes = !this.displayBoundingBoxes;
        new ArrayList<>(this.renderedGameObjects).forEach(idObj -> {
            GameObject gameObjectFromId = Scene.getInstance().getGameObjectFromId(idObj);
            if (gameObjectFromId != null) {
                removeGameObject(gameObjectFromId);
                addToRender(gameObjectFromId);
            }
        });
    }

    @Override
    public final int hashCode() {
        return Objects.hash(this.id);
    }

    protected void prepareInstance(ModelEntity modelEntity) {
        ((IGameObjectShader) this.shader).loadTransformationMatrix(modelEntity.getTransformationMatrix());
    }

    public void removeGameObject(GameObject gameObject) {
        int id = gameObject.getId();
        this.renderedGameObjects.remove(id);
        this.renderedModels.forEach((key, value) -> {
            value.removeIf(modelEntity -> modelEntity.getGameObjectId() == id);
        });
        this.renderedModels.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }
}