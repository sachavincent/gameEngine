package scene.components;

import renderEngine.BuildingRenderer;
import renderEngine.GameObjectRenderer;
import scene.Scene;
import scene.components.requirements.ResourceRequirementComponent;
import terrains.TerrainPosition;
import util.math.Vector3f;

public class PositionComponent extends Component {

    protected Vector3f position;

    public PositionComponent(TerrainPosition position) {
        this(position.toVector3f());
    }

    public PositionComponent() {
        this(new TerrainPosition(-1, -1));
    }

    public PositionComponent(Vector3f position) {
        this.position = position;

        setOnUpdateComponentCallback(gameObject -> {
            if (gameObject.hasComponent(ResourceRequirementComponent.class))
                Scene.getInstance().updateBuildingRequirements();

            GameObjectRenderer gameObjectRenderer = gameObject.getComponent(RendererComponent.class).getRenderer();
            if (gameObjectRenderer instanceof BuildingRenderer) {
                ((BuildingRenderer) gameObjectRenderer).removeGameObject(gameObject);
            }
        });
    }

    public void setPosition(Vector3f position) {
        this.position = position;
        update();
    }

    public Vector3f getPosition() {
        return this.position;
    }
}