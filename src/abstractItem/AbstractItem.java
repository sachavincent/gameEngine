package abstractItem;

import guis.presets.Background;
import items.Item;
import terrains.Terrain;
import terrains.TerrainPosition;

public abstract class AbstractItem {

    private final Background<?> modelTexture;
    private final Item          item;

    public AbstractItem(Background<?> modelTexture, Item item) {
        this.modelTexture = modelTexture;
        this.item = item;
    }

    public void place(TerrainPosition position) {
        if (position == null)
            return;

        Terrain terrain = Terrain.getInstance();

        boolean done = terrain.putItemIfSpace(position, newInstance(position));

        if (done)
            terrain.updateRequirements();
        else
            System.err.println("Not enough space for " + toString());
    }

    public void place(TerrainPosition[] positions) {
//        if (positions == null)
//            return;
//
//        for (Vector2f position : positions)
//            place(position);
        if (positions == null)
            return;

        Terrain terrain = Terrain.getInstance();
        for (TerrainPosition position : positions)
            terrain.putItemIfSpace(position, newInstance(position));

        terrain.updateRequirements();
    }

    public Item getPreviewItem() {
        return this.item;
    }

    public abstract Item newInstance(TerrainPosition position);

    public Background<?> getBackground() {
        return this.modelTexture;
    }
}
