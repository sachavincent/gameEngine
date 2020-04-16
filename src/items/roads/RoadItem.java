package items.roads;

import items.Item;
import java.util.Map;
import models.TexturedModel;
import terrains.Terrain;
import util.math.Vector3f;

public abstract class RoadItem extends Item {

    TexturedModel straight;
    TexturedModel deadEnd;
    TexturedModel turn;
    TexturedModel threeWay;
    TexturedModel fourWay;

    @Override
    public void place(Terrain terrain, Vector3f position) {
        Map<Vector3f, Item> items = terrain.getItems();

        Item item = items.getOrDefault(position, null);

        if (item != null) {
            terrain.removeItem(position);

            updateNeighbours(terrain, position);

            return;
        }

        items.put(position, updateNeighboursAndCenter(terrain, position));
        updateNeighbours(terrain, position);
    }

    private void updateNeighbours(Terrain terrain, Vector3f center) {
        Vector3f left = center.add(new Vector3f(2, 0, 0));
        Vector3f right = center.add(new Vector3f(-2, 0, 0));
        Vector3f back = center.add(new Vector3f(0, 0, -2));
        Vector3f front = center.add(new Vector3f(0, 0, 2));

        updateNeighboursAndCenter(terrain, right);
        updateNeighboursAndCenter(terrain, left);
        updateNeighboursAndCenter(terrain, back);
        updateNeighboursAndCenter(terrain, front);
    }

    private Item updateNeighboursAndCenter(Terrain terrain, Vector3f center) {
        Map<Vector3f, Item> items = terrain.getItems();

        Item item = items.getOrDefault(center, null);

        if (item == null)
            item = this;

        Vector3f left = center.add(new Vector3f(2, 0, 0));
        Vector3f right = center.add(new Vector3f(-2, 0, 0));
        Vector3f back = center.add(new Vector3f(0, 0, -2));
        Vector3f front = center.add(new Vector3f(0, 0, 2));

        Item rightItem = items.getOrDefault(right, null);
        Item leftItem = items.getOrDefault(left, null);
        Item backItem = items.getOrDefault(back, null);
        Item frontItem = items.getOrDefault(front, null);

        if (frontItem != null && backItem != null && leftItem != null && rightItem != null) { // FourWay
            item.setTexture(fourWay);
        } else if (backItem != null && leftItem != null && rightItem != null) { // ThreeWay w/o front
            item.setTexture(threeWay);
            item.setRotation(0);
        } else if (frontItem != null && backItem != null && rightItem != null) { // ThreeWay w/o left
            item.setTexture(threeWay);
            item.setRotation(90);
        } else if (frontItem != null && leftItem != null && rightItem != null) { // ThreeWay w/o back
            item.setTexture(threeWay);
            item.setRotation(180);
        } else if (frontItem != null && backItem != null && leftItem != null) { // ThreeWay w/o right
            item.setTexture(threeWay);
            item.setRotation(270);
        } else if (frontItem != null && rightItem != null) { // Turn with front & right
            item.setTexture(turn);
            item.setRotation(270);
        } else if (frontItem != null && leftItem != null) { // Turn with front & left
            item.setTexture(turn);
            item.setRotation(0);
        } else if (backItem != null && rightItem != null) { // Turn with back & right
            item.setTexture(turn);
            item.setRotation(180);
        } else if (backItem != null && leftItem != null) { // Turn with back & left
            item.setTexture(turn);
            item.setRotation(90);
        } else if (leftItem != null || rightItem != null) {
            item.setTexture(straight);
            item.setRotation(90);
        } else if (backItem != null || frontItem != null) {
            item.setTexture(straight);
            item.setRotation(0);
        } else {
            item.setTexture(straight);
            item.setRotation(0);
        }

        return item;
    }
}
