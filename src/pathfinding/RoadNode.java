package pathfinding;

import terrains.TerrainPosition;

public class RoadNode extends Road {

    public static final int SCORE = 1;

    public RoadNode(TerrainPosition position) {
        super(position, SCORE);
    }

    @Override
    public String toString() {
        return "RoadNode{" +
                "position=" + super.getPosition() +
                ", score=" + super.getScore() +
                ", hScore=" + super.gethScore() +
                '}';
    }
}
