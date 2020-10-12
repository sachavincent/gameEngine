package items;

import util.math.Vector2f;

public class PlaceHolderItem extends Item {

    protected Item     parent;
    protected Vector2f relativePosition;

    public PlaceHolderItem(Item parent, Vector2f relativePosToItem) {
        super(parent);

        this.parent = parent;
        this.relativePosition = relativePosToItem;
    }

    public Item getParent() {
        return this.parent;
    }

    public Vector2f getRelativePosition() {
        return this.relativePosition;
    }

    @Override
    public String toString() {
        return "PlaceHolderItem{" +
                "item=" + parent +
                ", relativePosition=" + relativePosition +
                "} " + super.toString();
    }
}
