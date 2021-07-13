package scene.components;

import entities.ModelEntity;
import models.Model;
import renderEngine.BuildingRenderer;
import renderEngine.Renderer;
import util.math.Vector3f;

public class PreviewComponent extends Component {

    private final ModelEntity previewTexture;

    private Vector3f previewPosition;

    public PreviewComponent(Model previewTexture) {
        this(previewTexture.toModelEntity());
    }

    public PreviewComponent(ModelEntity previewTexture) {
        this.previewTexture = previewTexture;
        setOnUpdateComponentCallback(gameObject -> {
            Renderer renderer = gameObject.getComponent(RendererComponent.class).getRenderer();
            if (renderer instanceof BuildingRenderer) {
                ((BuildingRenderer) renderer).removeGameObject(gameObject);
            }
        });
    }

    public ModelEntity getTexture() {
        return this.previewTexture;
    }

    public Vector3f getPreviewPosition() {
        return this.previewPosition;
    }

    public void setPreviewPosition(Vector3f previewPosition) {
//        System.out.println("setting preview position: " + previewPosition);
        this.previewPosition = previewPosition;
        update();
    }
}
