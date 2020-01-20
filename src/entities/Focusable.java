package entities;

import terrains.Terrain;
import util.math.Vector2f;
import util.math.Vector3f;

public abstract class Focusable {

    private Vector3f position;
    private double height;

    public Focusable(Terrain terrain, Vector3f position, double height) {
        float y = terrain.getHeightOfTerrain(position.x, position.z);
        this.position = new Vector3f(position.x, y, position.z);
        this.height = height;
    }

    public double getDistanceFromCamera(Camera camera) {
        return camera.getPosition().distance(position);
    }

    public double get2DDistanceFromCamera(Camera camera) {
        Vector2f cameraPos = new Vector2f(camera.getPosition().x, camera.getPosition().z);
        Vector2f focusPos = new Vector2f(position.x, position.z);

        return cameraPos.distance(focusPos);
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public double getHeight() {
        return this.height;
    }
}
