package scene.gameObjects;

import renderEngine.BuildingRenderer;
import scene.components.*;

public class Test extends GameObject {

    public final static int X_POSITIVE_OFFSET = 1;
    public final static int X_NEGATIVE_OFFSET = 6;
    public final static int Z_POSITIVE_OFFSET = 1;
    public final static int Z_NEGATIVE_OFFSET = 1;

    public Test() {
        addComponent(new OffsetsComponent(Z_NEGATIVE_OFFSET, X_POSITIVE_OFFSET, Z_POSITIVE_OFFSET, X_NEGATIVE_OFFSET));
        addComponent(new IconComponent(GameObjectPreviews.INSULA));
        addComponent(new ScaleComponent(1));
        addComponent(new DirectionComponent());
        addComponent(new SingleModelComponent(GameObjectDatas.INSULA.getTexture()));
        addComponent(new PreviewComponent(GameObjectDatas.INSULA.getPreviewTexture()));

//        addComponent(new OffsetComponent(new Vector3f(.5f, 0, .5f)));

        addComponent(new RendererComponent(BuildingRenderer.getInstance()));
    }
}