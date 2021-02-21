package items;

import static items.ConnectableItem.Connections.NONE;

import entities.Camera.Direction;
import terrains.TerrainPosition;

public class PlaceHolderConnectableItem extends PlaceHolderItem implements ConnectableItem {

    private final Connections[] individualConnections = new Connections[]{NONE, NONE, NONE, NONE};

    public PlaceHolderConnectableItem(ConnectableItem parent, TerrainPosition relativePosToItem) {
        super(((Item) parent), relativePosToItem);
    }

    @Override
    public void connect(Direction direction, Connections connections) {
        if (getAccessPoints()[direction.ordinal()]) {
            ((ConnectableItem) parent).connect(direction, connections);
            individualConnections[direction.ordinal()] = connections;
        } else
            System.err.println("Error");
    }

    @Override
    public void disconnect(Direction direction) {
        ((ConnectableItem) parent).disconnect(direction);
        individualConnections[direction.ordinal()] = NONE;
    }

    // WEST NORTH EAST SOUTH
    @Override
    public boolean[] getAccessPoints() {
        boolean[] accessPoints = new boolean[4];
        if (relativePosition.getZ() < 0)
            accessPoints[0] = true;
        if (relativePosition.getX() < 0)
            accessPoints[1] = true;
        if (relativePosition.getZ() > 0)
            accessPoints[2] = true;
        if (relativePosition.getX() > 0)
            accessPoints[3] = true;

        return accessPoints;
    }

    public boolean isParentConnected(Direction direction) {
        return ((ConnectableItem) parent).isConnected(direction);
    }

    @Override
    public boolean isConnected(Direction direction) {
        return individualConnections[direction.ordinal()] != NONE;
    }

    @Override
    public boolean isConnected() {
        for (Direction direction : Direction.values())
            if (isConnected(direction))
                return true;

        return false;
    }

    @Override
    public TerrainPosition getOffset(Direction direction) {
        return parent.getOffset(direction);
    }

    @Override
    public String toString() {
        return "PlaceHolderConnectableItem{" +
                "parent=" + parent +
                ", relativePosition=" + relativePosition +
                "} ";
    }
}
