package items;

import scene.gameObjects.OBJGameObject;
import util.parsing.ModelType;
import util.parsing.objParser.ModelLoader;

public class OBJGameObjects {

    public static final OBJGameObject INSULA = ModelLoader.loadModel("Insula", ModelType.INSTANCED);

    public static final OBJGameObject MARKET = ModelLoader.loadModel("Market", ModelType.INSTANCED_WITH_NORMAL_MAP);

    public static final OBJGameObject NPC = ModelLoader.loadModel("NPC", ModelType.INSTANCED);
//    public static final OBJGameObject BARREL = ModelLoader.loadModel("Barrel", ModelType.INSTANCED);

    public static final OBJGameObject WHEAT_FARM = ModelLoader.loadModel("WheatFarm", ModelType.INSTANCED);
//
//    public static final OBJGameObject WINDMILL = ModelLoader.loadModel("Windmill", ModelType.ANIMATED);

    public static final OBJGameObject WHEATFIELD_FULL_FENCE         = WHEAT_FARM;
    //            ModelLoader.loadModel("WheatField", "WheatFieldFullFence", "texture", ModelType.INSTANCED);
    public static final OBJGameObject WHEATFIELD_FENCE_POLE         = WHEAT_FARM;
    //            ModelLoader.loadModel("WheatField", "WheatFieldFencePole", "texture", ModelType.INSTANCED);
    public static final OBJGameObject WHEATFIELD_CORNER_FENCE       = WHEAT_FARM;
    //            ModelLoader.loadModel("WheatField", "WheatFieldCornerFence", "texture", ModelType.INSTANCED);
    public static final OBJGameObject WHEATFIELD_INTERSECTION_FENCE = WHEAT_FARM;
    //            ModelLoader.loadModel("WheatField", "WheatFieldInterFence", "texture", ModelType.INSTANCED);
    public static final OBJGameObject WHEATFIELD_MIDDLE_FENCE       = WHEAT_FARM;
    //            ModelLoader.loadModel("WheatField", "WheatFieldMidFence", "texture", ModelType.INSTANCED);
    public static final OBJGameObject WHEATFIELD_HALF_FENCE         = WHEAT_FARM;
    //            ModelLoader.loadModel("WheatField", "WheatFieldHalfFence", "texture", ModelType.INSTANCED);
    public static final OBJGameObject WHEATFIELD_LAND_100P          = WHEAT_FARM;
    //            ModelLoader.loadModel("WheatField", "WheatFieldLand100%", "texture", ModelType.INSTANCED);
    public static final OBJGameObject WHEATFIELD_LAND_66P           = WHEAT_FARM;
    //            ModelLoader.loadModel("WheatField", "WheatFieldLand66%", "texture", ModelType.INSTANCED);
    public static final OBJGameObject WHEATFIELD_LAND_33P           = WHEAT_FARM;
    //            ModelLoader.loadModel("WheatField", "WheatFieldLand33%", "texture", ModelType.INSTANCED);
    public static final OBJGameObject WHEATFIELD_LAND_0P            = WHEAT_FARM;
//            ModelLoader.loadModel("WheatField", "WheatFieldLand0%", "texture", ModelType.INSTANCED);
//
//    private final static Vao           ROAD_MODEL = ModelLoader.loadRoadModel();
//    public static final  OBJGameObject DIRT_ROAD  = loadRoad("DirtRoad");

//    private static OBJGameObject loadRoad(String folder) {
//        OBJGameObject objGameObject = new OBJGameObject();
//
//        File textureFile = new File(MODELS_PATH + "/" + folder + "/texture.png");
//        if (!textureFile.exists())
//            throw new MissingFileException(textureFile);
//
//        ModelTexture modelTexture = ModelTexture.createTexture(textureFile);
//        objGameObject.setTexture(new Model(ROAD_MODEL, modelTexture));
//        objGameObject.setPreviewTexture(new Model(ROAD_MODEL, modelTexture));
//        objGameObject.setBoundingBox(new BoundingBox(ROAD_MODEL));
//        return objGameObject;
//    }
}