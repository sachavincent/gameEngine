package items.roads;

import static items.ConnectableItem.Connections.NONE;

import entities.Camera.Direction;
import items.ConnectableItem;
import items.Item;
import items.PlaceHolderConnectableItem;
import java.util.Map;
import models.TexturedModel;
import terrains.Terrain;
import util.math.Vector2f;

public abstract class RoadItem extends Item implements ConnectableItem {

    public final static String PATH = "roads/";

    private Connections[] connected = new Connections[]{NONE, NONE, NONE, NONE};

    private RoadType roadType;

    TexturedModel[] straight;
    TexturedModel[] deadEnd;
    TexturedModel[] turn;
    TexturedModel[] threeWay;
    TexturedModel[] fourWay;

    public RoadItem(String name) {
        super(name, 0, 0, 0, 0, 0);
        setScale(.5f);
    }

    public Item updateNeighboursAndCenter(Terrain terrain, Vector2f center) {
        Map<Vector2f, Item> items = terrain.getItems();

        roadType = new StraightRoad();
        setTexture(straight[0]);
        setRotation(90);

        Vector2f northPosition = center.add(Direction.toRelativeDistance(Direction.NORTH));
        Vector2f eastPosition = center.add(Direction.toRelativeDistance(Direction.EAST));
        Vector2f southPosition = center.add(Direction.toRelativeDistance(Direction.SOUTH));
        Vector2f westPosition = center.add(Direction.toRelativeDistance(Direction.WEST));

        Item southItem = items.getOrDefault(southPosition, null);
        Item northItem = items.getOrDefault(northPosition, null);
        Item westItem = items.getOrDefault(westPosition, null);
        Item eastItem = items.getOrDefault(eastPosition, null);

        if (southItem instanceof ConnectableItem) {
            if (southItem instanceof PlaceHolderConnectableItem) {
                ((PlaceHolderConnectableItem) southItem).connect(Direction.NORTH, Connections.ROAD);
            } else
                ((ConnectableItem) southItem).connect(Direction.NORTH, Connections.ROAD);
            this.connect(Direction.SOUTH, Connections.getConnections((ConnectableItem) southItem));
        }

        if (northItem instanceof ConnectableItem) {
            if (northItem instanceof PlaceHolderConnectableItem) {
                ((PlaceHolderConnectableItem) northItem).connect(Direction.SOUTH, Connections.ROAD);
            } else
                ((ConnectableItem) northItem).connect(Direction.SOUTH, Connections.ROAD);
            this.connect(Direction.NORTH, Connections.getConnections((ConnectableItem) northItem));
        }

        if (westItem instanceof ConnectableItem) {
            if (westItem instanceof PlaceHolderConnectableItem) {
                ((PlaceHolderConnectableItem) westItem).connect(Direction.EAST, Connections.ROAD);
            } else
                ((ConnectableItem) westItem).connect(Direction.EAST, Connections.ROAD);
            this.connect(Direction.WEST, Connections.getConnections((ConnectableItem) westItem));
        }

        if (eastItem instanceof ConnectableItem) {
            if (eastItem instanceof PlaceHolderConnectableItem) {
                ((PlaceHolderConnectableItem) eastItem).connect(Direction.WEST, Connections.ROAD);
            } else
                ((ConnectableItem) eastItem).connect(Direction.WEST, Connections.ROAD);
            this.connect(Direction.EAST, Connections.getConnections((ConnectableItem) eastItem));
        }

        return this;
    }

    public RoadType getRoadType() {
        return this.roadType;
    }


    @Override
    public void connect(Direction direction, Connections connections) {
        this.connected[direction.ordinal()] = connections;

        updateTexture();
    }

    @Override
    public void disconnect(Direction direction) {
        this.connected[direction.ordinal()] = NONE;

        updateTexture();
    }

