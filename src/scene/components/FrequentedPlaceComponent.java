package scene.components;

import java.util.EnumMap;
import java.util.List;
import people.Person;
import people.SocialClass;

public class FrequentedPlaceComponent implements Component {

    private final EnumMap<SocialClass, List<Person>> persons;
    private final int                                maxPeopleCapacity;

    public FrequentedPlaceComponent(int maxPeopleCapacity) {
        this.maxPeopleCapacity = maxPeopleCapacity;
        this.persons = new EnumMap<>(SocialClass.class);
    }

    public int getCurrentPeopleCount() {
        return this.persons.size();
    }

    public void addPerson(Person person) {
        this.persons.get(person.getSocialClass()).add(person);
    }

    public int getMaxPeopleCapacity() {
        return this.maxPeopleCapacity;
    }

    public EnumMap<SocialClass, List<Person>> getPersons() {
        return this.persons;
    }
}
