package pathfinding;

import terrains.TerrainPosition;

public class NormalRoad extends Road {

    public static final int SCORE = 1;

    public NormalRoad(TerrainPosition position) {
        super(position, SCORE);
    }

    public NormalRoad(TerrainPosition position, Integer score, Integer hScore) {
        super(position, score, hScore);
    }

    public NormalRoad(NormalRoad normalRoad) {
        this(normalRoad.position, normalRoad.score, normalRoad.hScore);
    }

    @Override
    public String toString() {
        return "NormalRoad{" +
                "position=" + super.getPosition() +
                ", score=" + super.getScore() +
                ", hScore=" + super.gethScore() +
                '}';
    }
}
