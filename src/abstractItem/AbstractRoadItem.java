package abstractItem;

import guis.presets.GuiBackground;
import items.Item;
import items.roads.RoadItem;
import java.util.Map;
import terrains.Terrain;
import util.math.Vector2f;

public abstract class AbstractRoadItem extends AbstractItem {

    public AbstractRoadItem(GuiBackground<?> modelTexture, Item item) {
        super(modelTexture, item);
    }

    @Override
    public void place(Terrain terrain, Vector2f position) {
        Map<Vector2f, Item> items = terrain.getItems();

//        Item item = items.getOrDefault(position, null);
//
//        if (item != null) {
//            terrain.removeItem(position);
//
//            ((RoadItem) getItem()).updateNeighbours(terrain, position);
//
//            return;
//        }
        if (items.get(position) != null)
            return;

        RoadItem itemInstance = (RoadItem) getItemInstance();
        items.put(position, itemInstance.updateNeighboursAndCenter(terrain, position));
    }
}
