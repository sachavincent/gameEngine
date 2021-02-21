package items.buildings;

import entities.Camera.Direction;
import items.Items;
import terrains.TerrainPosition;

public class Market extends BuildingItem {

    private final static int X_POSITIVE_OFFSET = 4;
    private final static int X_NEGATIVE_OFFSET = 4;
    private final static int HEIGHT            = 3;
    private final static int Z_POSITIVE_OFFSET = 4;
    private final static int Z_NEGATIVE_OFFSET = 4;

    public final static String NAME = "Market";

    public Market(TerrainPosition terrainPosition) {
        super(terrainPosition, NAME, Items.MARKET, X_NEGATIVE_OFFSET, X_POSITIVE_OFFSET, HEIGHT, Z_NEGATIVE_OFFSET,
                Z_POSITIVE_OFFSET, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
    }

    public Market() {
        super(new TerrainPosition(0, 0), NAME, Items.MARKET, X_NEGATIVE_OFFSET, X_POSITIVE_OFFSET, HEIGHT,
                Z_NEGATIVE_OFFSET,
                Z_POSITIVE_OFFSET, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
    }

    @Override
    public String toString() {
        return "Market{" +
                "name='" + NAME + '\'' +
                ", id=" + id +
                ", xNegativeOffset=" + xNegativeOffset +
                ", xPositiveOffset=" + xPositiveOffset +
                ", height=" + height +
                ", zNegativeOffset=" + zNegativeOffset +
                ", zPositiveOffset=" + zPositiveOffset +
                ", texture=" + texture +
                ", previewTexture=" + previewTexture +
                ", boundingBox=" + boundingBox +
                ", selectionBox=" + selectionBox +
                ", facingDirection=" + facingDirection +
                ", scale=" + scale +
                ", selected=" + selected +
                "} ";
    }
}
