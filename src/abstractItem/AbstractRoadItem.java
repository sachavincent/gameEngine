package abstractItem;

import guis.presets.Background;
import items.Item;
import items.roads.RoadItem;
import terrains.Terrain;
import terrains.TerrainPosition;

public abstract class AbstractRoadItem extends AbstractItem {

    public AbstractRoadItem(Background<?> modelTexture, Item item) {
        super(modelTexture, item);
    }

    @Override
    public void place(TerrainPosition position) {
        if (position == null)
            return;

        Terrain terrain = Terrain.getInstance();

        if (terrain.isPositionOccupied(position))
            return;

        RoadItem itemInstance = (RoadItem) newInstance(position);
        boolean done = terrain.addItem(position, itemInstance.updateNeighboursAndCenter(position));
        terrain.updateRoadGraph();

        if (done)
            terrain.updateRequirements();
    }

    @Override
    public void place(TerrainPosition[] positions) {
        if (positions == null)
            return;

        Terrain terrain = Terrain.getInstance();

        for (TerrainPosition position : positions) {
            RoadItem itemInstance = (RoadItem) newInstance(position);

            terrain.addItem(position, itemInstance.updateNeighboursAndCenter(position));
        }
        terrain.updateRoadGraph();

        terrain.updateRequirements();
    }
}
