package scene.gameObjects;

import org.lwjgl.glfw.GLFW;
import pathfinding.Road;
import renderEngine.BuildingRenderer;
import scene.Scene;
import scene.components.*;
import scene.components.callbacks.AddComponentCallback;
import util.math.Vector3f;

public class Market extends GameObject {

    public static final int X_POSITIVE_OFFSET = 4;
    public static final int X_NEGATIVE_OFFSET = 4;
    public static final int Z_POSITIVE_OFFSET = 4;
    public static final int Z_NEGATIVE_OFFSET = 4;

    public Market() {
        addComponent(new IconComponent(GameObjectPreviews.MARKET));
        DirectionComponent directionComponent = new DirectionComponent();
        addComponent(directionComponent);
        addComponent(new SingleModelComponent(GameObjectDatas.MARKET.getTexture()));
        addComponent(new PreviewComponent(GameObjectDatas.MARKET.getPreviewTexture()));
        addComponent(new OffsetsComponent(Z_NEGATIVE_OFFSET, X_POSITIVE_OFFSET, Z_POSITIVE_OFFSET, X_NEGATIVE_OFFSET));
        addComponent(new ConnectionsComponent<>(Road.class, new AddComponentCallback() {
            @Override
            public void onAddComponent(GameObject gameObject, Vector3f position) {
                Scene.getInstance().updateBuildingRequirements();
            }

            @Override
            public boolean isForEach() {
                return false;
            }
        }));

        addComponent(new BoundingBoxComponent(GameObjectDatas.MARKET.getBoundingBox(), directionComponent));
        addComponent(new SelectableComponent(button -> {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
            }
            return false;
        }));
        addComponent(new RendererComponent(BuildingRenderer.getInstance()));
    }
}