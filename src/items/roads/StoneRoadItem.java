package items.roads;

import models.TexturedModel;
import terrains.TerrainPosition;
import textures.ModelTexture;

public class StoneRoadItem extends RoadItem {

    private final static String NAME = "StoneRoad";

    public final static TexturedModel TEXTURE = new TexturedModel(RoadItem.ROAD_MODEL,
            new ModelTexture(NAME.replace(" ", "_") + ".png", true));

    public final static TexturedModel PREVIEW_TEXTURE = new TexturedModel(RoadItem.ROAD_MODEL,
            new ModelTexture(NAME.replace(" ", "_") + ".png", true));

    public StoneRoadItem(TerrainPosition terrainPosition) {
        super(terrainPosition, NAME, TEXTURE, PREVIEW_TEXTURE);
    }
}
