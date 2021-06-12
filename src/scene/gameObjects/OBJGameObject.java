package scene.gameObjects;

import models.BoundingBox;
import models.TexturedModel;

public class OBJGameObject {

    private TexturedModel texture;
    private TexturedModel previewTexture;

    private BoundingBox   boundingBox;

    public TexturedModel getTexture() {
        return this.texture;
    }

    public void setTexture(TexturedModel texture) {
        this.texture = texture;
    }

    public TexturedModel getPreviewTexture() {
        return this.previewTexture;
    }

    public void setPreviewTexture(TexturedModel previewTexture) {
        this.previewTexture = previewTexture;
    }

    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    public void setBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }
}
