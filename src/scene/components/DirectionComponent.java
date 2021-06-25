package scene.components;

import entities.Camera.Direction;
import renderEngine.BuildingRenderer;
import renderEngine.Renderer;

public class DirectionComponent extends Component {

    private Direction direction;

    public DirectionComponent() {
        this(Direction.defaultDirection());
    }

    public DirectionComponent(Direction direction) {
        setOnUpdateComponentCallback(gameObject -> {
            Renderer renderer = gameObject.getComponent(RendererComponent.class).getRenderer();
            if (renderer instanceof BuildingRenderer) {
                ((BuildingRenderer) renderer).removeGameObject(gameObject);
            }
        });

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
