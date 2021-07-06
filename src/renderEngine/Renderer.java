package renderEngine;


import entities.Model;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import models.TexturedModel;
import renderEngine.shaders.ShaderProgram;
import scene.gameObjects.GameObject;

public abstract class Renderer {

    protected final Set<GameObject> gameObjects;
    private final   int             id;
    protected       boolean         displayBoundingBoxes;

    protected ShaderProgram shader;

    private static int ID;

    protected Renderer(ShaderProgram shader) {
        this();
        this.shader = shader;
    }

    protected Renderer() {
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
        Renderer renderer = (Renderer) o;
        return id == renderer.id;
    }

    public void switchDisplayBoundingBoxes() {
        this.displayBoundingBoxes = !this.displayBoundingBoxes;
    }

    protected void handleTexture(Map<TexturedModel, List<Model>> models, Model model) {
        if (model == null || model.getTexturedModel() == null || model.getTexturedModel().getModelTexture() == null ||
                model.getTexturedModel().getVao() == null)
            return;

        if (!models.containsKey(model.getTexturedModel()))
            models.put(model.getTexturedModel(), new ArrayList<>());

        models.get(model.getTexturedModel()).add(model);
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
}
