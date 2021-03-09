package items.abstractItem;

import guis.presets.Background;
import items.Item;
import terrains.Terrain;
import terrains.TerrainPosition;

public abstract class AbstractItem {

    private final Background<?> modelTexture;
    protected final Item          item;

    protected AbstractItem(Background<?> modelTexture, Item item) {
        this.modelTexture = modelTexture;
        this.item = item;
    }

    public Item place(TerrainPosition position) {
        if (position == null)
            return null;

        Terrain terrain = Terrain.getInstance();

        Item item = terrain.putItemIfSpace(this, position);

        if (item != null)
            terrain.updateRequirements();
        else
            System.err.println("Not enough space for " + toString());

        System.out.println("placed " + this.item.getName() + " at " + position);

        return item;
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
            terrain.putItemIfSpace(this, position);

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
