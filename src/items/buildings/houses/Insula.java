package items.buildings.houses;

import entities.Camera.Direction;
import items.Items;

public class Insula extends HouseItem {

    private final static int MAX_PEOPLE_CAPACITY = 10;
    private final static int X_WIDTH             = 7;
    private final static int HEIGHT              = 3;
    private final static int Z_WIDTH             = 7;

    public final static String name = "insula";

    public Insula() {
        super(name, Items.INSULA, MAX_PEOPLE_CAPACITY, X_WIDTH, HEIGHT, Z_WIDTH, Direction.NORTH);
    }

    @Override
    public String toString() {
        return "Insula{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", width=" + xWidth +
                ", height=" + height +
                ", depth=" + zWidth +
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
