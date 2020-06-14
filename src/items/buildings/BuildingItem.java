package items.buildings;

import entities.Camera.Direction;
import items.ConnectableItem;
import items.Item;
import items.RotatableItem;
import java.util.Arrays;
import util.math.Maths;

public abstract class BuildingItem extends Item implements RotatableItem, ConnectableItem {

    private boolean[] accessPoints = new boolean[]{false, false, false, false};
    private boolean[] connected    = new boolean[]{false, false, false, false};

    public BuildingItem(String name, Item copy, int xWidth, int height, int zWidth, Direction... directions) {
        super(xWidth, height, zWidth);

        this.name = name;

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
    public void connect(Direction direction) {
        if (this.accessPoints[direction.ordinal()])
            this.connected[direction.ordinal()] = true;
    }

    @Override
    public void disconnect(Direction direction) {
        this.connected[direction.ordinal()] = false;
    }

    public boolean isConnected(Direction direction) {
        return this.connected[direction.ordinal()];
    }

    @Override
    public boolean[] getAccessPoints() {
        return accessPoints;
    }
}
