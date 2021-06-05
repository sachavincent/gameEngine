package pathfinding;

import terrains.TerrainPosition;

public class NodeRoad extends Road {

    public static final int SCORE = 1;

    public NodeRoad(TerrainPosition position) {
        super(position, SCORE);
    }

    public NodeRoad(TerrainPosition position, Integer score, Integer hScore) {
        super(position, score, hScore);
    }

    public NodeRoad(NodeRoad nodeRoad) {
        this(nodeRoad.position, nodeRoad.score, nodeRoad.hScore);
    }

    @Override
    public String toString() {
        return "NodeRoad{" +
                "position=" + super.getPosition() +
                ", score=" + super.getScore() +
                ", hScore=" + super.gethScore() +
                '}';
    }
}
