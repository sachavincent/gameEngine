package items.buildings.houses;

import abstractItem.AbstractMarket;
import entities.Camera.Direction;
import guis.Gui;
import guis.prefabs.GuiHouseDetails;
import items.Item;
import items.buildings.BuildingItem;
import java.util.HashMap;
import java.util.Map;
import terrains.Terrain;

public class HouseItem extends BuildingItem implements RequireBuilding {

    private final int maxPeopleCapacity;

    private int numberOfPeople;

    private boolean meetRequirements;

    public HouseItem(String name, Item copy, int maxPeopleCapacity, int xNegativeOffset, int xPositiveOffset,
            int height,
            int zNegativeOffset, int zPositiveOffset, Direction... directions) {
        super(name, copy, xNegativeOffset, xPositiveOffset, height, zNegativeOffset, zPositiveOffset, directions);

        this.maxPeopleCapacity = maxPeopleCapacity;
        this.numberOfPeople = 0;
    }

    public int getNumberOfPeople() {
        return this.numberOfPeople;
    }

    public int getMaxPeopleCapacity() {
        return this.maxPeopleCapacity;
    }

    public boolean addPerson() {
        if (numberOfPeople >= maxPeopleCapacity)
            return false;

        numberOfPeople++;

        return Terrain.getInstance().addPerson();
    }


    public void removePerson() {
        if (numberOfPeople <= 0)
            return;

        numberOfPeople--;
        Terrain.getInstance().removePerson();
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
    public void select() {
        super.select();

        new GuiHouseDetails.Builder(this).create();
        Gui.showGui(GuiHouseDetails.getHouseDetailsGui());
    }

    @Override
    public void unselect() {
        super.unselect();

        Gui.hideGui(GuiHouseDetails.getHouseDetailsGui());
        GuiHouseDetails.getHouseDetailsGui().removeHouseItem();
    }

    @Override
    public boolean doesMeetRequirements() {
        return this.meetRequirements;
    }
}
