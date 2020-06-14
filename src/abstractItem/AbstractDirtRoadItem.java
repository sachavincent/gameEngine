package abstractItem;

import items.Item;
import items.roads.DirtRoadItem;

public class AbstractDirtRoadItem extends AbstractRoadItem {

    public AbstractDirtRoadItem() {
        super(ItemPreviews.DIRT_ROAD, new DirtRoadItem());
    }

    @Override
    public Item getItemInstance() {
        return new DirtRoadItem();
    }
}
