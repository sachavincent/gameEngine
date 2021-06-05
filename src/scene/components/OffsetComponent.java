package scene.components;

import util.math.Vector3f;

public class OffsetComponent extends Component {

    protected Vector3f offset; // Used internally to display roads and NPCs

    public OffsetComponent(Vector3f offset) {
        this.offset = offset;
    }

    public Vector3f getOffset() {
        return this.offset;
    }
}