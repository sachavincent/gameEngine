package items.roads;

import models.TexturedModel;
import terrains.TerrainPosition;
import textures.ModelTexture;

public class DirtRoadItem extends RoadItem {

    public final static String NAME = "Dirt Road";

    public final static TexturedModel TEXTURE = new TexturedModel(RoadItem.ROAD_MODEL,
            new ModelTexture(NAME.replace(" ", "_") + ".png", true));

    public final static TexturedModel PREVIEW_TEXTURE = new TexturedModel(RoadItem.ROAD_MODEL,
            new ModelTexture(NAME.replace(" ", "_") + ".png", true));

    public DirtRoadItem(TerrainPosition terrainPosition) {
        super(terrainPosition, NAME, TEXTURE, PREVIEW_TEXTURE);
    }

    @Override
    public String toString() {
        return "DirtRoadItem{" +
                "id=" + id +
                ", terrainPosition=" + terrainPosition +
                '}';
    }
}