    private void updateTexture() {
        Connections northConnections = this.connected[Direction.NORTH.ordinal()];
        Connections southConnections = this.connected[Direction.SOUTH.ordinal()];
        Connections eastConnections = this.connected[Direction.EAST.ordinal()];
        Connections westConnections = this.connected[Direction.WEST.ordinal()];

        boolean northConnected = northConnections == Connections.ROAD;
        boolean southConnected = southConnections == Connections.ROAD;
        boolean eastConnected = eastConnections == Connections.ROAD;
        boolean westConnected = westConnections == Connections.ROAD;

        if (eastConnected && westConnected && northConnected && southConnected) { // FourWay
            roadType = new FourWayRoad();
            setTexture(fourWay[0]);
        } else if (westConnected && northConnected && southConnected) { // ThreeWay w/o eastPosition
            roadType = new ThreeWayRoad();
            setTexture(threeWay[0]);
            setRotation(0);
        } else if (eastConnected && westConnected && southConnected) { // ThreeWay w/o northPosition
            roadType = new ThreeWayRoad();
            setTexture(threeWay[0]);
            setRotation(90);
        } else if (eastConnected && northConnected && southConnected) { // ThreeWay w/o westPosition
            roadType = new ThreeWayRoad();
            setTexture(threeWay[0]);
            setRotation(180);
        } else if (eastConnected && westConnected && northConnected) { // ThreeWay w/o southPosition
            roadType = new ThreeWayRoad();
            setTexture(threeWay[1]);
            setRotation(270);
        } else if (eastConnected && southConnected) { // Turn with eastPosition & southPosition
            roadType = new TurnRoad();
            setRotation(270);
            if (northConnections == Connections.BUILDING)
                setTexture(turn[1]);
            else if (westConnections == Connections.BUILDING) {
                setTexture(threeWay[1]);
                setRotation(90);
            } else
                setTexture(turn[0]);
        } else if (eastConnected && northConnected) { // Turn with eastPosition & northPosition
            roadType = new TurnRoad();
            setTexture(turn[0]);
            setRotation(0);
            if (southConnections == Connections.BUILDING)
                setTexture(turn[2]);
            else if (westConnections == Connections.BUILDING) {
                setTexture(threeWay[2]);
                setRotation(270);
            } else
                setTexture(turn[0]);
        } else if (westConnected && southConnected) { // Turn with westPosition & southPosition
            roadType = new TurnRoad();
            setTexture(turn[0]);
            setRotation(180);
            if (northConnections == Connections.BUILDING)
                setTexture(turn[2]);
            else if (eastConnections == Connections.BUILDING) {
                setTexture(threeWay[2]);
                setRotation(90);
            } else
                setTexture(turn[0]);
        } else if (westConnected && northConnected) { // Turn with westPosition & northPosition
            roadType = new TurnRoad();
            setTexture(turn[0]);
            setRotation(90);
            if (southConnections == Connections.BUILDING)
                setTexture(turn[2]);
            else if (eastConnections == Connections.BUILDING) {
                setTexture(threeWay[1]);
                setRotation(270);
            } else
                setTexture(turn[0]);
//        } else if (northConnected || southConnected) {
//            roadType = new StraightRoad();
////            if (eastConnections == Connections.BUILDING && westConnections == Connections.BUILDING)
////                setTexture(straight[3]);
////            else if (eastConnections == Connections.BUILDING)
////                setTexture(straight[1]);
////            else if (westConnections == Connections.BUILDING)
////                setTexture(straight[2]);
////            else
//                setTexture(straight[0]);
//            setRotation(90);
//        } else if (westConnected || eastConnected) {
//            roadType = new StraightRoad();
////            if (northConnections == Connections.BUILDING && southConnections == Connections.BUILDING)
////                setTexture(straight[3]);
////            else if (northConnections == Connections.BUILDING)
////                setTexture(straight[1]);
////            else if (southConnections == Connections.BUILDING)
////                setTexture(straight[2]);
////            else
//                setTexture(straight[0]);
//            setRotation(180);
        } else {
            roadType = new StraightRoad();
            setRotation(0);
            if (northConnections == Connections.BUILDING && southConnections == Connections.BUILDING)
                setTexture(straight[3]);
            else if (northConnections == Connections.BUILDING && !southConnected) {
                setTexture(straight[2]);
            } else if (southConnections == Connections.BUILDING && !northConnected)
                setTexture(straight[1]);
            else {
                setRotation(90);
                if (eastConnections == Connections.BUILDING && westConnections == Connections.BUILDING)
                    setTexture(straight[3]);
                else if (eastConnections == Connections.BUILDING && !westConnected)
                    setTexture(straight[1]);
                else if (westConnections == Connections.BUILDING && !eastConnected)
                    setTexture(straight[2]);
                else {
                    setTexture(straight[0]);
                    if (northConnected || southConnected)
                        setRotation(90);
                    if (westConnected || eastConnected)
                        setRotation(180);
                }
            }
        }
    }

    @Override
    public boolean[] getAccessPoints() {
        return new boolean[]{true, true, true, true};
    }

    public boolean isConnected(Direction direction) {
        return this.connected[direction.ordinal()] != NONE;
    }

}
