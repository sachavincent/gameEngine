package items.roads;

import static items.ConnectableItem.Connections.NONE;

import entities.Camera.Direction;
import items.ConnectableItem;
import items.Item;
import items.PlaceHolderConnectableItem;
import java.util.Map;
import models.RawModel;
import models.TexturedModel;
import renderEngine.OBJLoader;
import terrains.Terrain;
import textures.ModelTexture;
import util.math.Vector2f;

public abstract class RoadItem extends Item implements ConnectableItem {

    private final Connections[] connected = new Connections[]{NONE, NONE, NONE, NONE};

    private final static RawModel roadModel = OBJLoader.loadObjModel("road");

    public RoadItem(String name) {
        super(name, 0, 0, 0, 0, 0);

        String textureName = name.replace(" ", "_").toLowerCase();
        this.texture = new TexturedModel(roadModel, new ModelTexture(textureName + ".png", true));
        this.previewTexture = new TexturedModel(roadModel, new ModelTexture(textureName + ".png", true));

        setScale(.5f);
    }

    public Item updateNeighboursAndCenter(Terrain terrain, Vector2f center) {
        Map<Vector2f, Item> items = terrain.getItems();

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


    @Override
    public void connect(Direction direction, Connections connections) {
        this.connected[direction.ordinal()] = connections;
    }

    @Override
    public void disconnect(Direction direction) {
        this.connected[direction.ordinal()] = NONE;
    }

    @Override
    public boolean[] getAccessPoints() {
        return new boolean[]{true, true, true, true};
    }

    public boolean isConnected(Direction direction) {
        return this.connected[direction.ordinal()] != NONE;
    }

    @Override
    public boolean isConnected() {
        for (Direction direction : Direction.values()) {
            if (getAccessPoints()[direction.ordinal()] && isConnected(direction))
                return true;
        }
        return false;
    }
}
