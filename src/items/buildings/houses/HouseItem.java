package items.buildings.houses;

import abstractItem.AbstractMarket;
import entities.Camera.Direction;
import items.Item;
import items.buildings.BuildingItem;
import java.util.HashMap;
import java.util.Map;

public class HouseItem extends BuildingItem implements RequireBuilding {

    private final int peopleCapacity;

    private boolean meetRequirements;

    public HouseItem(String name, Item copy, int peopleCapacity, int xNegativeOffset, int xPositiveOffset, int height,
            int zNegativeOffset, int zPositiveOffset, Direction... directions) {
        super(name, copy, xNegativeOffset, xPositiveOffset, height, zNegativeOffset, zPositiveOffset, directions);

        this.peopleCapacity = peopleCapacity;
    }

    public int getPeopleCapacity() {
        return this.peopleCapacity;
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
    public Map<BuildingItem, Integer> getRequirements() {
        Map<BuildingItem, Integer> requirements = new HashMap<>();

        requirements.put(AbstractMarket.getAbstractInstance(), 0);

        return requirements;
    }

    @Override
    public void meetRequirements() {
        this.meetRequirements = true;
    }

    @Override
    public boolean doesMeetRequirements() {
        return this.meetRequirements;
    }
}
