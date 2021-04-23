package scene.components;

import entities.Camera.Direction;

public class DirectionComponent implements Component {

    private Direction direction = Direction.NORTH;

    public Direction getDirection() {
        return this.direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}
