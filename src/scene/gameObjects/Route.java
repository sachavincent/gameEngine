package scene.gameObjects;

import models.TexturedModel;
import renderEngine.PathRenderer;
import scene.components.PositionComponent;
import scene.components.RendererComponent;
import scene.components.TextureComponent;

public class Route extends GameObject {

    public Route(TexturedModel texturedModel) {
        addComponent(new TextureComponent(texturedModel));

        addComponent(new RendererComponent(PathRenderer.getInstance()));
        addComponent(new PositionComponent());
    }
}