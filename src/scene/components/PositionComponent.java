package scene.components;

import scene.gameObjects.GameObject;
import scene.Scene;
import terrains.TerrainPosition;
import util.math.Vector3f;

public class PositionComponent implements Component {

    private Vector3f position;

    public PositionComponent(TerrainPosition position) {
        this(position.toVector3f());
    }

    public PositionComponent(Vector3f position) {
        this.position = position;
    }

    public PositionComponent() {
        this(new TerrainPosition(0, 0));
    }

    public void setPosition(Vector3f position) {
        this.position = position;
        Scene.getInstance().updateRequirements();
    }

    public Vector3f getPosition() {
        return this.position;
    }

    @Override
    public void removeObject(GameObject gameObject) {
        Scene.getInstance().removeGameObject(gameObject, this.position);
    }
}