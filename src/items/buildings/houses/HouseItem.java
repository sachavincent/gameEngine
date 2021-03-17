package items.buildings.houses;

import engineTester.Game;
import entities.Camera.Direction;
import guis.Gui;
import guis.prefabs.GuiHouseDetails.GuiHouseDetails;
import items.Item;
import items.abstractItem.AbstractItem;
import items.buildings.BuildingItem;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import pathfinding.RouteFinder.Route;
import people.Farmer;
import people.Person;
import people.SocialClass;
import resources.ResourceManager.Resource;
import terrains.TerrainPosition;

public abstract class HouseItem extends BuildingItem implements RequireBuilding {

    private final int                                maxPeopleCapacity;
    private final EnumMap<SocialClass, List<Person>> classes;

    protected Map<AbstractItem, Route> requirements = new HashMap<>();

    public HouseItem(TerrainPosition terrainPosition, String name, Item copy, int maxPeopleCapacity,
            int xNegativeOffset, int xPositiveOffset, int height, int zNegativeOffset, int zPositiveOffset,
            EnumSet<SocialClass> socialClasses,             Direction... directions) {
        super(terrainPosition, name, copy, xNegativeOffset, xPositiveOffset,
                height, zNegativeOffset, zPositiveOffset, directions);

        this.maxPeopleCapacity = maxPeopleCapacity;
        this.classes = new EnumMap<>(SocialClass.class);

        for (SocialClass socialClass : socialClasses) {
            this.classes.put(socialClass, new ArrayList<>());
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

        return Game.getInstance().addPerson();
    }


    public void removePerson(Person person) {
        if (getNumberOfPeople() <= 0)
            return;

        this.classes.get(person.getSocialClass()).remove(person);

        Game.getInstance().removePerson();
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
    public boolean doesMeetRequirement(AbstractItem abstractItem) {
        return this.requirements.containsKey(abstractItem);
    }

    @Override
    public Route getRouteToItem(AbstractItem abstractItem) {
        return this.requirements.get(abstractItem);
    }

}
