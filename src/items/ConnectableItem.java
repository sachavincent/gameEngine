package items;

import entities.Camera.Direction;
import items.buildings.BuildingItem;
import items.roads.RoadItem;

public interface ConnectableItem {

    void connect(Direction direction, Connections connections);

    void disconnect(Direction direction);

    // WEST NORTH EAST SOUTH
    boolean[] getAccessPoints();

    boolean isConnected(Direction direction);

    boolean isConnected();

    enum Connections {
        BUILDING,
        ROAD,
        NONE;

        public static Connections getConnections(ConnectableItem connectableItem) {
            if (connectableItem instanceof PlaceHolderConnectableItem)
                connectableItem = (ConnectableItem) ((PlaceHolderConnectableItem) connectableItem).getParent();
            if (connectableItem instanceof BuildingItem)
                return BUILDING;
            if (connectableItem instanceof RoadItem)
                return ROAD;

            return NONE;
        }
    }
}
