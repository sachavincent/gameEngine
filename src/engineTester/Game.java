package engineTester;

import terrains.Terrain;

public class Game {

    private static Game instance;

    private int     numberOfPeople;
    private boolean isStarted;

    private Game() {
    }

    public static Game getInstance() {
        return instance == null ? (instance = new Game()) : instance;
    }

    public int getNumberOfPeople() {
        return this.numberOfPeople;
    }

    public boolean addPerson() {
        if (this.numberOfPeople >= Terrain.getInstance().getMaxPeopleCapacity())
            return false;

        this.numberOfPeople++;

        return true;
    }

    public void removePerson() {
        if (this.numberOfPeople <= 0)
            return;

        this.numberOfPeople--;
    }

    public void start() {
        this.isStarted = true;
    }

    public boolean isStarted() {
        return this.isStarted;
    }
}
