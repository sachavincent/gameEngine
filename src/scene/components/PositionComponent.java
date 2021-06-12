package scene.components;

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

        this.updateComponentCallback = gameObject -> {
            if (gameObject.getComponents().containsKey(RequirementComponent.class.getName()))
                Scene.getInstance().updateBuildingRequirements();
        };
    }

    public void setPosition(Vector3f position) {
        this.position = position;
        update();
    }

    public Vector3f getPosition() {
        return this.position;
    }
}