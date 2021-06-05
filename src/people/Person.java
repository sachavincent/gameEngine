package people;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import people.SocialClass.PersonalResourceInfos;
import resources.ResourceManager.Resource;
import util.TimeSystem;

public class Person {

    private static long max_id = 1;

    private final long id;

    private final SocialClass socialClass;
    private       int         settlementTime;
    private       int         idHouse;

    public Person(SocialClass socialClass) {
        this.socialClass = socialClass;
        this.id = max_id++;
    }

    public SocialClass getSocialClass() {
        return this.socialClass;
    }

    public int getSettlementTime() {
        return this.settlementTime;
    }

    public void settle(int idHouse) {
        this.settlementTime = TimeSystem.getCurrentTime();
        this.idHouse = idHouse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Person person = (Person) o;
        return id == person.id &&
                socialClass == person.socialClass;
    }

    public int getIdHouse() {
        return this.idHouse;
    }

    @Override
    public int hashCode() {
        return Objects.hash(socialClass, id);
    }

    @Override
    public String toString() {
        return "Person{" +
                "socialClass=" + socialClass +
                ", id=" + id +
                '}';
    }

    public static Map<Resource, Integer> getResourcesNeeded(EnumMap<SocialClass, List<Person>> persons) {
        List<Person> peopleList = persons.values().stream().flatMap(List::stream).collect(Collectors.toList());
        List<PersonalResourceInfos> allResources = peopleList.stream()
                .map(person -> person.getSocialClass().getPersonalResourceInfos())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        EnumMap<Resource, Integer> enumMap = new EnumMap<>(Resource.class);

        for (Resource resource : Resource.values()) {
            int nbResources = allResources.stream().filter(info -> info.getResource() == resource)
                    .mapToInt(PersonalResourceInfos::getNbResourcesNeeded).sum();
            if (nbResources > 0)
                enumMap.put(resource, nbResources);
        }

        return enumMap;
    }
}
