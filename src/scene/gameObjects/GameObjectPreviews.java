package scene.gameObjects;

import static util.Utils.MODELS_PATH;

import guis.presets.Background;
import java.io.File;

public class GameObjectPreviews {

    public static final Background<?> INSULA     = new Background<>(new File(MODELS_PATH + "/Insula/preview.png"));
    public static final Background<?> MARKET     = new Background<>(new File(MODELS_PATH + "/Market/preview.png"));
    public static final Background<?> DIRT_ROAD  = new Background<>(new File(MODELS_PATH + "/DirtRoad/preview.png"));
    public static final Background<?> WHEAT_FARM = new Background<>(new File(MODELS_PATH + "/WheatFarm/preview.png"));
    public static final Background<?> WINDMILL   = new Background<>(new File(MODELS_PATH + "/Windmill/preview.png"));
}
