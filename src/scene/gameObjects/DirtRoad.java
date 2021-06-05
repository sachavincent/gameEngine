package scene.gameObjects;

import items.GameObjectPreviews;
import items.OBJGameObjects;
import renderEngine.BuildingRenderer;
import scene.components.*;
import util.math.Vector3f;

public class DirtRoad extends GameObject {

    public DirtRoad() {
        addComponent(new IconComponent(GameObjectPreviews.DIRT_ROAD));
        addComponent(new RoadComponent());
        addComponent(new ScaleComponent(0.5f));
        addComponent(new TextureComponent(OBJGameObjects.DIRT_ROAD.getTexture()));
        addComponent(new PreviewComponent(OBJGameObjects.DIRT_ROAD.getPreviewTexture()));
        addComponent(new RoadConnectionsComponent());
        addComponent(new OffsetComponent(new Vector3f(.5f, 0, .5f)));

        addComponent(new RendererComponent(BuildingRenderer.getInstance()));
    }
}