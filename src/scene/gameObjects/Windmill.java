package scene.gameObjects;

import engineTester.Rome;
import org.lwjgl.glfw.GLFW;
import pathfinding.Road;
import renderEngine.AnimatedBuildingRenderer;
import resources.ResourceManager;
import scene.components.*;
import scene.components.callbacks.ObjectPlacedCallback;

public class Windmill extends GameObject {

    public static final int X_POSITIVE_OFFSET = 2;
    public static final int X_NEGATIVE_OFFSET = 2;
    public static final int Z_POSITIVE_OFFSET = 2;
    public static final int Z_NEGATIVE_OFFSET = 2;

    public Windmill() {
        addComponent(new IconComponent(GameObjectPreviews.WINDMILL));
        DirectionComponent directionComponent = new DirectionComponent();
        addComponent(directionComponent);
//        addComponent(new SingleModelComponent(new Model(OBJGameObjects.WINDMILL.getTexture())));
        addComponent(new AnimatedModelComponent(GameObjectDatas.WINDMILL.getTexture()));

        addComponent(new PreviewComponent(GameObjectDatas.WINDMILL.getPreviewTexture()));
        addComponent(new OffsetsComponent(Z_NEGATIVE_OFFSET, X_POSITIVE_OFFSET, Z_POSITIVE_OFFSET, X_NEGATIVE_OFFSET));
        addComponent(new ConnectionsComponent<>(Road.class, new ObjectPlacedCallback() {
            @Override
            public void onObjPlaced(GameObject gameObject) {
                Rome.getGame().getScene().updateBuildingRequirements();
            }

            @Override
            public boolean isForEach() {
                return false;
            }
        }));


        addComponent(new BoundingBoxComponent(GameObjectDatas.WINDMILL.getBoundingBox(), directionComponent));
        addComponent(new SelectableComponent(button -> {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
                System.out.println("Selecting windmill");
            }
            return false;
        }));
        ResourceTransformationComponent resourceTransformationComponent = new ResourceTransformationComponent();
        resourceTransformationComponent.addResourceTransformation(
                new ResourceTransformationComponent.ResourceTransformation(ResourceManager.Resource.WHEAT, ResourceManager.Resource.BREAD, 2));
        addComponent(resourceTransformationComponent);

//        addComponent(new RendererComponent(BuildingRenderer.getInstance()));
        addComponent(new RendererComponent(AnimatedBuildingRenderer.getInstance()));
    }
}