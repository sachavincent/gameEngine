package scene.components;

import entities.Camera.Direction;

public class RoadConnectionsComponent implements Component {

    // WEST NORTH EAST SOUTH
    private final boolean[] accessPoints = new boolean[]{true, true, true, true};
    private final boolean[] connections  = new boolean[]{false, false, false, false};

    public RoadConnectionsComponent(boolean west, boolean north, boolean east, boolean south) {
        this.accessPoints[0] = west;
        this.accessPoints[1] = north;
        this.accessPoints[2] = east;
        this.accessPoints[3] = south;
    }

    public RoadConnectionsComponent(boolean[] accessPoints) {
        this(accessPoints[0], accessPoints[1], accessPoints[2], accessPoints[3]);
    }

    public RoadConnectionsComponent() {
    }

    public boolean isConnected(Direction direction) {
        return this.connections[direction.ordinal()];
    }

    public boolean isConnected() {
        for (Direction direction : Direction.values()) {
            if (getAccessPoints()[direction.ordinal()] && isConnected(direction))
                return true;
        }
        return false;
    }

    public void connect(Direction direction) {
        if (this.accessPoints[direction.ordinal()])
            this.connections[direction.ordinal()] = true;
    }

    public void disconnect(Direction direction) {
        this.connections[direction.ordinal()] = false;
    }


    public boolean[] getAccessPoints() {
        return this.accessPoints;
    }
}
