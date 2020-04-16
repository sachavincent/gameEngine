package items.roads;

import models.RawModel;
import renderEngine.Loader;
import renderEngine.OBJLoader;

public class ThreeWayRoad implements RoadType {

    private final static RawModel model = OBJLoader.loadObjModel(getName(), Loader.getInstance());

    public static RawModel getModel() {
        return model;
    }

    public static String getName() {
        return "three_way";
    }
}
