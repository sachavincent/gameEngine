package items.roads;

import terrains.TerrainPosition;

public class DirtRoadItem extends RoadItem {

    public final static String NAME = "Dirt Road";

    public DirtRoadItem(TerrainPosition terrainPosition) {
        super(terrainPosition, NAME);
    }
}
