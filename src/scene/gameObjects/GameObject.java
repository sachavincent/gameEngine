package scene.gameObjects;

import engineTester.Rome;
import entities.Camera.Direction;
import entities.Entity;
import entities.ModelEntity;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import models.AbstractModel;
import scene.Scene;
import scene.components.*;
import scene.components.callbacks.ObjectPlacedCallback;
import terrain.TerrainPosition;
import util.Offset;
import util.math.Vector3f;

public abstract class GameObject {

    private static final Map<Class<? extends GameObject>, GameObject> UNIQUE_GAMEOBJECTS = new HashMap<>();

    private static int ID;

    protected final int id;

    protected final Map<String, Component> components;

    private final Scene scene;

    private boolean ignoreAddCallback;

    private Vector3f position;

    private boolean isPlaced;

    public GameObject() {
        if (!UNIQUE_GAMEOBJECTS.containsKey(getClass())) {
            UNIQUE_GAMEOBJECTS.put(getClass(), this);
            this.id = UNIQUE_GAMEOBJECTS.size() * -1;
        } else {
            this.id = ++ID;
        }
        this.components = new LinkedHashMap<>();
        this.scene = Rome.getGame().getScene();
    }

    public static void reset() {
        ID = 0;
        UNIQUE_GAMEOBJECTS.clear();
    }

    public int getId() {
        return this.id;
    }

    public static GameObject getGameObjectFromClass(Class<? extends GameObject> gameObjectClass) {
        if (gameObjectClass == null)
            return null;

        return UNIQUE_GAMEOBJECTS.get(gameObjectClass);
    }

    public final void addComponent(Component component) {
        assert !hasComponent(component.getClass());

        component.setId(this.id);

        this.components.put(component.getClass().getName(), component);
        ObjectPlacedCallback objPlacedCallback;
        if (this.isPlaced && (objPlacedCallback = component.getOnObjectPlacedCallback()) != null) {
            objPlacedCallback.onObjPlaced(this);
        }
    }

    public void onUniqueAddGameObject() {
        Stream<ObjectPlacedCallback> addComponentCallbackStream = this.components.values().stream()
                .map(Component::getOnObjectPlacedCallback).filter(Objects::nonNull);
        if (isIgnoreAddCallback())
            addComponentCallbackStream = addComponentCallbackStream
                    .filter(ObjectPlacedCallback::isForEach);

        addComponentCallbackStream.forEach(callback -> callback.onObjPlaced(this));
    }

    public Map<String, Component> getComponents() {
        return this.components;
    }

    public final <T extends Component> T getComponent(Class<T> componentClass) {
        Object object = this.components.get(componentClass.getName());
        return object == null ? null : (T) object;
    }

    public final <T extends Component> void removeComponent(Class<T> componentClass) {
        this.components.remove(componentClass.getName());
    }

    public final boolean hasComponent(Class<?> componentClass) {
        return this.components.containsKey(componentClass.getName());
    }

    public Scene getScene() {
        return this.scene;
    }

    public static <X extends GameObject> X createObjectFromClass(Class<X> objectClass) {
        try {
            return objectClass.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            System.err.println("Error while creating object: " + objectClass.getName());
        }
        return null;
    }

    public final void destroy() {
        Map<Class<? extends Component>, Set<Integer>> idGameObjectsForComponents = Rome.getGame().getScene()
                .getIdGameObjectsForComponents();
        for (Component component : getComponents().values()) {
            Set<Integer> ids = idGameObjectsForComponents.get(component.getClass());
            ids.remove(this.id);
        }
        Rome.getGame().getScene().removeGameObject(this.id);
//        if (this.id == ID)
//            ID--;
    }

    public static <X extends GameObject> X newInstance(Class<X> objectClass, TerrainPosition position,
            Direction direction) {
        return newInstance(objectClass, position, direction, false);
    }

    public static <X extends GameObject> X newInstance(Class<X> objectClass, TerrainPosition position) {
        return newInstance(objectClass, position, Direction.defaultDirection(), false);
    }

    public static <X extends GameObject> X newInstance(Class<X> objectClass, TerrainPosition position,
            Direction direction, boolean ignoreAddCallback) {
        X gameObject = createObjectFromClass(objectClass);
        gameObject.setIgnoreAddCallback(ignoreAddCallback);
        if (gameObject.hasComponent(DirectionComponent.class))
            gameObject.getComponent(DirectionComponent.class).setDirection(direction);
        else
            gameObject.addComponent(new DirectionComponent(direction));
        gameObject.placeAt(position.toVector3f());
        return gameObject;
    }


    public static <X extends GameObject> X[] newInstances(Class<X> objectClass, TerrainPosition[] positions) {
        if (positions == null)
            return (X[]) Array.newInstance(objectClass, 0);

        X[] gameObjects = (X[]) Array.newInstance(objectClass, positions.length);
        for (int i = 0; i < positions.length; i++) {
            if (positions[i] == null)
                continue;
            X gameObject = createObjectFromClass(objectClass);
            gameObject.placeAt(positions[i].toVector3f());

            gameObjects[i] = gameObject;
        }
        return gameObjects;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        GameObject that = (GameObject) o;
        return this.id == that.id;
    }

    public final Vector3f getPosition() {
        return this.position;
    }

