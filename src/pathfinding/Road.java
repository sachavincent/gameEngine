package pathfinding;

import java.util.Objects;
import terrains.TerrainPosition;

public abstract class Road {

    protected TerrainPosition position;
    protected int      score;
    protected int      hScore;

    public Road(TerrainPosition position, int score) {
        this.position = position;
        this.score = score;
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


    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Road road = (Road) o;
        return score == road.score &&
                hScore == road.hScore &&
                Objects.equals(position, road.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, score);
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
