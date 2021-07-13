package scene.gameObjects;

import entities.Camera.Direction;
import entities.ModelEntity;
import items.GameObjectPreviews;
import items.OBJGameObjects;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import models.Model;
import org.lwjgl.glfw.GLFW;
import renderEngine.BuildingRenderer;
import resources.ResourceManager.Resource;
import scene.Scene;
import scene.components.*;
import scene.components.requirements.BuildingRadiusRequirement;
import scene.components.requirements.ResourceRequirementComponent;
import util.ShiftingList;
import util.Utils;
import util.math.Vector3f;

public class WheatField extends GameObject {

    public final static int X_POSITIVE_OFFSET = 1;
    public final static int X_NEGATIVE_OFFSET = 1;
    public final static int Z_POSITIVE_OFFSET = 1;
    public final static int Z_NEGATIVE_OFFSET = 1;

    private final static String LAND       = "LAND";
    private final static String FENCE      = "FENCE";
    private final static String FENCEPOLEC = "FENCEPOLEC";
    private final static String FENCEPOLE1 = "FENCEPOLE1";
    private final static String FENCEPOLE2 = "FENCEPOLE2";
    private final static String FENCEPOLE3 = "FENCEPOLE3";
    private final static String FENCEPOLE4 = "FENCEPOLE4";

    private final static String[] FENCEPOLES = new String[]{FENCEPOLE1, FENCEPOLE2, FENCEPOLE3, FENCEPOLE4};

    public final static List<Model> TEXTURES = new ArrayList<>();

    private final Random random = new Random();

    static {
        TEXTURES.add(OBJGameObjects.WHEATFIELD_LAND_0P.getTexture());
        TEXTURES.add(OBJGameObjects.WHEATFIELD_LAND_33P.getTexture());
        TEXTURES.add(OBJGameObjects.WHEATFIELD_LAND_66P.getTexture());
        TEXTURES.add(OBJGameObjects.WHEATFIELD_LAND_100P.getTexture());
    }

    public WheatField() {
        addComponent(new IconComponent(GameObjectPreviews.MARKET));
        DirectionComponent directionComponent = new DirectionComponent();
        addComponent(directionComponent);

        ShiftingList<Model> shiftingTextures = new ShiftingList<>(TEXTURES);

        MultipleModelsComponent modelsComponent = new MultipleModelsComponent();
        modelsComponent.addConcurrentModel(FENCE, OBJGameObjects.WHEATFIELD_FULL_FENCE.getTexture().toModelEntity());
        modelsComponent.addConcurrentModel(LAND, new ModelEntity(TEXTURES.get(0), true));

        modelsComponent.setOnAddComponentCallback((gameObject, position) -> {
            this.random.setSeed(System.currentTimeMillis());
        });
        AtomicInteger waitTicks = new AtomicInteger();
        waitTicks.set(this.random.nextInt(200));
        modelsComponent.setOnTickElapsedCallback((gameObject, nbTicks) -> {
            if (waitTicks.get() == nbTicks - 100) {
                waitTicks.set(this.random.nextInt(200));
                modelsComponent.getModelFromName(LAND).setModel(shiftingTextures.shiftAndGet());

                return true;
            }
            return false;
        });
        addComponent(modelsComponent);
        addComponent(new PreviewComponent(OBJGameObjects.WHEATFIELD_LAND_100P.getPreviewTexture()));
        addComponent(new OffsetsComponent(Z_NEGATIVE_OFFSET, X_POSITIVE_OFFSET, Z_POSITIVE_OFFSET, X_NEGATIVE_OFFSET));

        addComponent(
                new BoundingBoxComponent(OBJGameObjects.WHEATFIELD_FULL_FENCE.getBoundingBox(), directionComponent));
        addComponent(new SelectableComponent(button -> {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
            }
            return false;
        }));
        BuildingRadiusRequirement wheatFarmRequirement = new BuildingRadiusRequirement(WheatFarm.class, 15,
                value -> {
                    System.out.println("updating buildingradiusrequirement: " + value);
                });

