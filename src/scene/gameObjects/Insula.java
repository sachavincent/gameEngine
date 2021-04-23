package scene.gameObjects;

import guis.prefabs.GuiHouseDetails.GuiHouseDetails;
import items.GameObjectPreviews;
import items.OBJGameObjects;
import java.util.Map;
import people.Person;
import renderEngine.HouseRenderer;
import scene.components.*;

public class Insula extends GameObject {

    private final static int MAX_PEOPLE_CAPACITY = 10;

    private final static int X_POSITIVE_OFFSET = 3;
    private final static int X_NEGATIVE_OFFSET = 2;
    private final static int Z_POSITIVE_OFFSET = 2;
    private final static int Z_NEGATIVE_OFFSET = 2;

    public Insula() {
        addComponent(new IconComponent(GameObjectPreviews.INSULA));
        addComponent(new DirectionComponent());
        addComponent(new RequireBuildingComponent(Map.of(Market.class, 0)));
        addComponent(new TextureComponent(OBJGameObjects.INSULA.getTexture()));
        addComponent(new PreviewComponent(OBJGameObjects.INSULA.getPreviewTexture()));
        addComponent(new OffsetsComponent(Z_POSITIVE_OFFSET, X_POSITIVE_OFFSET, Z_NEGATIVE_OFFSET, X_NEGATIVE_OFFSET));

        addComponent(new RoadConnectionsComponent());

        FrequentedPlaceComponent frequentedPlaceComponent = new FrequentedPlaceComponent(MAX_PEOPLE_CAPACITY);
        addComponent(frequentedPlaceComponent);

        addComponent(new RequireResourcesComponent(Person.getResourcesNeeded(frequentedPlaceComponent.getPersons())));
        addComponent(new BoundingBoxComponent(OBJGameObjects.INSULA.getBoundingBox()));
        addComponent(new SelectableComponent(() -> {
            GuiHouseDetails.getInstance().setHouseObject(this);
            GuiHouseDetails.getInstance().setDisplayed(true);
        }));
        addComponent(new RendererComponent(this, HouseRenderer.getInstance()));
    }
}