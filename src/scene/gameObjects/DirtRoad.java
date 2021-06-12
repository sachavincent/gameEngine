package scene.gameObjects;

import items.GameObjectPreviews;
import items.OBJGameObjects;
import renderEngine.BuildingRenderer;
import scene.Scene;
import scene.components.*;
import scene.components.callbacks.AddComponentCallback;
import util.math.Vector3f;

public class DirtRoad extends GameObject {

    public DirtRoad() {
        addComponent(new IconComponent(GameObjectPreviews.DIRT_ROAD));
        addComponent(new RoadComponent());
        addComponent(new ScaleComponent(0.5f));
        addComponent(new TextureComponent(OBJGameObjects.DIRT_ROAD.getTexture()));
        addComponent(new PreviewComponent(OBJGameObjects.DIRT_ROAD.getPreviewTexture()));
        addComponent(new RoadConnectionsComponent(new AddComponentCallback() {
            @Override
            public void onAddComponent(GameObject gameObject, Vector3f position) {
                Scene.getInstance().updateBuildingRequirements();
            }

            @Override
            public boolean isForEach() {
                return false;
            }
        }));
        addComponent(new OffsetComponent(new Vector3f(.5f, 0, .5f)));

        addComponent(new RendererComponent(BuildingRenderer.getInstance()));
    }
}