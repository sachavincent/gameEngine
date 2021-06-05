package renderEngine;


import entities.Camera.Direction;
import entities.Entity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import models.TexturedModel;
import renderEngine.shaders.ShaderProgram;
import scene.gameObjects.GameObject;
import util.math.Vector3f;

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

    protected void handleTexture(Map<TexturedModel, List<Entity>> entities, Vector3f pos, Direction dir,
            float scale, TexturedModel texture) {
        if (texture == null || texture.getModelTexture() == null || texture.getRawModel() == null)
            return;

        if (!entities.containsKey(texture))
            entities.put(texture, new ArrayList<>());

        entities.get(texture).add(new entities.Entity(texture, pos, 0, dir.getDegree(), 0, scale, 3));
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
