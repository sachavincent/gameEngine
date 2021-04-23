package scene.gameObjects;

import items.GameObjectPreviews;
import items.OBJGameObjects;
import renderEngine.HouseRenderer;
import scene.components.*;

public class DirtRoad extends GameObject {

    public DirtRoad() {
        addComponent(new IconComponent(GameObjectPreviews.DIRT_ROAD));
        addComponent(new RoadComponent());
        addComponent(new ScaleComponent(0.5f));
        addComponent(new TextureComponent(OBJGameObjects.DIRT_ROAD.getTexture()));
        addComponent(new PreviewComponent(OBJGameObjects.DIRT_ROAD.getPreviewTexture()));
        addComponent(new RoadConnectionsComponent());

        addComponent(new RendererComponent(this, HouseRenderer.getInstance()));
    }
}