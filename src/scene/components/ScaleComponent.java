package scene.components;

public class ScaleComponent extends Component {

    protected float scale;

    public ScaleComponent() {
        this(1f);
    }

    public ScaleComponent(float scale) {
        this.scale = scale;
    }

    public float getScale() {
        return this.scale;
    }

}
