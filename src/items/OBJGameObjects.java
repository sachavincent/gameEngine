package items;

import models.RawModel;
import models.TexturedModel;
import renderEngine.OBJLoader;
import scene.gameObjects.OBJGameObject;
import textures.ModelTexture;

public class OBJGameObjects {

    private final static RawModel ROAD_MODEL = OBJLoader.loadRoadModel();

    public static final OBJGameObject INSULA = OBJLoader.loadOBJGameObject("Insula", true, true);

    public static final OBJGameObject NPC = OBJLoader.loadOBJGameObject("NPC", false, false);

    public static final OBJGameObject MARKET = OBJLoader.loadOBJGameObject("Market", true, true);

    public static final OBJGameObject DIRT_ROAD = loadRoad("Dirt Road");

    private static OBJGameObject loadRoad(String roadName) {
        OBJGameObject objGameObject = new OBJGameObject();

        ModelTexture modelTexture = new ModelTexture(roadName.replace(" ", "_") + ".png", true);
        objGameObject.setTexture(new TexturedModel(ROAD_MODEL, modelTexture));
        objGameObject.setPreviewTexture(new TexturedModel(ROAD_MODEL, modelTexture));

        return objGameObject;
    }
}
