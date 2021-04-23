package scene.components;

import models.BoundingBox;

public class BoundingBoxComponent implements Component {

    private final BoundingBox boundingBox;

    public BoundingBoxComponent(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }
}
