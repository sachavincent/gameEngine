package items.abstractItem;

import items.Item;
import items.buildings.houses.Insula;
import terrains.TerrainPosition;

public class AbstractInsula extends AbstractItem {

    private static AbstractInsula instance;

    private AbstractInsula() {
        super(ItemPreviews.INSULA, new Insula());
    }

    public static AbstractInsula getInstance() {
        return instance == null ? (instance = new AbstractInsula()) : instance;
    }

    @Override
    public Item newInstance(TerrainPosition terrainPosition) {
        return new Insula(terrainPosition);
    }

}
