package scene.components;

import util.math.Vector3f;

public class LightComponent extends Component {

    private final Vector3f color;
    private final Vector3f attenuation;

    public LightComponent(Vector3f color, Vector3f attenuation) {
        this.color = color;
        this.attenuation = attenuation;
    }

    public Vector3f getColor() {
        return this.color;
    }

    public Vector3f getAttenuation() {
        return this.attenuation;
    }
}
