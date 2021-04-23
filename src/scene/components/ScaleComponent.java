package scene.components;

public class ScaleComponent implements Component {

    protected float scale;

    public ScaleComponent(float scale) {
        this.scale = scale;
    }

    public ScaleComponent() {
        this.scale = 1;
    }

    public float getScale() {
        return this.scale;
    }

}
