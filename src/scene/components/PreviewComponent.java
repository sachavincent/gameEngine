package scene.components;

import models.TexturedModel;
import terrains.TerrainPosition;

public class PreviewComponent extends Component {

    private final TexturedModel previewTexture;

    private TerrainPosition previewPosition;

    public PreviewComponent(TexturedModel previewTexture) {
        this.previewTexture = previewTexture;
    }

    public TexturedModel getTexture() {
        return this.previewTexture;
    }

    public TerrainPosition getPreviewPosition() {
        return this.previewPosition;
    }

    public void setPreviewPosition(TerrainPosition previewPosition) {
        this.previewPosition = previewPosition;
    }
}
