package items;

import models.RawModel;
import models.TexturedModel;
import renderEngine.OBJLoader;
import scene.gameObjects.OBJGameObject;
import textures.ModelTexture;

public class OBJGameObjects {

    private final static RawModel ROAD_MODEL = OBJLoader.loadRoadModel();

    public static final OBJGameObject INSULA     = OBJLoader.loadOBJGameObject("Insula", true);
    public static final OBJGameObject WHEAT_FARM = OBJLoader.loadOBJGameObject("WheatFarm", true);

    public static final OBJGameObject NPC  = OBJLoader.loadOBJGameObject("NPC", false);
    public static final OBJGameObject TEST = OBJLoader.loadOBJGameObject("test", false);

    static {
        TEST.getTexture().setModelTexture(new ModelTexture("#B71C1C"));
        TEST.getTexture().getModelTexture().setAlpha(0.6f);
    }

    public static final OBJGameObject MARKET = OBJLoader.loadOBJGameObject("Market", true);

    public static final OBJGameObject WHEATFIELD_FULL_FENCE = OBJLoader
            .loadOBJGameObject("WheatFieldFullFence", "WheatField", false);
    public static final OBJGameObject WHEATFIELD_FENCE_POLE       = OBJLoader
            .loadOBJGameObject("WheatFieldFencePole", "WheatField", false);
    public static final OBJGameObject WHEATFIELD_CORNER_FENCE       = OBJLoader
            .loadOBJGameObject("WheatFieldCornerFence", "WheatField", false);
    public static final OBJGameObject WHEATFIELD_INTERSECTION_FENCE = OBJLoader
            .loadOBJGameObject("WheatFieldInterFence", "WheatField", false);
    public static final OBJGameObject WHEATFIELD_MIDDLE_FENCE = OBJLoader
            .loadOBJGameObject("WheatFieldMidFence", "WheatField", false);
    public static final OBJGameObject WHEATFIELD_HALF_FENCE = OBJLoader
            .loadOBJGameObject("WheatFieldHalfFence", "WheatField", false);
    public static final OBJGameObject WHEATFIELD_LAND_100P = OBJLoader
            .loadOBJGameObject("WheatFieldLand100%", "WheatField", true);
    public static final OBJGameObject WHEATFIELD_LAND_66P = OBJLoader
            .loadOBJGameObject("WheatFieldLand66%", "WheatField", false);
    public static final OBJGameObject WHEATFIELD_LAND_33P = OBJLoader
            .loadOBJGameObject("WheatFieldLand33%", "WheatField", false);
    public static final OBJGameObject WHEATFIELD_LAND_0P   = OBJLoader
            .loadOBJGameObject("WheatFieldLand0%", "WheatField", false);

    public static final OBJGameObject DIRT_ROAD = loadRoad("Dirt Road");

    private static OBJGameObject loadRoad(String roadName) {
        OBJGameObject objGameObject = new OBJGameObject();

        ModelTexture modelTexture = new ModelTexture(roadName.replace(" ", "_") + ".png");
        objGameObject.setTexture(new TexturedModel(ROAD_MODEL, modelTexture));
        objGameObject.setPreviewTexture(new TexturedModel(ROAD_MODEL, modelTexture));
//        objGameObject.setBoundingBox(new BoundingBox(ROAD_MODEL));
        return objGameObject;
    }
}
