package pathfinding;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import terrains.TerrainPosition;

public abstract class Road {

    protected TerrainPosition position;
    protected int             score;
    protected int             hScore;

    public Road(TerrainPosition position, int score) {
        this.position = position;
        this.score = score;
    }

    /**
     * Copy constructor
     */
    protected Road(TerrainPosition position, Integer score, Integer hScore) {
        this.position = position;
        this.score = score;
        this.hScore = hScore;
    }

    public int gethScore() {
        return this.hScore;
    }

    public void sethScore(int hScore) {
        this.hScore = hScore;
    }

    public TerrainPosition getPosition() {
        return this.position;
    }

    public int getScore() {
        return this.score;
    }

    public Road clone() {
        try {
            Class<?> cl = this.getClass();
            Constructor<?> cons = cl.getConstructor(TerrainPosition.class, Integer.class, Integer.class);
            return (Road) cons.newInstance(this.position, this.score, this.hScore);
        } catch (NoSuchMethodException | SecurityException |
                InstantiationException | IllegalAccessException |
                IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Road road = (Road) o;
        return Objects.equals(position, road.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position);
    }

    @Override
    public String toString() {
        return "Road{" +
                "position=" + position +
                ", score=" + score +
                ", hScore=" + hScore +
                '}';
    }

}
