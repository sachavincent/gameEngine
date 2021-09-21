package renderEngine;

import entities.Entity;
import entities.ModelEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import models.AbstractModel;
import renderEngine.shaders.IGameObjectShader;
import renderEngine.shaders.ShaderProgram;
import scene.gameObjects.GameObject;

public abstract class GameObjectRenderer<Shader extends ShaderProgram> extends Renderer<Shader> {

    private final Map<AbstractModel, List<ModelEntity>> renderedModels;

    private final int id;

    private static int ID;

    protected GameObjectRenderer(Shader shader, LoadShaderCallback<Shader> loadShaderCallback) {
        super(shader, loadShaderCallback);

        this.id = ++ID;
        this.renderedModels = new HashMap<>();
    }

    protected GameObjectRenderer(Shader shader) {
        super(shader);

        this.id = ++ID;
        this.renderedModels = new HashMap<>();
    }

    @Override
    public final void render() {
        this.shader.start();
        doPreRender();

        doRender(this.renderedModels.entrySet());

        this.shader.stop();

        this.renderedModels.clear();
    }

    protected abstract void doPreRender();

    protected abstract void doRender(Set<Map.Entry<AbstractModel, List<ModelEntity>>> entrySet);

    public final void addToRender(Entity entity) {
        entity.getModelEntities().forEach(modelEntity -> {
            int gameObjectId = modelEntity.getGameObjectId();
            AbstractModel model = modelEntity.getModel();
            if (!this.renderedModels.containsKey(model))
                this.renderedModels.put(model, new ArrayList<>());
            this.renderedModels.get(model).add(modelEntity);
        });
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

    @Override
    public final int hashCode() {
        return Objects.hash(this.id);
    }

    protected void prepareInstance(ModelEntity modelEntity) {
        ((IGameObjectShader) this.shader).loadTransformationMatrix(modelEntity.getTransformationMatrix());
    }

    public void removeGameObject(GameObject gameObject) {
        int id = gameObject.getId();
        this.renderedModels.forEach((key, value) -> {
            value.removeIf(modelEntity -> modelEntity.getGameObjectId() == id);
        });
        this.renderedModels.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }
}