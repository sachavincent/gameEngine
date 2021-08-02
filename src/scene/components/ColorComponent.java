package scene.components;

import java.awt.Color;
import util.math.Vector3f;

public class ColorComponent extends Component {

    private final Vector3f color;

    public ColorComponent(Vector3f color) {
        this.color = color;
    }

    public ColorComponent(Color color) {
        this.color = new Vector3f(color.getRed(), color.getGreen(), color.getBlue());
    }

    public Vector3f getColor() {
        return this.color;
    }
}
