package scene.components;

import util.math.Vector3f;

public class ColorComponent implements Component {

    private final Vector3f color;
    public ColorComponent(Vector3f color) {
        this.color = color;
    }

    public Vector3f getColor() {
        return this.color;
    }
}
