package scene.gameObjects;

import items.GameObjectPreviews;
import items.OBJGameObjects;
import org.lwjgl.glfw.GLFW;
import renderEngine.BuildingRenderer;
import scene.Scene;
import scene.components.*;
import scene.components.callbacks.AddComponentCallback;
import util.math.Vector3f;

public class Market extends GameObject {

    public final static int X_POSITIVE_OFFSET = 4;
    public final static int X_NEGATIVE_OFFSET = 5;
    public final static int Z_POSITIVE_OFFSET = 4;
    public final static int Z_NEGATIVE_OFFSET = 5;

    public Market() {
        addComponent(new IconComponent(GameObjectPreviews.MARKET));
        DirectionComponent directionComponent = new DirectionComponent();
        addComponent(directionComponent);
        addComponent(new TextureComponent(OBJGameObjects.MARKET.getTexture()));
        addComponent(new PreviewComponent(OBJGameObjects.MARKET.getPreviewTexture()));
        addComponent(new OffsetsComponent(Z_NEGATIVE_OFFSET, X_POSITIVE_OFFSET, Z_POSITIVE_OFFSET, X_NEGATIVE_OFFSET));
        addComponent(new RoadConnectionsComponent(new AddComponentCallback() {
            @Override
            public void onAddComponent(GameObject gameObject, Vector3f position) {
                Scene.getInstance().updateBuildingRequirements();
            }

            @Override
            public boolean isForEach() {
                return false;
            }
        }));

        addComponent(new BoundingBoxComponent(OBJGameObjects.MARKET.getBoundingBox(), directionComponent));
        addComponent(new SelectableComponent(button -> {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
            }
        }));
        addComponent(new RendererComponent(BuildingRenderer.getInstance()));
    }
}