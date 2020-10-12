package items.buildings.houses;

import entities.Camera.Direction;
import items.Items;

public class Insula extends HouseItem {

    private final static int MAX_PEOPLE_CAPACITY = 10;
    private final static int X_POSITIVE_OFFSET   = 3;
    private final static int X_NEGATIVE_OFFSET   = 2;
    private final static int HEIGHT              = 3;
    private final static int Z_POSITIVE_OFFSET   = 2;
    private final static int Z_NEGATIVE_OFFSET   = 2;

    public final static String NAME = "Insula";

    public Insula() {
        super(NAME, Items.INSULA, MAX_PEOPLE_CAPACITY, X_NEGATIVE_OFFSET, X_POSITIVE_OFFSET, HEIGHT, Z_NEGATIVE_OFFSET,
                Z_POSITIVE_OFFSET, Direction.NORTH);
    }

    @Override
    public String toString() {
        return "Insula{" +
                "id=" + id +
                ", texture=" + texture +
                ", previewTexture=" + previewTexture +
                ", boundingBox=" + boundingBox +
                ", selectionBox=" + selectionBox +
                ", direction=" + facingDirection +
                ", scale=" + scale +
                ", selected=" + selected +
                "} " + super.toString();
    }
}
