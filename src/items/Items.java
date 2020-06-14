package items;

import items.buildings.houses.Insula;
import renderEngine.OBJLoader;

public class Items {

    public static final Item INSULA = OBJLoader.loadObjForItem(new Insula());
//    public static final Item roadItem;
//
//    static {
//        roadItem = new RoadItem();
//    }
}
