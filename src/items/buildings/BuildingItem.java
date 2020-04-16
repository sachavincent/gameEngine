package items.buildings;

import items.Item;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import terrains.Terrain;
import util.math.Vector3f;

public abstract class BuildingItem extends Item {

    public BuildingItem(@NotNull String name, Item copy) {
        super();

        this.name = name;

        if (copy != null) {
            this.texture = copy.getTexture();
            this.boundingBox = copy.getBoundingBox();
            this.selectionBox = copy.getSelectionBox();
        }
    }

    @Override
    public void place(Terrain terrain, Vector3f position) {
        Map<Vector3f, Item> items = terrain.getItems();

        Item item = items.getOrDefault(position, null);

        if (item != null) {
            terrain.removeItem(position);

            return;
        }

        items.put(position, this);
    }

}
