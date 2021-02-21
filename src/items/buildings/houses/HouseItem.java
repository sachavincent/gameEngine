package items.buildings.houses;

import abstractItem.AbstractMarket;
import entities.Camera.Direction;
import guis.Gui;
import guis.prefabs.GuiHouseDetails.GuiHouseDetails;
import items.Item;
import items.buildings.BuildingItem;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import people.Farmer;
import people.Person;
import people.SocialClass;
import resources.ResourceManager.Resource;
import terrains.Terrain;
import terrains.TerrainPosition;

public class HouseItem extends BuildingItem implements RequireBuilding {

    private final int maxPeopleCapacity;

    private final EnumMap<SocialClass, List<Person>> classes;

    private boolean meetRequirements;

    public HouseItem(TerrainPosition terrainPosition, String name, Item copy, int maxPeopleCapacity,
            int xNegativeOffset, int xPositiveOffset, int height, int zNegativeOffset, int zPositiveOffset,
            EnumSet<SocialClass> socialClasses, Direction... directions) {
        super(terrainPosition, name, copy, xNegativeOffset, xPositiveOffset, height, zNegativeOffset, zPositiveOffset,
                directions);

        this.maxPeopleCapacity = maxPeopleCapacity;
        this.classes = new EnumMap<>(SocialClass.class);

        for (SocialClass socialClass : socialClasses) {
            classes.put(socialClass, new ArrayList<>());
        }
    }

    public Map<Resource, Integer> getResourcesNeeded() {
        List<Person> peopleList = classes.values().stream().flatMap(List::stream).collect(Collectors.toList());
        List<EnumMap<Resource, Integer>> allResources = peopleList.stream().map(Person::getResourcesNeeded)
                .collect(Collectors.toList());

        Set<Entry<Resource, Integer>> entries = allResources.stream()
                .flatMap(map -> map.entrySet().stream()).collect(Collectors.toSet());

        EnumMap<Resource, Integer> enumMap = new EnumMap<>(Resource.class);
        entries.forEach(entry -> enumMap.put(entry.getKey(), entry.getValue()));

        return enumMap;
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

        addPerson(new Farmer()); //todo temp
        GuiHouseDetails houseDetailsGui = GuiHouseDetails.getInstance();
        houseDetailsGui.setHouseItem(this);
        houseDetailsGui.setDisplayed(true);
    }

    @Override
    public void unselect() {
        super.unselect();

        Gui.hideGui(GuiHouseDetails.getInstance());
        GuiHouseDetails.getInstance().removeHouseItem();
    }

    @Override
    public boolean doesMeetRequirements() {
        return this.meetRequirements;
    }
}
