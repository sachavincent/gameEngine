package items.roads;

import entities.Camera.Direction;
import items.ConnectableItem;
import items.Item;
import java.util.Map;
import models.TexturedModel;
import terrains.Terrain;
import util.math.Vector2f;

public abstract class RoadItem extends Item implements ConnectableItem {

    private boolean[] connected = new boolean[]{false, false, false, false};

    private RoadType roadType;

    TexturedModel straight;
    TexturedModel deadEnd;
    TexturedModel turn;
    TexturedModel threeWay;
    TexturedModel fourWay;

    public RoadItem() {
        super(1, 0, 1);
        setScale(.5f);
    }

    public Item updateNeighboursAndCenter(Terrain terrain, Vector2f center) {
        Map<Vector2f, Item> items = terrain.getItems();

        roadType = new StraightRoad();
        setTexture(straight);
        setRotation(90);

        Vector2f northPosition = center.add(new Vector2f(1, 0));
        Vector2f eastPosition = center.add(new Vector2f(0, 1));
        Vector2f southPosition = center.add(new Vector2f(-1, 0));
        Vector2f westPosition = center.add(new Vector2f(0, -1));

        Item southItem = items.getOrDefault(southPosition, null);
        Item northItem = items.getOrDefault(northPosition, null);
        Item westItem = items.getOrDefault(westPosition, null);
        Item eastItem = items.getOrDefault(eastPosition, null);

        if (southItem instanceof ConnectableItem) {
            ((ConnectableItem) southItem).connect(Direction.NORTH);
            this.connect(Direction.SOUTH);
        }

        if (northItem instanceof ConnectableItem) {
            ((ConnectableItem) northItem).connect(Direction.SOUTH);
            this.connect(Direction.NORTH);
        }

        if (westItem instanceof ConnectableItem) {
            ((ConnectableItem) westItem).connect(Direction.EAST);
            this.connect(Direction.WEST);
        }

        if (eastItem instanceof ConnectableItem) {
            ((ConnectableItem) eastItem).connect(Direction.WEST);
            this.connect(Direction.EAST);
        }

        return this;
    }

    public RoadType getRoadType() {
        return this.roadType;
    }

    @Override
    public void connect(Direction direction) {
        this.connected[direction.ordinal()] = true;

        updateTexture();
    }

    @Override
    public void disconnect(Direction direction) {
        this.connected[direction.ordinal()] = false;

        updateTexture();
    }

    private void updateTexture() {
        boolean northConnected = this.connected[Direction.NORTH.ordinal()];
        boolean southConnected = this.connected[Direction.SOUTH.ordinal()];
        boolean eastConnected = this.connected[Direction.EAST.ordinal()];
        boolean westConnected = this.connected[Direction.WEST.ordinal()];

        if (eastConnected && westConnected && northConnected && southConnected) { // FourWay
            roadType = new FourWayRoad();
            setTexture(fourWay);
        } else if (westConnected && northConnected && southConnected) { // ThreeWay w/o eastPosition
            roadType = new ThreeWayRoad();
            setTexture(threeWay);
            setRotation(0);
        } else if (eastConnected && westConnected && southConnected) { // ThreeWay w/o northPosition
            roadType = new ThreeWayRoad();
            setTexture(threeWay);
            setRotation(90);
        } else if (eastConnected && northConnected && southConnected) { // ThreeWay w/o westPosition
            roadType = new ThreeWayRoad();
            setTexture(threeWay);
            setRotation(180);
        } else if (eastConnected && westConnected && northConnected) { // ThreeWay w/o southPosition
            roadType = new ThreeWayRoad();
            setTexture(threeWay);
            setRotation(270);
        } else if (eastConnected && southConnected) { // Turn with eastPosition & southPosition
            roadType = new TurnRoad();
            setTexture(turn);
            setRotation(270);
        } else if (eastConnected && northConnected) { // Turn with eastPosition & northPosition
            roadType = new TurnRoad();
            setTexture(turn);
            setRotation(0);
        } else if (westConnected && southConnected) { // Turn with westPosition & southPosition
            roadType = new TurnRoad();
            setTexture(turn);
            setRotation(180);
        } else if (westConnected && northConnected) { // Turn with westPosition & northPosition
            roadType = new TurnRoad();
            setTexture(turn);
            setRotation(90);
        } else if (northConnected || southConnected) {
            roadType = new StraightRoad();
            setTexture(straight);
            setRotation(90);
        } else if (westConnected || eastConnected) {
            roadType = new StraightRoad();
            setTexture(straight);
            setRotation(0);
        } else {
            roadType = new StraightRoad();
            setTexture(straight);
            setRotation(0);
        }
    }

    @Override
    public boolean[] getAccessPoints() {
        return new boolean[]{true, true, true, true};
    }
}
