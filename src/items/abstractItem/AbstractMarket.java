package items.abstractItem;

import items.Item;
import items.buildings.Market;
import terrains.TerrainPosition;

public class AbstractMarket extends AbstractItem {

    private static AbstractMarket instance;

    private AbstractMarket() {
        super(ItemPreviews.MARKET, new Market());
    }

    public static AbstractMarket getInstance() {
        return instance == null ? (instance = new AbstractMarket()) : instance;
    }

    @Override
    public Item newInstance(TerrainPosition position) {
        return new Market(position);
    }
}
