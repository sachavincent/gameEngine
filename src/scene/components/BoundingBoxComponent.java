package scene.components;

import models.BoundingBox;

public class BoundingBoxComponent extends Component {

    private final BoundingBox boundingBox;

    public BoundingBoxComponent(BoundingBox boundingBox) {
        this.boundingBox = boundingBox == null ? null : new BoundingBox(boundingBox);
    }

    public BoundingBoxComponent(BoundingBox boundingBox, DirectionComponent directionComponent) {
        this(boundingBox);

        if (boundingBox != null) {
            this.boundingBox.getPlanes()
                    .forEach(plane3D -> plane3D.rotate((directionComponent.getDirection().getDegree())));

            directionComponent.setOnUpdateComponentCallback(gameObject -> this.boundingBox.getPlanes()
                    .forEach(plane3D -> {
                        plane3D.rotate(directionComponent.getDirection().getDegree());
                    }));
        }
    }

    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }
}
