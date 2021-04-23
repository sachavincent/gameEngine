package scene.gameObjects;

import renderEngine.LightRenderer;
import scene.components.AttenuationComponent;
import scene.components.ColorComponent;
import scene.components.PositionComponent;
import scene.components.RendererComponent;
import util.math.Vector3f;

public class Light extends GameObject {

    public Light(Vector3f position, Vector3f color) {
        this(position, color, new Vector3f(1, 0, 0));
    }

    public Light(Vector3f position, Vector3f color, Vector3f attenuation) {
        addComponent(new PositionComponent(position));
        addComponent(new ColorComponent(color));
        addComponent(new AttenuationComponent(attenuation));

        addComponent(new RendererComponent(this, LightRenderer.getInstance()));
    }
}
