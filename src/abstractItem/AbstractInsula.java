package abstractItem;

import items.Item;
import items.buildings.houses.Insula;

public class AbstractInsula extends AbstractItem {

    public AbstractInsula() {
        super(ItemPreviews.INSULA, new Insula());
    }

    @Override
    public Item getItemInstance() {
        return new Insula();
    }
}
