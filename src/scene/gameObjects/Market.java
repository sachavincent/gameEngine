package scene.gameObjects;

import guis.prefabs.GuiHouseDetails.GuiHouseDetails;
import items.GameObjectPreviews;
import items.OBJGameObjects;
import renderEngine.HouseRenderer;
import scene.components.*;

public class Market extends GameObject {

    private final static int X_POSITIVE_OFFSET = 4;
    private final static int X_NEGATIVE_OFFSET = 4;
    private final static int Z_POSITIVE_OFFSET = 4;
    private final static int Z_NEGATIVE_OFFSET = 4;

    public Market() {
        addComponent(new IconComponent(GameObjectPreviews.MARKET));
        addComponent(new DirectionComponent());
        addComponent(new TextureComponent(OBJGameObjects.MARKET.getTexture()));
        addComponent(new PreviewComponent(OBJGameObjects.MARKET.getPreviewTexture()));
        addComponent(new OffsetsComponent(Z_POSITIVE_OFFSET, X_POSITIVE_OFFSET, Z_NEGATIVE_OFFSET, X_NEGATIVE_OFFSET));
        addComponent(new RoadConnectionsComponent());

        FrequentedPlaceComponent frequentedPlaceComponent = new FrequentedPlaceComponent(10);
        addComponent(frequentedPlaceComponent);

        addComponent(new BoundingBoxComponent(OBJGameObjects.MARKET.getBoundingBox()));
        addComponent(new SelectableComponent(() -> {
            GuiHouseDetails.getInstance().setHouseObject(this);
            GuiHouseDetails.getInstance().setDisplayed(true);
        }));
        addComponent(new RendererComponent(this, HouseRenderer.getInstance()));
    }


}