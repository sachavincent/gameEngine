package scene.gameObjects;

import util.parsing.ModelType;
import util.parsing.objParser.ModelLoader;

public class GameObjectDatas {

    public static final GameObjectData INSULA = ModelLoader.loadModel("Insula", ModelType.INSTANCED);

    public static final GameObjectData MARKET = ModelLoader.loadModel("Market", ModelType.INSTANCED);

    public static final GameObjectData NPC = ModelLoader.loadModel("NPC", ModelType.INSTANCED);
//    public static final OBJGameObject BARREL = ModelLoader.loadModel("Barrel", ModelType.INSTANCED);

    public static final GameObjectData WHEAT_FARM = ModelLoader.loadModel("WheatFarm", ModelType.INSTANCED);

    public static final GameObjectData WINDMILL = ModelLoader.loadModel("Windmill", ModelType.ANIMATED_INSTANCED);

    public static final GameObjectData WHEATFIELD_FULL_FENCE         =
            ModelLoader.loadModel("WheatField", "WheatFieldFullFence", ModelType.INSTANCED);
    public static final GameObjectData WHEATFIELD_FENCE_POLE         =
            ModelLoader.loadModel("WheatField", "WheatFieldFencePole", ModelType.INSTANCED);
    public static final GameObjectData WHEATFIELD_CORNER_FENCE       =
            ModelLoader.loadModel("WheatField", "WheatFieldCornerFence", ModelType.INSTANCED);
    public static final GameObjectData WHEATFIELD_INTERSECTION_FENCE =
            ModelLoader.loadModel("WheatField", "WheatFieldInterFence", ModelType.INSTANCED);
    public static final GameObjectData WHEATFIELD_MIDDLE_FENCE       =
            ModelLoader.loadModel("WheatField", "WheatFieldMidFence", ModelType.INSTANCED);
    public static final GameObjectData WHEATFIELD_HALF_FENCE         =
            ModelLoader.loadModel("WheatField", "WheatFieldHalfFence", ModelType.INSTANCED);
    public static final GameObjectData WHEATFIELD_LAND_100P          =
            ModelLoader.loadModel("WheatField", "WheatFieldLand100%", ModelType.INSTANCED);
    public static final GameObjectData WHEATFIELD_LAND_66P           =
            ModelLoader.loadModel("WheatField", "WheatFieldLand66%", ModelType.INSTANCED);
    public static final GameObjectData WHEATFIELD_LAND_33P           =
            ModelLoader.loadModel("WheatField", "WheatFieldLand33%", ModelType.INSTANCED);
    public static final GameObjectData WHEATFIELD_LAND_0P            =
            ModelLoader.loadModel("WheatField", "WheatFieldLand0%", ModelType.INSTANCED);

    public static final GameObjectData DIRT_ROAD = ModelLoader.loadModel("DirtRoad", ModelType.INSTANCED);
//
//
//    private static GameObjectData loadRoad(String folder) {
//        GameObjectData gameObjectData = new GameObjectData();
//
//        File textureFile = new File(MODELS_PATH + "/" + folder + "/texture.png");
//        if (!textureFile.exists())
//            throw new MissingFileException(textureFile);
//        ModelTexture modelTexture = ModelTexture.createTexture(textureFile);
//        Material dirtRoadMaterial = new Material(folder);
//        dirtRoadMaterial.setDiffuseMap(modelTexture);
//        dirtRoadMaterial.setDiffuse(new SimpleMaterialColor(Color.RED));
//        Float[] vertices = new Float[]{-1f, 0f, 1f, 1f, 0f, 1f, -1f, 0f, -1f, 1f, 0f, -1f};
//        Float[] textureCoords = new Float[]{0f, 0f, 1f, 0f, 0f, 1f, 1f, 1f};
//        Float[] normals = new Float[]{0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f};
//        Integer[] indices = new Integer[]{1, 2, 0, 1, 3, 2};
//        List<AttributeData<?>> roadAttributes = new ArrayList<>();
//        roadAttributes.add(new AttributeData<>(0, 3, vertices, DataType.FLOAT));
//        roadAttributes.add(new AttributeData<>(1, 2, textureCoords, DataType.FLOAT));
//        roadAttributes.add(new AttributeData<>(2, 3, normals, DataType.FLOAT));
//        roadAttributes.add(new MaterialIndicesAttribute(dirtRoadMaterial, indices));
//
//        List<AttributeData<?>> attributes = new ArrayList<>(roadAttributes);
//
//        attributes.add(new InstancedAttribute(6, 4, DataType.FLOAT, 4));
//        IndexData data = IndexData.createData(attributes);
//        Vao vao = Vao.createVao(data, data.getVaoType());
//        gameObjectData.setTexture(new SimpleModel(vao));
//        gameObjectData.setPreviewTexture(new SimpleModel(vao));
//        gameObjectData.setBoundingBox(new BoundingBox(vao));
//        return gameObjectData;
//    }
}