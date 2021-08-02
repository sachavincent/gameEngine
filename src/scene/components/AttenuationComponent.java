package scene.components;

import util.math.Vector3f;

public class AttenuationComponent extends Component {

    private final Vector3f attenuation;

    public AttenuationComponent(Vector3f attenuation) {
        this.attenuation = attenuation;
    }

    public Vector3f getAttenuation() {
        return this.attenuation;
    }
}