        wheatFarmRequirement.setOnRequirementMetCallback(requirement -> {
            GameObject building = wheatFarmRequirement.getBuilding();
            if (building != null && !Scene.getInstance().isPreviewed(this.id)) {
                System.out
                        .println("Connected WheatField to " + building.getClass().getName() + " = " + building.getId());
                building.getComponent(ResourceProductionComponent.class)
                        .addToProductionRate(Resource.WHEAT, WheatFarm.WHEAT_PRODUCTION_PER_WHEATFIELD);
            }
        });

        ResourceRequirementComponent resourceRequirementComponent = new ResourceRequirementComponent(
                new HashSet<>(Collections.singletonList(wheatFarmRequirement)), new HashSet<>(), new HashSet<>(),
                new HashSet<>());
        addComponent(resourceRequirementComponent);

        ConnectionsComponent<WheatField> connectionsComponent = new ConnectionsComponent<>(WheatField.class,
                (gameObject, position) -> {

                });
        connectionsComponent.setOnUpdateComponentCallback(this::onUpdateConnection);
        addComponent(connectionsComponent);
        addComponent(new RendererComponent(BuildingRenderer.getInstance()));
    }

    private void onUpdateConnection(GameObject gameObject) {
        if (gameObject == null)
            return;

        Scene scene = Scene.getInstance();
        MultipleModelsComponent multipleModelsComponent = gameObject.getComponent(MultipleModelsComponent.class);
        if (multipleModelsComponent == null)
            return;

        DirectionComponent directionComponent = gameObject.getComponent(DirectionComponent.class);
        if (directionComponent == null)
            return;

        ConnectionsComponent<?> connectionsComponent = gameObject.getComponent(ConnectionsComponent.class);
        GameObject southObject = scene.getGameObjectFromId(connectionsComponent.getConnection(Direction.SOUTH));
        GameObject northObject = scene.getGameObjectFromId(connectionsComponent.getConnection(Direction.NORTH));
        GameObject westObject = scene.getGameObjectFromId(connectionsComponent.getConnection(Direction.WEST));
        GameObject eastObject = scene.getGameObjectFromId(connectionsComponent.getConnection(Direction.EAST));

        boolean isEastObjectWheatField = eastObject != null;
        boolean isNorthObjectWheatField = northObject != null;
        boolean isWestObjectWheatField = westObject != null;
        boolean isSouthObjectWheatField = southObject != null;

        int connections = (isEastObjectWheatField ? 1 : 0) + (isNorthObjectWheatField ? 1 : 0) * 2 +
                (isWestObjectWheatField ? 1 : 0) * 4 + (isSouthObjectWheatField ? 1 : 0) * 8;

        multipleModelsComponent.removeConcurrentModel(FENCE);
        multipleModelsComponent.removeConcurrentModel(FENCEPOLEC);
        multipleModelsComponent.removeConcurrentModel(FENCEPOLE1);
        multipleModelsComponent.removeConcurrentModel(FENCEPOLE2);
        multipleModelsComponent.removeConcurrentModel(FENCEPOLE3);
        multipleModelsComponent.removeConcurrentModel(FENCEPOLE4);

        int[] perpendiculars = new int[]{90, -90};
        int nbConnections = Integer.bitCount(connections);
        Model fenceModel;
        Direction dir;
        switch (nbConnections) {
            case 1:
                fenceModel = OBJGameObjects.WHEATFIELD_HALF_FENCE.getTexture();
                Direction direction = Direction.values()[Utils.findPositionOf1(connections) - 1];
                directionComponent.setDirection(direction);
                break;
            case 2:
                if (connections == 10) {
                    directionComponent.setDirection(Direction.NORTH);
                    fenceModel = OBJGameObjects.WHEATFIELD_MIDDLE_FENCE.getTexture();
                } else if (connections == 5) {
                    directionComponent.setDirection(Direction.WEST);
                    fenceModel = OBJGameObjects.WHEATFIELD_MIDDLE_FENCE.getTexture();
                } else {
                    if (connections == 9)
                        directionComponent.setDirection(Direction.EAST);
                    else if (connections == 6)
                        directionComponent.setDirection(Direction.WEST);
                    else if (connections == 3)
                        directionComponent.setDirection(Direction.SOUTH);
                    else if (connections == 12)
                        directionComponent.setDirection(Direction.NORTH);

                    dir = directionComponent.getDirection();
                    if (dir == Direction.NORTH)
                        dir = Direction.SOUTH;
                    else if (dir == Direction.SOUTH)
                        dir = Direction.NORTH;

                    updateModelAtCorner(gameObject, FENCEPOLEC, dir,
                            Direction.getDirectionFromDegree(dir.getDegree() + 90), Direction.WEST);

                    fenceModel = OBJGameObjects.WHEATFIELD_CORNER_FENCE.getTexture();
                }
                break;
            case 3:
                if (connections == 7)
                    directionComponent.setDirection(Direction.SOUTH);
                else if (connections == 13)
                    directionComponent.setDirection(Direction.NORTH);
                else if (connections == 11)
                    directionComponent.setDirection(Direction.EAST);
                else if (connections == 14)
                    directionComponent.setDirection(Direction.WEST);

                dir = directionComponent.getDirection();
                if (dir == Direction.NORTH)
                    dir = Direction.SOUTH;
                else if (dir == Direction.SOUTH)
                    dir = Direction.NORTH;

                for (int i = 0; i < 2; i++)
                    updateModelAtCorner(gameObject, FENCEPOLES[i], dir, dir.add(perpendiculars[i]),
                            Direction.values()[i]);

                fenceModel = OBJGameObjects.WHEATFIELD_INTERSECTION_FENCE.getTexture();
                break;
            case 4:
                dir = directionComponent.getDirection();
                if (dir == Direction.NORTH)
                    dir = Direction.SOUTH;
                else if (dir == Direction.SOUTH)
                    dir = Direction.NORTH;

                Direction[] dirs = new Direction[]{dir, dir.toOppositeDirection()};
                for (int i = 0; i < 4; i++)
                    updateModelAtCorner(gameObject, FENCEPOLES[i], dirs[i / 2], dirs[i / 2].add(perpendiculars[i % 2]),
                            Direction.values()[i]);

                return;
            default:
                fenceModel = OBJGameObjects.WHEATFIELD_FULL_FENCE.getTexture();
                break;
        }

        multipleModelsComponent.addConcurrentModel(FENCE, fenceModel.toModelEntity());
    }

    /**
     * Updates model at corner of centerGameObject
     *
     * @param centerGameObject GameObject at the center
     * @param modelName name of the model
     * @param direction direction in which to look for neighbor
     * @param perpendicularDirection direction in which to look for corner from neighbor
     * @param offsetDirection direction of the model
     */
    private void updateModelAtCorner(GameObject centerGameObject, String modelName, Direction direction,
            Direction perpendicularDirection, Direction offsetDirection) {
        Scene scene = Scene.getInstance();
        MultipleModelsComponent multipleModelsComponent = centerGameObject.getComponent(MultipleModelsComponent.class);
        GameObject gameObjectAtCorner = scene
                .getNeighbor(scene.getNeighbor(centerGameObject, direction, WheatField.class::isInstance),
                        perpendicularDirection, WheatField.class::isInstance);
        if (gameObjectAtCorner == null) {
            multipleModelsComponent
                    .addConcurrentModel(modelName, OBJGameObjects.WHEATFIELD_FENCE_POLE.getTexture().toModelEntity());
            multipleModelsComponent.setOffsetsRotation(modelName, new Vector3f(0, offsetDirection.getDegree(), 0));
        } else {
            if (doesFenceNeedRemoval(
                    gameObjectAtCorner.getComponent(MultipleModelsComponent.class).getConcurrentModels())) {
                ConnectionsComponent<?> connectionsComponent = gameObjectAtCorner
                        .getComponent(ConnectionsComponent.class);
                if (!connectionsComponent.isUpdated()) {
                    centerGameObject.getComponent(ConnectionsComponent.class).setUpdated(true);
                    connectionsComponent.update();
                    connectionsComponent.setUpdated(true);
                }
            }
        }
    }

    private boolean doesFenceNeedRemoval(Map<String, ModelEntity> concurrentModels) {
        return concurrentModels.keySet().stream().anyMatch(
                name -> name.equals(FENCEPOLEC) || name.equals(FENCEPOLE1) || name.equals(FENCEPOLE2) ||
                        name.equals(FENCEPOLE3) || name.equals(FENCEPOLE4));
    }
}