package scene.components;

import entities.Camera.Direction;

public class DirectionComponent extends Component {

    private Direction direction;

    public DirectionComponent() {
        this(Direction.defaultDirection());
    }

    public DirectionComponent(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public void setDirection(Direction direction) {
        if (direction == null)
            return;

        this.direction = direction;

        update();
    }
}