package items.buildings.houses;

import entities.Camera.Direction;
import items.Item;
import items.buildings.BuildingItem;

public class HouseItem extends BuildingItem {

    private int peopleCapacity;

    public HouseItem(String name, Item copy, int peopleCapacity, int xWidth, int height, int zWidth,
            Direction... directions) {
        super(name, copy, xWidth, height, zWidth, directions);

        this.peopleCapacity = peopleCapacity;
    }

    public int getPeopleCapacity() {
        return this.peopleCapacity;
    }
}
