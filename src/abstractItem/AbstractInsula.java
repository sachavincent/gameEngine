package abstractItem;

import items.Item;
import items.buildings.houses.Insula;

public class AbstractInsula extends AbstractItem {

    private final static Insula abstractInstance = new Insula();

    public AbstractInsula() {
        super(ItemPreviews.INSULA, abstractInstance);
    }

    @Override
    public Item getItemInstance() {
        return new Insula();
    }

    public static Insula getAbstractInstance() {
        return abstractInstance;
    }
}
