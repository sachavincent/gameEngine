package items;

import items.abstractItem.AbstractInsula;
import items.abstractItem.AbstractMarket;
import renderEngine.OBJLoader;

public class Items {

    public static final Item INSULA = OBJLoader.loadObjForItem(AbstractInsula.getInstance().getPreviewItem(), true);

    public static final Item MARKET = OBJLoader.loadObjForItem(AbstractMarket.getInstance().getPreviewItem(), true);
}
