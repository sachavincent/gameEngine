package people;

import java.util.EnumMap;
import java.util.Objects;
import resources.ResourceManager.Resource;

public class Person {

    private static long max_id = 1;

    private final EnumMap<Resource, Integer> resourcesNeeded;
    private final SocialClass                socialClass;

    private final long id;

    public Person(SocialClass socialClass, EnumMap<Resource, Integer> resourcesNeeded) {
        this.resourcesNeeded = resourcesNeeded;
        this.socialClass = socialClass;
        this.id = max_id++;
    }

    public SocialClass getSocialClass() {
        return this.socialClass;
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

    @Override
    public int hashCode() {
        return Objects.hash(socialClass, id);
    }

    public EnumMap<Resource, Integer> getResourcesNeeded() {
        return this.resourcesNeeded;
    }

    @Override
    public String toString() {
        return "Person{" +
                "socialClass=" + socialClass +
                ", id=" + id +
                '}';
    }
}
