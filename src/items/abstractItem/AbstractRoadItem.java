package items.abstractItem;

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
    public Item place(TerrainPosition position) {
        if (position == null)
            return null;

        Terrain terrain = Terrain.getInstance();

        if (terrain.isPositionOccupied(position)) {
            System.err.println("Position " + position + " occupied!");
            return null;
        }

        RoadItem itemInstance = (RoadItem) newInstance(position);
        boolean done = terrain.addItem(itemInstance.updateNeighboursAndCenter(position));
        terrain.getRoadGraph().addRoad(position);

        if (done)
            terrain.updateRequirements();

        System.out.println("placed " + this.item.getName() + " at " + position);

        return itemInstance;
    }

    @Override
    public void place(TerrainPosition[] positions) {
        if (positions == null || positions.length == 0)
            return;

        Terrain terrain = Terrain.getInstance();

        for (TerrainPosition position : positions) {
            if (terrain.isPositionOccupied(position)) {
                System.err.println("Position " + position + " occupied!");
                return;
            }
        }

        for (TerrainPosition position : positions) {
            RoadItem itemInstance = (RoadItem) newInstance(position);
            terrain.addItem(itemInstance.updateNeighboursAndCenter(position));
            terrain.getRoadGraph().addRoad(position);
        }

        terrain.updateRequirements();
    }
}
