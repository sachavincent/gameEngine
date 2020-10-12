package items;

import items.buildings.Market;
import items.buildings.houses.Insula;
import renderEngine.OBJLoader;

public class Items {

    public static final Item INSULA = OBJLoader.loadObjForItem(new Insula(), true);

    public static final Item MARKET = OBJLoader.loadObjForItem(new Market(), false);

    public static final Item EMPTY = new EmptyItem(true);
}
