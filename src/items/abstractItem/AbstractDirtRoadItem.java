package items.abstractItem;

import items.Item;
import items.roads.DirtRoadItem;
import terrains.TerrainPosition;

public class AbstractDirtRoadItem extends AbstractRoadItem {

    private static AbstractDirtRoadItem instance;

    private AbstractDirtRoadItem() {
        super(ItemPreviews.DIRT_ROAD, new DirtRoadItem(new TerrainPosition(0, 0)));
    }

    public static AbstractDirtRoadItem getInstance() {
        return instance == null ? (instance = new AbstractDirtRoadItem()) : instance;
    }

    @Override
    public Item newInstance(TerrainPosition position) {
        return new DirtRoadItem(position);
    }
}
