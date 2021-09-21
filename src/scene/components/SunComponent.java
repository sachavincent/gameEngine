package scene.components;

import display.DisplayManager;
import scene.gameObjects.Light;
import util.DayNightCycle;
import util.math.Vector3f;

public class SunComponent extends Component {

    private final Vector3f center;
    private final float distance;

    private double movingAngle;

    public SunComponent(Vector3f center, float distance) {
        this.center = center;
        this.distance = distance;
        this.movingAngle = DayNightCycle.NOON;
        this.setOnFrameRenderedCallback((gameObject, nbFrames) -> {
            moveSun((Light.Sun) gameObject);
            return true;
        });
    }

    private void moveSun(Light.Sun sun) {
        if (!sun.isPlaced())
            return;

        Vector3f position = sun.getPosition();
        double angle = (-2.0 * Math.PI / ((double) DisplayManager.CURRENT_FPS *
                (double) DayNightCycle.DAY_NIGHT_CYCLE_DURATION));
        this.movingAngle = (this.movingAngle + angle) % (2.0 * Math.PI);
        position.setY((float) (this.center.getY() + Math.cos(this.movingAngle) * this.distance));
        position.setZ((float) (this.center.getZ() + Math.sin(this.movingAngle) * this.distance));
        sun.placeAt(position);
    }

    public Vector3f getCenter() {
        return this.center;
    }

    public float getDistance() {
        return this.distance;
    }
}