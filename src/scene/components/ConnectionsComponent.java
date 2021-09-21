package scene.components;

import engineTester.Rome;
import entities.Camera.Direction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import scene.Scene;
import scene.components.callbacks.ObjectPlacedCallback;
import scene.gameObjects.GameObject;
import scene.gameObjects.Terrain;
import terrain.TerrainPosition;

public class ConnectionsComponent<ConnectionType> extends Component {

    // EAST NORTH WEST SOUTH
    private final boolean[] accessPoints = new boolean[]{true, true, true, true};
    private final int[]     connections  = new int[]{0, 0, 0, 0};

    private final Class<ConnectionType> connectionTypeClass;

    public ConnectionsComponent(Class<ConnectionType> clazz, boolean west, boolean north, boolean east, boolean south,
            ObjectPlacedCallback objectPlacedCallback) {
        this(clazz, new boolean[]{west, north, east, south}, objectPlacedCallback);
    }

    public ConnectionsComponent(Class<ConnectionType> clazz, boolean[] accessPoints,
            ObjectPlacedCallback objectPlacedCallback) {
        super((gameObject) -> {
            Set<ConnectionsComponent<?>> toUpdate = new HashSet<>();
            TerrainPosition pos = gameObject.getPosition().toTerrainPosition();
            Scene scene = Rome.getGame().getScene();
            int id = gameObject.getId();
            ConnectionsComponent<ConnectionType> connectionsComponent = gameObject
                    .getComponent(ConnectionsComponent.class);
            if (connectionsComponent == null)
                return;

            Terrain terrain = Rome.getGame().getScene().getTerrain();
            HeightMapComponent heightMapComponent = terrain.getComponent(HeightMapComponent.class);
            if (heightMapComponent == null)
                return;

            int xNeg = 0;
            int xPos = 1;
            int zNeg = 0;
            int zPos = 1;
            if (gameObject.hasComponent(OffsetsComponent.class)) {
                OffsetsComponent offsetsComponent = gameObject.getComponent(OffsetsComponent.class);
                Direction direction = gameObject.hasComponent(DirectionComponent.class) ? gameObject
                        .getComponent(DirectionComponent.class).getDirection() : Direction.defaultDirection();
                int[] offsets = offsetsComponent.getLocalOffsets(direction);
                zNeg = offsets[0];
                xPos = offsets[1];
                zPos = offsets[2];
                xNeg = offsets[3];
            }

            ConnectionsComponent<?> relativeComponent;
            Map<Integer, Integer> nbConnections = new HashMap<>();
            for (int x = -xNeg; x < xPos; x++) {
                int xOff = pos.getX() + x;
                int zOff = pos.getZ() - zNeg - 1;
                GameObject relativeGameObject = scene.getGameObjectAtPosition(xOff, zOff);
                if (relativeGameObject != null && relativeGameObject.hasComponent(ConnectionsComponent.class)) {
                    relativeComponent = relativeGameObject.getComponent(ConnectionsComponent.class);
                    if (relativeComponent.getConnectionTypeClass() == connectionsComponent.getConnectionTypeClass()) {
                        int xPosRelative = 1;
                        int xNegRelative = 0;
                        if (relativeGameObject.hasComponent(OffsetsComponent.class)) {
                            OffsetsComponent offsetsComponent = relativeGameObject.getComponent(OffsetsComponent.class);
                            Direction direction =
                                    relativeGameObject.hasComponent(DirectionComponent.class) ? relativeGameObject
                                            .getComponent(DirectionComponent.class).getDirection()
                                            : Direction.defaultDirection();
                            int[] offsets = offsetsComponent.getLocalOffsets(direction);
                            xNegRelative = offsets[3];
                            xPosRelative = offsets[1];
                        }

                        int nbConnectionsForRelativeGameObject = nbConnections
                                .merge(relativeGameObject.getId(), 1, Integer::sum);
                        if (nbConnectionsForRelativeGameObject == xNegRelative + xPosRelative ||
                                nbConnectionsForRelativeGameObject == xNeg + xPos) {
                            connectionsComponent.connect(Direction.WEST, relativeGameObject.getId());
                            relativeComponent.connect(Direction.EAST, id);
                            toUpdate.add(relativeComponent);
                        }
                    }
                }

                zOff = pos.getZ() + zPos;
                relativeGameObject = scene.getGameObjectAtPosition(xOff, zOff);
                if (relativeGameObject != null && relativeGameObject.hasComponent(ConnectionsComponent.class)) {
                    relativeComponent = relativeGameObject.getComponent(ConnectionsComponent.class);
                    if (relativeComponent.getConnectionTypeClass() == connectionsComponent.getConnectionTypeClass()) {
                        int xPosRelative = 1;
                        int xNegRelative = 0;
                        if (relativeGameObject.hasComponent(OffsetsComponent.class)) {
                            OffsetsComponent offsetsComponent = relativeGameObject.getComponent(OffsetsComponent.class);
                            Direction direction =
                                    relativeGameObject.hasComponent(DirectionComponent.class) ? relativeGameObject
                                            .getComponent(DirectionComponent.class).getDirection()
                                            : Direction.defaultDirection();
                            int[] offsets = offsetsComponent.getLocalOffsets(direction);
                            xNegRelative = offsets[3];
                            xPosRelative = offsets[1];
                        }

                        int nbConnectionsForRelativeGameObject = nbConnections
                                .merge(relativeGameObject.getId(), 1, Integer::sum);
                        if (nbConnectionsForRelativeGameObject == xNegRelative + xPosRelative ||
                                nbConnectionsForRelativeGameObject == xNeg + xPos) {
                            connectionsComponent.connect(Direction.EAST, relativeGameObject.getId());
                            relativeComponent.connect(Direction.WEST, id);
                            toUpdate.add(relativeComponent);
                        }
                    }
                }
            }
            for (int z = -zNeg; z < zPos; z++) {
                int xOff = pos.getX() - xNeg - 1;
                int zOff = pos.getZ() + z;
                GameObject relativeGameObject = scene.getGameObjectAtPosition(xOff, zOff);
                if (relativeGameObject != null && relativeGameObject.hasComponent(ConnectionsComponent.class)) {
                    relativeComponent = relativeGameObject.getComponent(ConnectionsComponent.class);
                    if (relativeComponent.getConnectionTypeClass() == connectionsComponent.getConnectionTypeClass()) {
                        int zPosRelative = 1;
                        int zNegRelative = 0;
                        if (relativeGameObject.hasComponent(OffsetsComponent.class)) {
                            OffsetsComponent offsetsComponent = relativeGameObject.getComponent(OffsetsComponent.class);
                            Direction direction =
                                    relativeGameObject.hasComponent(DirectionComponent.class) ? relativeGameObject
                                            .getComponent(DirectionComponent.class).getDirection()
                                            : Direction.defaultDirection();
                            int[] offsets = offsetsComponent.getLocalOffsets(direction);
                            zNegRelative = offsets[0];
                            zPosRelative = offsets[2];
                        }

                        int nbConnectionsForRelativeGameObject = nbConnections
                                .merge(relativeGameObject.getId(), 1, Integer::sum);
                        if (nbConnectionsForRelativeGameObject == zNegRelative + zPosRelative ||
                                nbConnectionsForRelativeGameObject == zNeg + zPos) {
                            connectionsComponent.connect(Direction.SOUTH, relativeGameObject.getId());
                            relativeComponent.connect(Direction.NORTH, id);
                            toUpdate.add(relativeComponent);
                        }
                    }
                }
                xOff = pos.getX() + xPos;
                relativeGameObject = scene.getGameObjectAtPosition(xOff, zOff);
                if (relativeGameObject != null && relativeGameObject.hasComponent(ConnectionsComponent.class)) {
                    relativeComponent = relativeGameObject.getComponent(ConnectionsComponent.class);
                    if (relativeComponent.getConnectionTypeClass() == connectionsComponent.getConnectionTypeClass()) {
                        int zPosRelative = 1;
                        int zNegRelative = 0;
                        if (relativeGameObject.hasComponent(OffsetsComponent.class)) {
                            OffsetsComponent offsetsComponent = relativeGameObject.getComponent(OffsetsComponent.class);
                            Direction direction =
                                    relativeGameObject.hasComponent(DirectionComponent.class) ? relativeGameObject
                                            .getComponent(DirectionComponent.class).getDirection()
                                            : Direction.defaultDirection();
                            int[] offsets = offsetsComponent.getLocalOffsets(direction);
                            zNegRelative = offsets[0];
                            zPosRelative = offsets[2];
                        }

                        int nbConnectionsForRelativeGameObject = nbConnections
                                .merge(relativeGameObject.getId(), 1, Integer::sum);
                        if (nbConnectionsForRelativeGameObject == zNegRelative + zPosRelative ||
                                nbConnectionsForRelativeGameObject == zNeg + zPos) {
                            connectionsComponent.connect(Direction.NORTH, relativeGameObject.getId());
                            relativeComponent.connect(Direction.SOUTH, id);
                            toUpdate.add(relativeComponent);
                        }
                    }
                }
            }
            if (!toUpdate.isEmpty()) {
                toUpdate.add(connectionsComponent);
                Rome.getGame().getScene().getIdGameObjectsForComponentClass(ConnectionsComponent.class).stream()
                        .map(idObj -> Rome.getGame().getScene().getGameObjectFromId(idObj))
                        .filter(Objects::nonNull)
                        .map(gameObjectFromId -> gameObjectFromId.getComponent(ConnectionsComponent.class))
                        .filter(component -> component.getConnectionTypeClass() ==
                                connectionsComponent.getConnectionTypeClass())
                        .forEach(component -> component.setUpdated(false));

                toUpdate.forEach(Component::update);
            }

            objectPlacedCallback.onObjPlaced(gameObject);
        });

        this.accessPoints[0] = accessPoints[0];
        this.accessPoints[1] = accessPoints[1];
        this.accessPoints[2] = accessPoints[2];
        this.accessPoints[3] = accessPoints[3];

        this.connectionTypeClass = clazz;
    }

    public ConnectionsComponent(Class<ConnectionType> clazz, ObjectPlacedCallback objectPlacedCallback) {
        this(clazz, new boolean[]{true, true, true, true}, objectPlacedCallback);
    }

    public boolean isConnected(Direction direction) {
        return this.connections[direction.ordinal()] != 0;
    }

    public int getConnection(Direction direction) {
        return this.connections[direction.ordinal()];
    }

    public boolean isConnected() {
        for (Direction direction : Direction.values()) {
            if (getAccessPoints()[direction.ordinal()] && isConnected(direction))
                return true;
        }
        return false;
    }

    public void connect(Direction direction, int idConnected) {
        if (this.accessPoints[direction.ordinal()]) {
            this.connections[direction.ordinal()] = idConnected;
        }
    }

    public void disconnect(Direction direction) {
        if (this.accessPoints[direction.ordinal()]) {
            if (this.connections[direction.ordinal()] != 0) {
                this.connections[direction.ordinal()] = 0;
            }
        }
    }

    public boolean[] getAccessPoints() {
        return this.accessPoints;
    }

    public Class<ConnectionType> getConnectionTypeClass() {
        return this.connectionTypeClass;
    }
}
