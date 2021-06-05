package scene.components;

import renderEngine.Renderer;
import scene.Scene;

public class RendererComponent extends Component {

    private final Renderer renderer;

    public RendererComponent(Renderer renderer) {
        super((gameObject, position) -> Scene.getInstance().addRenderableGameObject(renderer, gameObject));

        this.renderer = renderer;
    }

    public Renderer getRenderer() {
        return this.renderer;
    }
}