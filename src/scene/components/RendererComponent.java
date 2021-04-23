package scene.components;

import scene.gameObjects.GameObject;
import renderEngine.Renderer;
import scene.Scene;

public class RendererComponent implements Component {

    private final Renderer renderer;

    public RendererComponent(GameObject gameObject, Renderer renderer) {
        this.renderer = renderer;

        Scene scene = Scene.getInstance();
        scene.addRenderableGameObject(this.renderer, gameObject);
    }

    public Renderer getRenderer() {
        return this.renderer;
    }

    @Override
    public void removeObject(GameObject gameObject) {
        Scene scene = Scene.getInstance();
        scene.removeRenderableGameObject(this.renderer, gameObject);
    }
}