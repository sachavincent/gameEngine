package items.buildings;

import static items.ConnectableItem.Connections.NONE;

import entities.Camera.Direction;
import items.ConnectableItem;
import items.Item;
import items.RotatableItem;
import java.util.Arrays;
import util.math.Maths;

public abstract class BuildingItem extends Item implements RotatableItem, ConnectableItem {

    // WEST NORTH EAST SOUTH
    private       boolean[]     accessPoints = new boolean[]{true, true, true, true};
    private final Connections[] connected    = new Connections[]{NONE, NONE, NONE, NONE};

    public BuildingItem(String name, Item copy, int xNegativeOffset, int xPositiveOffset, int height,
            int zNegativeOffset, int zPositiveOffset, Direction... directions) {
        super(name, xNegativeOffset, xPositiveOffset, height, zNegativeOffset, zPositiveOffset);

        if (copy != null) {
            this.texture = copy.getTexture();
            this.previewTexture = copy.getPreviewTexture();
            this.boundingBox = copy.getBoundingBox();
            this.selectionBox = copy.getSelectionBox();
        }

        Arrays.stream(directions).forEach(direction -> accessPoints[direction.ordinal()] = true);
    }

    @Override
    public void setRotation(int rotation) {
        super.setRotation(rotation);
        int degree = this.facingDirection.getDegree();
        while (degree > 0) {
            this.accessPoints = Maths.shiftArray(accessPoints);
            degree -= 90;
        }
    }

    @Override
    public void connect(Direction direction, Connections connections) {
        if (this.accessPoints[direction.ordinal()])
            this.connected[direction.ordinal()] = connections;
    }

    @Override
    public void disconnect(Direction direction) {
        this.connected[direction.ordinal()] = NONE;
    }

    public boolean isConnected(Direction direction) {
        return this.connected[direction.ordinal()] != NONE;
    }

    @Override
    public boolean isConnected() {
        for (Direction direction : Direction.values()) {
            if (getAccessPoints()[direction.ordinal()] && isConnected(direction))
                return true;
        }
        return false;
    }

    @Override
    public boolean[] getAccessPoints() {
        return accessPoints;
    }
}
