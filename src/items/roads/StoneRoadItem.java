package items.roads;

import terrains.TerrainPosition;

public class StoneRoadItem extends RoadItem {

    private final static String NAME = "StoneRoad";

    public StoneRoadItem(TerrainPosition terrainPosition) {
        super(terrainPosition, NAME);
    }
}
