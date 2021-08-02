package scene.gameObjects;

import models.AbstractModel;
import models.BoundingBox;
import models.Model;

public class GameObjectData {

    private AbstractModel       texture;
    private AbstractModel previewTexture;

    private BoundingBox   boundingBox;

    public AbstractModel getTexture() {
        return this.texture;
    }

    public void setTexture(AbstractModel texture) {
        this.texture = texture;
    }

    public AbstractModel getPreviewTexture() {
        return this.previewTexture;
    }

    public void setPreviewTexture(AbstractModel previewTexture) {
        this.previewTexture = previewTexture;
    }

    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    public void setBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }
}
