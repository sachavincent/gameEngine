package abstractItem;

import items.Item;
import items.buildings.houses.Insula;
import terrains.TerrainPosition;

public class AbstractInsula extends AbstractItem {

    private final static Insula abstractInstance = new Insula();

    public AbstractInsula() {
        super(ItemPreviews.INSULA, abstractInstance);
    }

    @Override
    public Item newInstance(TerrainPosition terrainPosition) {
        return new Insula(terrainPosition);
    }

    public static Insula getAbstractInstance() {
        return abstractInstance;
    }
}
