package scene.gameObjects;

import entities.Model;
import models.TexturedModel;
import renderEngine.PathRenderer;
import scene.components.PositionComponent;
import scene.components.RendererComponent;
import scene.components.SingleModelComponent;

public class Route extends GameObject {

    public Route(TexturedModel texturedModel) {
        addComponent(new SingleModelComponent(new Model(texturedModel)));

        addComponent(new RendererComponent(PathRenderer.getInstance()));
        addComponent(new PositionComponent());
    }
}