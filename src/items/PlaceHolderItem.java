package items;

import terrains.TerrainPosition;

public class PlaceHolderItem extends Item {

    protected Item            parent;
    protected TerrainPosition relativePosition;

    public PlaceHolderItem(Item parent, TerrainPosition relativePosToItem) {
        super(parent, relativePosToItem);

        this.parent = parent;
        this.relativePosition = relativePosToItem;
    }

    public Item getParent() {
        return this.parent;
    }

    public TerrainPosition getRelativePosition() {
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
