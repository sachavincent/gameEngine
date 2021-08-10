package scene.components;

import entities.Camera.Direction;
import renderEngine.BuildingRenderer;
import renderEngine.GameObjectRenderer;

public class DirectionComponent extends Component {

    private Direction direction;

    public DirectionComponent() {
        this(Direction.defaultDirection());
    }

    public DirectionComponent(Direction direction) {
        setOnUpdateComponentCallback(gameObject -> {
            GameObjectRenderer<?> gameObjectRenderer = gameObject.getComponent(RendererComponent.class).getRenderer();
            if (gameObjectRenderer instanceof BuildingRenderer) {
                gameObjectRenderer.removeGameObject(gameObject);
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