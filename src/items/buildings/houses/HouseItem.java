package items.buildings.houses;

import abstractItem.AbstractMarket;
import entities.Camera.Direction;
import guis.Gui;
import guis.prefabs.GuiHouseDetails;
import items.Item;
import items.buildings.BuildingItem;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import people.Person;
import people.SocialClass;
import terrains.Terrain;

public class HouseItem extends BuildingItem implements RequireBuilding {

    private final int maxPeopleCapacity;

    private final EnumMap<SocialClass, List<Person>> classes;

    private boolean meetRequirements;

    public HouseItem(String name, Item copy, int maxPeopleCapacity, int xNegativeOffset, int xPositiveOffset,
            int height, int zNegativeOffset, int zPositiveOffset, EnumSet<SocialClass> socialClasses,
            Direction... directions) {
        super(name, copy, xNegativeOffset, xPositiveOffset, height, zNegativeOffset, zPositiveOffset, directions);

        this.maxPeopleCapacity = maxPeopleCapacity;
        this.classes = new EnumMap<>(SocialClass.class);

        for (SocialClass socialClass : socialClasses) {
            classes.put(socialClass, new ArrayList<>());
        }
    }

    public EnumMap<SocialClass, List<Person>> getClasses() {
        return this.classes;
    }

    public int getNumberOfPeople() {
        return this.classes.values().stream().mapToInt(List::size).sum();
    }

    public int getMaxPeopleCapacity() {
        return this.maxPeopleCapacity;
    }

    public boolean addPerson(Person person) {
        if (getNumberOfPeople() >= maxPeopleCapacity)
            return false;

        this.classes.get(person.getSocialClass()).add(person);

        return Terrain.getInstance().addPerson();
    }


    public void removePerson(Person person) {
        if (getNumberOfPeople() <= 0)
            return;

        this.classes.get(person.getSocialClass()).remove(person);

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
