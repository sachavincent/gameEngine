package items;

import entities.Camera.Direction;
import util.math.Vector2f;

public class PlaceHolderConnectableItem extends Item implements ConnectableItem {

    private ConnectableItem parent;
    private Vector2f        relativePosition;

    public PlaceHolderConnectableItem(ConnectableItem parent, Vector2f relativePosToItem) {
        super(((Item) parent).getName(), 1, 1, 1, 1, 1);

        this.parent = parent;
        this.relativePosition = relativePosToItem;
    }

    public ConnectableItem getParent() {
        return this.parent;
    }

    public Vector2f getRelativePosition() {
        return this.relativePosition;
    }

    @Override
    public void connect(Direction direction, Connections connections) {
        parent.connect(direction, connections);
    }

    @Override
    public void disconnect(Direction direction) {
        parent.disconnect(direction);
    }

    @Override
    public boolean[] getAccessPoints() {
        return parent.getAccessPoints();
    }

    @Override
    public boolean isConnected(Direction direction) {
        return parent.isConnected(direction);
    }

    @Override
    public Vector2f getOffset(Direction direction) {
        return ((Item) parent).getOffset(direction);
    }

    @Override
    public String toString() {
        return "PlaceHolderConnectableItem{" +
                "parent=" + parent +
                ", relativePosition=" + relativePosition +
                "} " + super.toString();
    }
}
