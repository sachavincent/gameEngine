package items.buildings.houses;

import entities.Camera.Direction;
import items.Items;
import items.abstractItem.AbstractItem;
import items.abstractItem.AbstractMarket;
import java.util.EnumSet;
import java.util.Map;
import pathfinding.RouteFinder.Route;
import people.SocialClass;
import terrains.TerrainPosition;

public class Insula extends HouseItem {

    private final static int MAX_PEOPLE_CAPACITY = 10;

    private final static int X_POSITIVE_OFFSET = 3;
    private final static int X_NEGATIVE_OFFSET = 2;
    private final static int HEIGHT            = 3;
    private final static int Z_POSITIVE_OFFSET = 2;
    private final static int Z_NEGATIVE_OFFSET = 2;

    public final static String NAME = "Insula";

    private final Map<AbstractItem, Integer> neededRequirements = Map.of(AbstractMarket.getInstance(), 0);

    public final static EnumSet<SocialClass> socialClasses = EnumSet.of(SocialClass.FARMER);

    public Insula(TerrainPosition terrainPosition) {
        super(terrainPosition, NAME, Items.INSULA, MAX_PEOPLE_CAPACITY, X_NEGATIVE_OFFSET, X_POSITIVE_OFFSET, HEIGHT,
                Z_NEGATIVE_OFFSET, Z_POSITIVE_OFFSET, socialClasses, Direction.any());
    }

    public Insula() {
        this(new TerrainPosition(0, 0));
    }

    @Override
    public boolean doesMeetAllRequirements() {
        return this.requirements.equals(this.neededRequirements.keySet());
    }

    @Override
    public void meetRequirement(AbstractItem abstractItem, Route routeToItem) {
        if (this.neededRequirements.containsKey(abstractItem))
            this.requirements.put(abstractItem, routeToItem);
    }

    /**
     * Requirements of the Item
     * For each item, the max length (road) at which the requirement needs to be found
     * This integer defines the search radius.
     * 0 = infinite range
     *
     * @return the requirements
     */
    @Override
    public Map<AbstractItem, Integer> getRequirements() {
        return this.neededRequirements;
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
                "} ";
    }
}
