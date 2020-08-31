package items.roads;

import models.RawModel;
import models.TexturedModel;
import textures.ModelTexture;

public class DirtRoadItem extends RoadItem {

    public final static String NAME = "Dirt Road";

    public DirtRoadItem() {
        super(NAME);

        this.straight = Models.straight;
        this.deadEnd = Models.deadEnd;
        this.turn = Models.turn;
        this.threeWay = Models.threeWay;
        this.fourWay = Models.fourWay;

        setPreviewTexture(this.straight[0]);
    }

    public static class Models {

        static TexturedModel[] straight = initModels(new StraightRoad());
        static TexturedModel[] deadEnd  = initModels(new DeadEndRoad());
        static TexturedModel[] turn     = initModels(new TurnRoad());
        static TexturedModel[] threeWay = initModels(new ThreeWayRoad());
        static TexturedModel[] fourWay  = initModels(new FourWayRoad());


        static TexturedModel[] initModels(RoadType roadType) {
            TexturedModel[] models = new TexturedModel[4];
            int i = 0;
            for (RawModel rawModel : roadType.getModels())
                models[i++] = new TexturedModel(rawModel,
                        new ModelTexture(PATH + roadType.getName() + ".png", true));

            return models;
        }
    }
}
