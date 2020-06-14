package items;

import entities.Camera.Direction;

public interface ConnectableItem {

    void connect(Direction direction);

    void disconnect(Direction direction);

    boolean[] getAccessPoints();
}
