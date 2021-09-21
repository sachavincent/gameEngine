package scene.gameObjects;

import static scene.gameObjects.WheatField.TEXTURES;

import entities.Camera.Direction;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import models.AbstractModel;
import renderEngine.BuildingRenderer;
import resources.ResourceManager.Resource;
import scene.components.*;
import util.Offset;
import util.ShiftingList;
import util.math.Vector3f;

public class WheatFarm extends GameObject {

    public static final int X_POSITIVE_OFFSET = 3;
    public static final int X_NEGATIVE_OFFSET = 3;
    public static final int Z_POSITIVE_OFFSET = 3;
    public static final int Z_NEGATIVE_OFFSET = 3;

    public static final double WHEAT_PRODUCTION_PER_WHEATFIELD = 0.01;

    private final Random random = new Random();

    public WheatFarm() {
        addComponent(new IconComponent(GameObjectPreviews.INSULA));
        DirectionComponent directionComponent = new DirectionComponent(Direction.NORTH);
        addComponent(directionComponent);
        MultipleModelsComponent modelsComponent = new MultipleModelsComponent();
        Offset offset1 = new Offset(new Vector3f(-2, 0.04f, -2), 0, 0);
        Offset offset2 = new Offset(new Vector3f(0, 0.04f, -2), 0, 0);
        Offset offset3 = new Offset(new Vector3f(-2, 0.04f, 0), 0, 0);
        Offset offset4 = new Offset(new Vector3f(0, 0.04f, 0), 0, 0);
        modelsComponent.addConcurrentModel("FARM", GameObjectDatas.WHEAT_FARM.getTexture());
        modelsComponent.addConcurrentModel("CORNER1", TEXTURES.get(0), offset1);
        modelsComponent.addConcurrentModel("CORNER2", TEXTURES.get(0), offset2);
        modelsComponent.addConcurrentModel("CORNER3", TEXTURES.get(0), offset3);
        modelsComponent.addConcurrentModel("CORNER4", TEXTURES.get(0), offset4);

        modelsComponent.setOnObjectPlacedCallback((gameObject) -> {
            this.random.setSeed(System.currentTimeMillis());
        });
        AtomicInteger waitTicks = new AtomicInteger();
        waitTicks.set(this.random.nextInt(200));
        AtomicInteger nextCorner = new AtomicInteger();
        nextCorner.set(this.random.nextInt(4));
        List<ShiftingList<AbstractModel>> shiftingTextures = new ArrayList<>() {{
            add(new ShiftingList<>(TEXTURES));
            add(new ShiftingList<>(TEXTURES));
            add(new ShiftingList<>(TEXTURES));
            add(new ShiftingList<>(TEXTURES));
        }};
        modelsComponent.setOnTickElapsedCallback((gameObject, nbTicks) -> {
            if (waitTicks.get() == nbTicks) {
                waitTicks.set(this.random.nextInt(200));
                modelsComponent.replaceConcurrentModel("CORNER" + (nextCorner.get() + 1),
                        shiftingTextures.get(nextCorner.get()).shiftAndGet());
                nextCorner.set(this.random.nextInt(4));

                return true;
            }

            return false;
        });

        addComponent(modelsComponent);
        addComponent(new PreviewComponent(GameObjectDatas.WHEAT_FARM.getPreviewTexture()));
        addComponent(new OffsetsComponent(Z_NEGATIVE_OFFSET, X_POSITIVE_OFFSET, Z_POSITIVE_OFFSET, X_NEGATIVE_OFFSET));

        ResourceProductionComponent resourceProductionComponent = new ResourceProductionComponent();
        resourceProductionComponent.addResource(Resource.WHEAT, WHEAT_PRODUCTION_PER_WHEATFIELD * 4);
        addComponent(resourceProductionComponent);
        addComponent(new BoundingBoxComponent(GameObjectDatas.WHEAT_FARM.getBoundingBox(), directionComponent));
        addComponent(new SelectableComponent(button -> {
            return false;
        }));
        addComponent(new RendererComponent(BuildingRenderer.getInstance()));
    }
}