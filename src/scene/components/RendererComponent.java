package scene.components;

import engineTester.Rome;
import renderEngine.GameObjectRenderer;

public class RendererComponent extends Component {

    private final GameObjectRenderer<?> gameObjectRenderer;

    public RendererComponent(GameObjectRenderer<?> gameObjectRenderer) {
        super((gameObject) -> Rome.getGame().getScene().addRenderableGameObject(gameObjectRenderer, gameObject, false));

        this.gameObjectRenderer = gameObjectRenderer;
    }

    public GameObjectRenderer<?> getRenderer() {
        return this.gameObjectRenderer;
    }
}