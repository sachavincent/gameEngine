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
    public void place(Vector2f position) {
        if (position == null)
            return;

        Terrain terrain = Terrain.getInstance();

        Map<Vector2f, Item> items = terrain.getItems();

        if (items.get(position) != null)
            return;

        RoadItem itemInstance = (RoadItem) getItemInstance();
        boolean done = terrain.addItem(position, itemInstance.updateNeighboursAndCenter(terrain, position));
        terrain.updateRoadGraph();

        if (done)
            terrain.updateRequirements();
    }

    @Override
    public void place(Vector2f[] positions) {
        if (positions == null)
            return;

        Terrain terrain = Terrain.getInstance();

        Map<Vector2f, Item> items = terrain.getItems();

        RoadItem itemInstance = (RoadItem) getItemInstance();
        for (Vector2f position : positions) {
            if (items.get(position) != null)
                continue;

            terrain.addItem(position, itemInstance.updateNeighboursAndCenter(terrain, position));
        }
        terrain.updateRoadGraph();

        terrain.updateRequirements();
    }
}
