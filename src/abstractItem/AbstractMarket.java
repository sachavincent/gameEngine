package abstractItem;

import items.Item;
import items.buildings.Market;

public class AbstractMarket extends AbstractItem {

    private final static Market abstractInstance = new Market();

    public AbstractMarket() {
        super(ItemPreviews.MARKET, abstractInstance);
    }

    @Override
    public Item getItemInstance() {
        return new Market();
    }

    public static Market getAbstractInstance() {
        return abstractInstance;
    }
}
