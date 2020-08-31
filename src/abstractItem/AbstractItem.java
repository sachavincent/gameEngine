package abstractItem;

import guis.presets.GuiBackground;
import items.Item;
import terrains.Terrain;
import util.math.Vector2f;

public abstract class AbstractItem {

    private GuiBackground<?> modelTexture;
    private Item             item;

    public AbstractItem(GuiBackground<?> modelTexture, Item item) {
        this.modelTexture = modelTexture;
        this.item = item;
    }

    public void place(Terrain terrain, Vector2f position) {
        boolean done = terrain.putItemIfSpace(position, getItemInstance());

        if (done)
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
