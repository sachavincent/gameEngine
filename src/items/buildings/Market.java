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
                Z_POSITIVE_OFFSET, Direction.any());
    }

    public Market() {
        this(new TerrainPosition(0, 0));
    }

    @Override
    public String toString() {
        return "Market{" +
                "name='" + NAME + '\'' +
                ", id=" + id +
                ", xNegativeOffset=" + getxNegativeOffset() +
                ", xPositiveOffset=" + getxPositiveOffset() +
                ", height=" + height +
                ", zNegativeOffset=" + getzNegativeOffset() +
                ", zPositiveOffset=" + getzPositiveOffset() +
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
