package pathfinding;

import terrains.TerrainPosition;

public class NormalRoad extends Road {

    public static final int SCORE = 1;

    public NormalRoad(TerrainPosition position) {
        super(position, SCORE);
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
