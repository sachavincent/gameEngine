package abstractItem;

import items.Item;
import items.roads.DirtRoadItem;
import terrains.TerrainPosition;

public class AbstractDirtRoadItem extends AbstractRoadItem {

    public AbstractDirtRoadItem() {
        super(ItemPreviews.DIRT_ROAD, new DirtRoadItem(new TerrainPosition(0, 0)));
    }

    @Override
    public Item newInstance(TerrainPosition position) {
        return new DirtRoadItem(position);
    }
}
