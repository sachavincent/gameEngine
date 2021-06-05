package scene.components;

import entities.Camera.Direction;
import scene.Scene;

public class RoadConnectionsComponent extends Component {

    // EAST NORTH WEST SOUTH
    private final boolean[] accessPoints = new boolean[]{true, true, true, true};
    private final boolean[] connections  = new boolean[]{false, false, false, false};

    public RoadConnectionsComponent(boolean west, boolean north, boolean east, boolean south) {
        this(new boolean[]{west, north, east, south});
    }

    public RoadConnectionsComponent(boolean[] accessPoints) {
        super((gameObject, position) -> Scene.getInstance().updateRequirements());

        this.accessPoints[0] = accessPoints[0];
        this.accessPoints[1] = accessPoints[1];
        this.accessPoints[2] = accessPoints[2];
        this.accessPoints[3] = accessPoints[3];
    }

    public RoadConnectionsComponent() {
        this(new boolean[]{true, true, true, true});
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
