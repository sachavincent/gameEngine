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
        Insula insula = new Insula();

//        new ImmigrationEvent(insula, new Random().nextInt(10000));

        return insula;
    }

    public static Insula getAbstractInstance() {
        return abstractInstance;
    }
}
