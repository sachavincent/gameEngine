package items;

import util.math.Vector2f;

public class PlaceHolderItem extends Item {

    private Item     item;
    private Vector2f relativePosition;

    public PlaceHolderItem(Item item, Vector2f relativePosToItem) {
        super(item.getName(), 1, 1, 1, 1, 1);

        this.item = item;
        this.relativePosition = relativePosToItem;
    }

    public Item getItem() {
        return this.item;
    }

    public Vector2f getRelativePosition() {
        return this.relativePosition;
    }
}
