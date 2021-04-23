package renderEngine;


import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import renderEngine.shaders.ShaderProgram;
import scene.gameObjects.GameObject;

public abstract class Renderer {

    protected final Set<GameObject> gameObjects;
    private final   int             id;

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

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
