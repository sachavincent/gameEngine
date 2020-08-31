package pathfinding;

import util.math.Vector2f;

public class RoadNode extends Road {

    public static final int SCORE = 1;

    public RoadNode(Vector2f position) {
        super(position, SCORE);
    }
}
