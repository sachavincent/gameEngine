package scene.components;

import models.BoundingBox;

public class BoundingBoxComponent extends Component {

    private final BoundingBox boundingBox;

    public BoundingBoxComponent(BoundingBox boundingBox) {
        this.boundingBox = new BoundingBox(boundingBox);
    }

    public BoundingBoxComponent(BoundingBox boundingBox, DirectionComponent directionComponent) {
        this(boundingBox);

        this.boundingBox.getPlanes()
                .forEach(plane3D -> plane3D.rotate((directionComponent.getDirection().getDegree())));

        directionComponent.updateComponentCallback = gameObject -> this.boundingBox.getPlanes()
                .forEach(plane3D -> {
                    plane3D.rotate(directionComponent.getDirection().getDegree());
                });
    }

    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }
}
