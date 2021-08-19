package scene.gameObjects;

import static util.Utils.MODELS_PATH;

import java.io.File;
import models.BoundingBox;
import models.SimpleModel;
import renderEngine.MeshData;
import renderEngine.Vao;
import renderEngine.shaders.structs.Material;
import textures.ModelTexture;
import util.exceptions.MissingFileException;
import util.parsing.ModelType;
import util.parsing.objParser.ModelLoader;

public class GameObjectDatas {

    public static final GameObjectData INSULA = ModelLoader.loadModel("Insula", ModelType.DEFAULT);

    public static final GameObjectData MARKET = ModelLoader.loadModel("Market", ModelType.INSTANCED_WITH_NORMAL_MAP);

    public static final GameObjectData NPC = ModelLoader.loadModel("NPC", ModelType.INSTANCED);
//    public static final OBJGameObject BARREL = ModelLoader.loadModel("Barrel", ModelType.INSTANCED);

    public static final GameObjectData WHEAT_FARM = ModelLoader.loadModel("WheatFarm", ModelType.INSTANCED);

    public static final GameObjectData WINDMILL = ModelLoader.loadModel("Windmill", ModelType.ANIMATED_INSTANCED);

    public static final GameObjectData WHEATFIELD_FULL_FENCE =
            ModelLoader.loadModel("WheatField", "WheatFieldFullFence", ModelType.INSTANCED);
    public static final GameObjectData WHEATFIELD_FENCE_POLE =
            ModelLoader.loadModel("WheatField", "WheatFieldFencePole", ModelType.INSTANCED);
    public static final GameObjectData WHEATFIELD_CORNER_FENCE =
            ModelLoader.loadModel("WheatField", "WheatFieldCornerFence", ModelType.INSTANCED);
    public static final GameObjectData WHEATFIELD_INTERSECTION_FENCE =
            ModelLoader.loadModel("WheatField", "WheatFieldInterFence", ModelType.INSTANCED);
    public static final GameObjectData WHEATFIELD_MIDDLE_FENCE =
            ModelLoader.loadModel("WheatField", "WheatFieldMidFence", ModelType.INSTANCED);
    public static final GameObjectData WHEATFIELD_HALF_FENCE =
            ModelLoader.loadModel("WheatField", "WheatFieldHalfFence", ModelType.INSTANCED);
    public static final GameObjectData WHEATFIELD_LAND_100P =
            ModelLoader.loadModel("WheatField", "WheatFieldLand100%", ModelType.INSTANCED);
    public static final GameObjectData WHEATFIELD_LAND_66P =
            ModelLoader.loadModel("WheatField", "WheatFieldLand66%", ModelType.INSTANCED);
    public static final GameObjectData WHEATFIELD_LAND_33P =
            ModelLoader.loadModel("WheatField", "WheatFieldLand33%", ModelType.INSTANCED);
    public static final GameObjectData WHEATFIELD_LAND_0P =
            ModelLoader.loadModel("WheatField", "WheatFieldLand0%", ModelType.INSTANCED);

    private final static MeshData ROAD_MODEL = ModelLoader.loadRoadModel();

    public static final GameObjectData DIRT_ROAD = loadRoad("DirtRoad", ModelType.INSTANCED);

    private static GameObjectData loadRoad(String folder, ModelType modelType) {
        GameObjectData gameObjectData = new GameObjectData();

        File textureFile = new File(MODELS_PATH + "/" + folder + "/texture.png");
        if (!textureFile.exists())
            throw new MissingFileException(textureFile);
        ModelTexture modelTexture = ModelTexture.createTexture(textureFile);
        Material dirtRoadMaterial = new Material(folder);
        dirtRoadMaterial.setDiffuseMap(modelTexture);

        int[] indices = ROAD_MODEL.getIndicesList().get(new Material("TEMP"));
        ROAD_MODEL.getIndicesList().clear();
        ROAD_MODEL.getIndicesList().put(dirtRoadMaterial, indices);
        Vao vao = Vao.createVao(ROAD_MODEL, modelType);
        gameObjectData.setTexture(new SimpleModel(vao));
        gameObjectData.setPreviewTexture(new SimpleModel(vao));
        gameObjectData.setBoundingBox(new BoundingBox(vao));
        return gameObjectData;
    }
}