package pathfinding;

import util.math.Vector2f;

public class NormalRoad extends Road {

    public static final int SCORE = 1;

    public NormalRoad(Vector2f position) {
        super(position, SCORE);
    }
}
