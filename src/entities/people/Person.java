package entities.people;

import java.util.Objects;

public class Person {

    private final SocialClass socialClass;

    public Person(SocialClass socialClass) {
        this.socialClass = socialClass;
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
        return socialClass == person.socialClass;
    }

    @Override
    public int hashCode() {
        return Objects.hash(socialClass);
    }

    @Override
    public String toString() {
        return "Person{" +
                "socialClass=" + socialClass +
                '}';
    }
}