    public final boolean isPlaced() {
        return this.isPlaced;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(this.id);
    }

    public final boolean isIgnoreAddCallback() {
        return this.ignoreAddCallback;
    }

    public final void setIgnoreAddCallback(boolean ignoreAddCallback) {
        this.ignoreAddCallback = ignoreAddCallback;
    }

    public static Class<? extends GameObject> getClassFromName(String gameObjectName) {
        return UNIQUE_GAMEOBJECTS.keySet().stream().filter(clazz -> {
            String simpleName = clazz.getSimpleName();
            return simpleName.equalsIgnoreCase(gameObjectName);
        }).findFirst().orElse(null);
    }

    public final void placeAt(TerrainPosition position) {
        placeAt(position.toVector3f());
    }

    /**
     * Place this gameObject at given position,
     * if this is the first time this object is placed,
     * ObjectPlacedCallbacks are called.
     *
     * @param position at which the gameObject is placed
     */
    public final void placeAt(Vector3f position) {
        this.position = position;

        if (!this.isPlaced && Rome.getGame().getScene().addGameObject(this)) {
            this.isPlaced = true;

            onUniqueAddGameObject();
        }
    }

    public static Entity createEntityFromGameObject(GameObject gameObject, boolean displayBoundingBoxes) {
        if (gameObject == null || !gameObject.isPlaced())
            return null;

        int id = gameObject.getId();

        Vector3f pos = gameObject.getPosition();
        PreviewComponent previewComponent = gameObject.getComponent(PreviewComponent.class);
        boolean preview = false;
        if (previewComponent != null && previewComponent.getPreviewPosition() != null) {
            TerrainPosition previewPosition = previewComponent.getPreviewPosition(); // = null if no preview
            if (previewPosition != null) {
                preview = true;
                pos = previewPosition.toVector3f();
            }
        }

        if (pos == null)
            return null;

        if (gameObject.hasComponent(OffsetComponent.class))
            pos.add(gameObject.getComponent(OffsetComponent.class).getOffset());

        float scale = gameObject.hasComponent(ScaleComponent.class) ? gameObject
                .getComponent(ScaleComponent.class).getScale() : 1;
        Direction direction =
                gameObject.hasComponent(DirectionComponent.class) ? gameObject.getComponent(DirectionComponent.class)
                        .getDirection() : Direction.defaultDirection();

        Entity entity;
        if (gameObject.hasComponent(SingleModelComponent.class) ||
                gameObject.hasComponent(AnimatedModelComponent.class) || displayBoundingBoxes) {
            AbstractModel model = null;
            if (preview)
                model = previewComponent.getModel();
            else if (displayBoundingBoxes)
                if (gameObject.hasComponent(BoundingBoxComponent.class))
                    model = gameObject.getComponent(BoundingBoxComponent.class).getBoundingBox();

            if (model == null) {
                if (gameObject.hasComponent(SingleModelComponent.class))
                    model = gameObject.getComponent(SingleModelComponent.class).getModel();
                else
                    model = gameObject.getComponent(AnimatedModelComponent.class).getModel();
            }

            entity = new Entity(new ModelEntity(pos, direction, scale, model, id));
        } else if (gameObject.hasComponent(MultipleModelsComponent.class)) {
            if (preview)
                entity = new Entity(new ModelEntity(pos, direction, scale, previewComponent.getModel(), id));
            else {
                Vector3f finalPos = pos;
                MultipleModelsComponent multipleModelsComponent = gameObject
                        .getComponent(MultipleModelsComponent.class);
                Map<String, Entry<AbstractModel, Offset>> concurrentModels = multipleModelsComponent.getConcurrentModels();
                List<ModelEntity> modelEntities = concurrentModels
                        .values().stream().map(entry -> {
                            ModelEntity modelEntity = new ModelEntity(entry.getKey().toModelEntity());
                            Vector3f offsetPosition = entry.getValue().getOffsetPosition();
                            int offsetRotation = entry.getValue().getOffsetRotation();
                            float offsetScale = entry.getValue().getOffsetScale();
                            Vector3f modelPosition = new Vector3f();
                            switch (direction) {
                                case NORTH:
                                    break;
                                case WEST:
                                    modelPosition = new Vector3f(-modelPosition.getX(), modelPosition.getY(),
                                            modelPosition.getZ());
                                    break;
                                case SOUTH:
                                    modelPosition = new Vector3f(-modelPosition.getX(), modelPosition.getY(),
                                            -modelPosition.getZ());
                                    break;
                                case EAST:
                                    modelPosition = new Vector3f(modelPosition.getX(), modelPosition.getY(),
                                            -modelPosition.getZ());
                                    break;
                            }
                            Vector3f newPos = Vector3f.add(offsetPosition, finalPos, null);
                            Vector3f.add(newPos, modelPosition, newPos);
                            modelEntity.setPosition(newPos);
                            modelEntity.setScale(offsetScale + scale);
                            if (!entry.getValue().isFixedRotation()) {
                                modelEntity.setRotation(new Vector3f(0, direction.add(offsetRotation).getDegree(), 0));
                            }
                            return modelEntity;
                        }).collect(Collectors.toList());
                entity = new Entity(modelEntities);
            }
        } else {
            return null;
        }

        return entity;
    }
}