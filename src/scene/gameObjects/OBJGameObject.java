package scene.gameObjects;

import models.BoundingBox;
import models.Model;

public class OBJGameObject {

    private Model       texture;
    private Model previewTexture;

    private BoundingBox   boundingBox;

    public Model getTexture() {
        return this.texture;
    }

    public void setTexture(Model texture) {
        this.texture = texture;
    }

    public Model getPreviewTexture() {
        return this.previewTexture;
    }

    public void setPreviewTexture(Model previewTexture) {
        this.previewTexture = previewTexture;
    }

    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    public void setBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }
}
