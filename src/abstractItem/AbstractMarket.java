package abstractItem;

import items.Item;
import items.buildings.Market;
import terrains.TerrainPosition;

public class AbstractMarket extends AbstractItem {

    private final static Market abstractInstance = new Market(new TerrainPosition(0, 0));

    public AbstractMarket() {
        super(ItemPreviews.MARKET, abstractInstance);
    }

    @Override
    public Item newInstance(TerrainPosition position) {
        return new Market(position);
    }

    public static Market getAbstractInstance() {
        return abstractInstance;
    }
}
