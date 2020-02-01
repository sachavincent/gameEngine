package items;

public class DirtRoadItem<Type extends RoadType> extends RoadItem {

    private Type roadType;

    public DirtRoadItem(Type type) {
        this.roadType = type;
    }


}
