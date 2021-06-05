package scene.components;

import entities.Camera.Direction;

public class DirectionComponent extends Component {

    private Direction direction = Direction.NORTH;

    public DirectionComponent() {
    }

    public DirectionComponent(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;

        update();
    }
}
