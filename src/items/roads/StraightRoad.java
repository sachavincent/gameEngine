package items.roads;

import models.RawModel;
import renderEngine.OBJLoader;

public class StraightRoad implements RoadType {

    private final static String     name   = "straight";
    private final static RawModel[] models = OBJLoader.loadRoadModel(name);

    @Override
    public RawModel[] getModels() {
        return models;
    }

    @Override
    public String getName() {
        return name;
    }
}
