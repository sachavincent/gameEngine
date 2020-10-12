package abstractItem;

import guis.presets.GuiBackground;
import items.Item;
import terrains.Terrain;
import util.math.Vector2f;

public abstract class AbstractItem {

    private final GuiBackground<?> modelTexture;
    private final Item             item;

    public AbstractItem(GuiBackground<?> modelTexture, Item item) {
        this.modelTexture = modelTexture;
        this.item = item;
    }

    public void place(Vector2f position) {
        if (position == null)
            return;

        Terrain terrain = Terrain.getInstance();

        boolean done = terrain.putItemIfSpace(position, getItemInstance());

        if (done)
            terrain.updateRequirements();
        else
            System.err.println("Not enough space for " + toString());
    }

    public void place(Vector2f[] positions) {
//        if (positions == null)
//            return;
//
//        for (Vector2f position : positions)
//            place(position);
        if (positions == null)
            return;

        Terrain terrain = Terrain.getInstance();
        for (Vector2f position : positions)
            terrain.putItemIfSpace(position, getItemInstance());

        terrain.updateRequirements();
    }

    public Item getPreviewItem() {
        return this.item;
    }

    public abstract Item getItemInstance();

    public GuiBackground<?> getBackground() {
        return this.modelTexture;
    }
}
