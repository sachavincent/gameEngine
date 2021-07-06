package scene.gameObjects;

import entities.Model;
import items.GameObjectPreviews;
import items.OBJGameObjects;
import org.lwjgl.glfw.GLFW;
import pathfinding.Road;
import renderEngine.AnimatedBuildingRenderer;
import scene.Scene;
import scene.components.*;
import scene.components.callbacks.AddComponentCallback;
import util.math.Vector3f;

public class Windmill extends GameObject {

    public final static int X_POSITIVE_OFFSET = 2;
    public final static int X_NEGATIVE_OFFSET = 2;
    public final static int Z_POSITIVE_OFFSET = 2;
    public final static int Z_NEGATIVE_OFFSET = 2;

    public Windmill() {
        addComponent(new IconComponent(GameObjectPreviews.WINDMILL));
        DirectionComponent directionComponent = new DirectionComponent();
        addComponent(directionComponent);
//        addComponent(new SingleModelComponent(new Model(OBJGameObjects.WINDMILL.getTexture())));
        addComponent(new AnimatedModelComponent(new Model(OBJGameObjects.WINDMILL.getTexture())));

        addComponent(new PreviewComponent(OBJGameObjects.WINDMILL.getPreviewTexture()));
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


        addComponent(new BoundingBoxComponent(OBJGameObjects.WINDMILL.getBoundingBox(), directionComponent));
        addComponent(new SelectableComponent(button -> {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
            }
        }));
//        addComponent(new RendererComponent(BuildingRenderer.getInstance()));
        addComponent(new RendererComponent(AnimatedBuildingRenderer.getInstance()));
    }
}