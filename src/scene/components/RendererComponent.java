package scene.components;

import renderEngine.GameObjectRenderer;
import scene.Scene;

public class RendererComponent extends Component {

    private final GameObjectRenderer<?> gameObjectRenderer;

    public RendererComponent(GameObjectRenderer<?> gameObjectRenderer) {
        super((gameObject, position) -> Scene.getInstance().addRenderableGameObject(gameObjectRenderer, gameObject));

        this.gameObjectRenderer = gameObjectRenderer;
    }

    public GameObjectRenderer<?> getRenderer() {
        return this.gameObjectRenderer;
    }
}