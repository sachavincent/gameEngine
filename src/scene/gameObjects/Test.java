package scene.gameObjects;

import entities.Model;
import items.GameObjectPreviews;
import items.OBJGameObjects;
import renderEngine.BuildingRenderer;
import scene.components.*;

public class Test extends GameObject {

    public final static int X_POSITIVE_OFFSET = 1;
    public final static int X_NEGATIVE_OFFSET = 6;
    public final static int Z_POSITIVE_OFFSET = 1;
    public final static int Z_NEGATIVE_OFFSET = 1;

    public Test() {
        addComponent(new OffsetsComponent(Z_NEGATIVE_OFFSET, X_POSITIVE_OFFSET, Z_POSITIVE_OFFSET, X_NEGATIVE_OFFSET));
        addComponent(new IconComponent(GameObjectPreviews.DIRT_ROAD));
        addComponent(new ScaleComponent(1f));
        addComponent(new DirectionComponent());
        addComponent(new SingleModelComponent(new Model(OBJGameObjects.TEST.getTexture())));
        addComponent(new PreviewComponent(OBJGameObjects.TEST.getPreviewTexture()));

//        addComponent(new OffsetComponent(new Vector3f(.5f, 0, .5f)));

        addComponent(new RendererComponent(BuildingRenderer.getInstance()));
    }
}