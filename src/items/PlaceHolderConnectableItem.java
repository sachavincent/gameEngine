package items;

import entities.Camera.Direction;
import util.math.Vector2f;

public class PlaceHolderConnectableItem extends Item implements ConnectableItem {

    private ConnectableItem item;
    private Vector2f        relativePosition;

    public PlaceHolderConnectableItem(ConnectableItem item, Vector2f relativePosToItem) {
        super(1, 1, 1);

        this.item = item;
        this.relativePosition = relativePosToItem;
    }

    public ConnectableItem getItem() {
        return this.item;
    }

    public Vector2f getRelativePosition() {
        return this.relativePosition;
    }

    @Override
    public void connect(Direction direction) {
        item.connect(direction);
    }

    @Override
    public void disconnect(Direction direction) {
        item.disconnect(direction);
    }

    @Override
    public boolean[] getAccessPoints() {
        return item.getAccessPoints();
    }
}
