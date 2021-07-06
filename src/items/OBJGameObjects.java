package items;

import models.BoundingBox;
import models.TexturedModel;
import renderEngine.ModelLoader;
import scene.gameObjects.OBJGameObject;
import textures.ModelTexture;
import util.ModelType;
import util.Vao;

public class OBJGameObjects {

    private final static Vao ROAD_MODEL = ModelLoader.loadRoadModel();

    public static final OBJGameObject INSULA = ModelLoader.loadModel("Insula", ModelType.INSTANCED_WITH_NORMAL_MAP);

    public static final OBJGameObject WHEAT_FARM = ModelLoader.loadModel("WheatFarm", ModelType.INSTANCED);

    public static final OBJGameObject NPC  = ModelLoader.loadModel("NPC", ModelType.INSTANCED);
    public static final OBJGameObject TEST = ModelLoader.loadModel("test", ModelType.INSTANCED);

    static {
        TEST.getTexture().setModelTexture(new ModelTexture("#B71C1C"));
        TEST.getTexture().getModelTexture().setAlpha(0.6f);
    }

    public static final OBJGameObject MARKET = ModelLoader.loadModel("Market", ModelType.INSTANCED);

    public static final OBJGameObject WINDMILL = ModelLoader
            .loadModel("testing.dae", "Windmill.png", ModelType.ANIMATED);
//    public static final OBJGameObject WINDMILL = ModelLoader.loadModel("testing", "Windmill", ModelType.NORMAL);

    public static final OBJGameObject WHEATFIELD_FULL_FENCE         =
            ModelLoader.loadModel("WheatFieldFullFence", "WheatField", ModelType.INSTANCED);
    public static final OBJGameObject WHEATFIELD_FENCE_POLE         =
            ModelLoader.loadModel("WheatFieldFencePole", "WheatField", ModelType.INSTANCED);
    public static final OBJGameObject WHEATFIELD_CORNER_FENCE       =
            ModelLoader.loadModel("WheatFieldCornerFence", "WheatField", ModelType.INSTANCED);
    public static final OBJGameObject WHEATFIELD_INTERSECTION_FENCE =
            ModelLoader.loadModel("WheatFieldInterFence", "WheatField", ModelType.INSTANCED);
    public static final OBJGameObject WHEATFIELD_MIDDLE_FENCE       =
            ModelLoader.loadModel("WheatFieldMidFence", "WheatField", ModelType.INSTANCED);
    public static final OBJGameObject WHEATFIELD_HALF_FENCE         =
            ModelLoader.loadModel("WheatFieldHalfFence", "WheatField", ModelType.INSTANCED);
    public static final OBJGameObject WHEATFIELD_LAND_100P          =
            ModelLoader.loadModel("WheatFieldLand100%", "WheatField", ModelType.INSTANCED);
    public static final OBJGameObject WHEATFIELD_LAND_66P           =
            ModelLoader.loadModel("WheatFieldLand66%", "WheatField", ModelType.INSTANCED);
    public static final OBJGameObject WHEATFIELD_LAND_33P           =
            ModelLoader.loadModel("WheatFieldLand33%", "WheatField", ModelType.INSTANCED);
    public static final OBJGameObject WHEATFIELD_LAND_0P            =
            ModelLoader.loadModel("WheatFieldLand0%", "WheatField", ModelType.INSTANCED);

    public static final OBJGameObject DIRT_ROAD = loadRoad("Dirt Road");

    private static OBJGameObject loadRoad(String roadName) {
        OBJGameObject objGameObject = new OBJGameObject();

        ModelTexture modelTexture = new ModelTexture(roadName.replace(" ", "_") + ".png");
        objGameObject.setTexture(new TexturedModel(ROAD_MODEL, modelTexture));
        objGameObject.setPreviewTexture(new TexturedModel(ROAD_MODEL, modelTexture));
        objGameObject.setBoundingBox(new BoundingBox(ROAD_MODEL));
        return objGameObject;
    }
}