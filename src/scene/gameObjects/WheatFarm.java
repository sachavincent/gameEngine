package scene.gameObjects;

import static scene.gameObjects.WheatField.TEXTURES;

import entities.Camera.Direction;
import entities.Model;
import items.GameObjectPreviews;
import items.OBJGameObjects;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import models.TexturedModel;
import renderEngine.BuildingRenderer;
import resources.ResourceManager.Resource;
import scene.components.*;
import util.ShiftingList;
import util.math.Vector3f;

public class WheatFarm extends GameObject {

    public final static int X_POSITIVE_OFFSET = 3;
    public final static int X_NEGATIVE_OFFSET = 3;
    public final static int Z_POSITIVE_OFFSET = 3;
    public final static int Z_NEGATIVE_OFFSET = 3;

    public final static double WHEAT_PRODUCTION_PER_WHEATFIELD = 0.01;

    private final Random random = new Random();

    public WheatFarm() {
        addComponent(new IconComponent(GameObjectPreviews.INSULA));
        DirectionComponent directionComponent = new DirectionComponent(Direction.NORTH);
        addComponent(directionComponent);
        MultipleModelsComponent modelsComponent = new MultipleModelsComponent();
        modelsComponent.addConcurrentModel("FARM", new Model(OBJGameObjects.WHEAT_FARM.getTexture()));
        Model cornerModel1 = new Model(new Vector3f(-2, 0, -2), new Vector3f(0, 0, 0), 0, TEXTURES.get(0));
        Model cornerModel2 = new Model(new Vector3f(0, 0, -2), new Vector3f(0, 0, 0), 0, TEXTURES.get(0));
        Model cornerModel3 = new Model(new Vector3f(-2, 0, 0), new Vector3f(0, 0, 0), 0, TEXTURES.get(0));
        Model cornerModel4 = new Model(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 0, TEXTURES.get(0));
        modelsComponent.addConcurrentModel("CORNER1", cornerModel1);
        modelsComponent.addConcurrentModel("CORNER2", cornerModel2);
        modelsComponent.addConcurrentModel("CORNER3", cornerModel3);
        modelsComponent.addConcurrentModel("CORNER4", cornerModel4);

        modelsComponent.setOnAddComponentCallback((gameObject, position) -> {
            this.random.setSeed(System.currentTimeMillis());

        });
        AtomicInteger waitTicks = new AtomicInteger();
        waitTicks.set(this.random.nextInt(200));
        AtomicInteger nextCorner = new AtomicInteger();
        nextCorner.set(this.random.nextInt(4));
        List<ShiftingList<TexturedModel>> shiftingTextures = new ArrayList<>() {{
            add(new ShiftingList<>(TEXTURES));
            add(new ShiftingList<>(TEXTURES));
            add(new ShiftingList<>(TEXTURES));
            add(new ShiftingList<>(TEXTURES));
        }};
        modelsComponent.setOnTickElapsedCallback((gameObject, nbTicks) -> {
            if (waitTicks.get() == nbTicks) {
                waitTicks.set(this.random.nextInt(200));
                modelsComponent.getModelFromName("CORNER" + (nextCorner.get() + 1))
                        .setTexturedModel(shiftingTextures.get(nextCorner.get()).shiftAndGet());
                nextCorner.set(this.random.nextInt(4));

                return true;
            }

            return false;
        });

        addComponent(modelsComponent);
        addComponent(new PreviewComponent(OBJGameObjects.WHEAT_FARM.getPreviewTexture()));
        addComponent(new OffsetsComponent(Z_NEGATIVE_OFFSET, X_POSITIVE_OFFSET, Z_POSITIVE_OFFSET, X_NEGATIVE_OFFSET));

        ProductionComponent productionComponent = new ProductionComponent();
        productionComponent.addResource(Resource.WHEAT, WHEAT_PRODUCTION_PER_WHEATFIELD * 4);
        addComponent(productionComponent);
        addComponent(new BoundingBoxComponent(OBJGameObjects.WHEAT_FARM.getBoundingBox(), directionComponent));
        addComponent(new SelectableComponent(button -> {
        }));
        addComponent(new RendererComponent(BuildingRenderer.getInstance()));
    }
}