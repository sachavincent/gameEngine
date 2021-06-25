package scene.components;

import renderEngine.BuildingRenderer;
import renderEngine.Renderer;
import scene.Scene;
import scene.components.requirements.RequirementComponent;
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
            if (gameObject.hasComponent(RequirementComponent.class))
                Scene.getInstance().updateBuildingRequirements();

            Renderer renderer = gameObject.getComponent(RendererComponent.class).getRenderer();
            if (renderer instanceof BuildingRenderer) {
                ((BuildingRenderer) renderer).removeGameObject(gameObject);
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