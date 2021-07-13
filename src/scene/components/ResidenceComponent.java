package scene.components;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import people.Person;
import people.SocialClass;
import resources.ResourceManager;
import util.TimeSystem;

public class ResidenceComponent extends Component {

    public final static int MIN_TICKS_BETWEEN_SETTLEMENT_AND_MOVING_AWAY_TIER_2 = TimeSystem.TICK_RATE * 60;
    public final static int MIN_TICKS_BETWEEN_SETTLEMENT_AND_MOVING_AWAY_TIER_1 = TimeSystem.TICK_RATE * 30;
    public final static int COOLDOWN_LOCAL_MOVE_AWAY                            = (int) (TimeSystem.TICK_RATE * 22.5);

    protected final EnumMap<SocialClass, List<Person>> persons;
    protected final int                                maxPeopleCapacity;
    //TODO: Map for each spot, store moveAwayTime

    protected int lastLocalMoveAwayTime = TimeSystem.getCurrentTimeTicks();

    public ResidenceComponent(int maxPeopleCapacity) {
        this.maxPeopleCapacity = maxPeopleCapacity;
        this.persons = new EnumMap<>(SocialClass.class);
        this.persons.put(SocialClass.FARMER, new ArrayList<>());

        setOnTickElapsedCallback((gameObject, nbTicks) -> {
            EnumMap<SocialClass, Integer> peopleList = new EnumMap<>(SocialClass.class);

            this.persons.forEach((socialClass, people) -> {
                if (!peopleList.containsKey(socialClass))
                    peopleList.put(socialClass, 0);

                peopleList.put(socialClass, peopleList.get(socialClass) + people.size());
            });
            peopleList.forEach((socialClass, nb) -> {
                socialClass.getPersonalResourceInfos().forEach(personalResourceInfos -> {
                    double depletion = personalResourceInfos.getDepletionRate() * nb;
                    ResourceManager.removeFromResource(personalResourceInfos.getResource(), depletion);
                });
            });
            return true;
        });
    }

    public int getCurrentPeopleCount() {
        return this.persons.values().stream().mapToInt(List::size).sum();
    }

    public boolean addPerson(Person person) {
        if (!this.persons.containsKey(person.getSocialClass()))
            return false;

        person.settle(this.idGameObject);

        this.persons.get(person.getSocialClass()).add(person);

        update();
        return true;
    }

    public boolean removePerson(Person person) {
        boolean removed = this.persons.get(person.getSocialClass()).remove(person);
        if (removed) {
            this.lastLocalMoveAwayTime = TimeSystem.getCurrentTimeTicks();
            update();
        }
        return removed;
    }

    public int getLastLocalMoveAwayTime() {
        return this.lastLocalMoveAwayTime;
    }

    public int getMaxPeopleCapacity() {
        return this.maxPeopleCapacity;
    }

    public EnumMap<SocialClass, List<Person>> getPersons() {
        return this.persons;
    }
}
