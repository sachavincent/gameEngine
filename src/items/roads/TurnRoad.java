package items.roads;

import models.RawModel;
import renderEngine.OBJLoader;

public class TurnRoad implements RoadType {

    private final static String     name   = "turn";
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