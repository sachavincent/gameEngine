package items.roads;

import models.TexturedModel;
import textures.ModelTexture;

public class DirtRoadItem extends RoadItem {

    public DirtRoadItem() {
        super();

        this.straight = Models.straight;
        this.deadEnd = Models.deadEnd;
        this.turn = Models.turn;
        this.threeWay = Models.threeWay;
        this.fourWay = Models.fourWay;

        setPreviewTexture(this.straight);
    }

    public static class Models {
        static TexturedModel straight;
        static TexturedModel deadEnd;
        static TexturedModel turn;
        static TexturedModel threeWay;
        static TexturedModel fourWay;

        static {
            straight = new TexturedModel(StraightRoad.getModel(), new ModelTexture(StraightRoad.getName() + ".png", true));

            deadEnd = new TexturedModel(DeadEndRoad.getModel(), new ModelTexture(DeadEndRoad.getName() + ".png", true));

            turn = new TexturedModel(TurnRoad.getModel(), new ModelTexture(TurnRoad.getName() + ".png", true));

            threeWay = new TexturedModel(ThreeWayRoad.getModel(), new ModelTexture(ThreeWayRoad.getName() + ".png", true));

            fourWay = new TexturedModel(FourWayRoad.getModel(), new ModelTexture(FourWayRoad.getName() + ".png", true));
        }
    }
}
