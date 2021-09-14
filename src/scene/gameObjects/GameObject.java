package scene.gameObjects;

import static java.util.Map.Entry;
import static scene.components.MultipleModelsComponent.Offset;

import entities.Camera.Direction;
import entities.Entity;
import entities.ModelEntity;
import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import models.AbstractModel;
import scene.Scene;
import scene.components.*;
import scene.components.callbacks.AddComponentCallback;
import terrain.TerrainPosition;
import util.math.Vector3f;

public abstract class GameObject {

    private static final Set<Class<? extends GameObject>> UNIQUE_GAMEOBJECTS = new HashSet<>();

    private static int ID;

    protected final int id;

    protected final Map<String, Component> components;

    private final Scene scene;

    private boolean ignoreAddCallback;

    public GameObject() {
        this.id = ++ID;

        this.components = new LinkedHashMap<>();
        this.scene = Scene.getInstance();

        UNIQUE_GAMEOBJECTS.add(this.getClass());
    }

    public static void reset() {
        ID = 0;
        UNIQUE_GAMEOBJECTS.clear();
    }

    public int getId() {
        return this.id;
    }

    public final void addComponent(Component component) {
        assert !hasComponent(component.getClass());

        component.setId(this.id);

        this.components.put(component.getClass().getName(), component);

        if (component instanceof PositionComponent) {
            if (Scene.getInstance().addGameObject(this)) {
                Stream<AddComponentCallback> addComponentCallbackStream = this.components.values().stream()
                        .map(Component::getOnAddComponentCallback).filter(Objects::nonNull);
                if (isIgnoreAddCallback())
                    addComponentCallbackStream = addComponentCallbackStream
                            .filter(AddComponentCallback::isForEach);

                addComponentCallbackStream.forEach(callback -> callback
                        .onAddComponent(this, ((PositionComponent) component).getPosition()));
            }
        }
    }

    public void onUniqueAddGameObject(Vector3f position) {
        this.components.values().stream().map(Component::getOnAddComponentCallback).filter(Objects::nonNull)
                .filter(addComponentCallback -> !addComponentCallback.isForEach())
                .forEach(callback -> callback.onAddComponent(this, position));
    }

    public Map<String, Component> getComponents() {
        return this.components;
    }

    public final <T extends Component> T getComponent(Class<T> componentClass) {
        Object object = this.components.get(componentClass.getName());
        return object == null ? null : (T) object;
    }

    public final <T extends Component> void removeComponent(Class<T> componentClass) {
        Component removedComponent = this.components.remove(componentClass.getName());

        if (removedComponent instanceof PositionComponent) {
            Scene.getInstance().removeGameObject(this.id);
        }
    }

    public final boolean hasComponent(Class<?> componentClass) {
        return this.components.containsKey(componentClass.getName());
    }

    public Scene getScene() {
        return this.scene;
    }

    public static <X extends GameObject> X getObjectFromClass(Class<X> objectClass) {
        try {
            return objectClass.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            System.err.println("Error while creating object: " + objectClass.getName());
        }
        return null;
    }

    public final void destroy() {
        Map<Class<? extends Component>, Set<Integer>> idGameObjectsForComponents = Scene.getInstance()
                .getIdGameObjectsForComponents();
        for (Component component : getComponents().values()) {
            Set<Integer> ids = idGameObjectsForComponents.get(component.getClass());
            ids.remove(this.id);
        }
        Scene.getInstance().removeGameObject(this.id);
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
            Direction direction,
            boolean ignoreAddCallback) {
        X gameObject = getObjectFromClass(objectClass);
        gameObject.setIgnoreAddCallback(ignoreAddCallback);
        if (gameObject.hasComponent(DirectionComponent.class))
            gameObject.getComponent(DirectionComponent.class).setDirection(direction);
        else
            gameObject.addComponent(new DirectionComponent(direction));
        if (gameObject.hasComponent(PositionComponent.class))
            gameObject.getComponent(PositionComponent.class).setPosition(position.toVector3f());
        else
            gameObject.addComponent(new PositionComponent(position));
        return gameObject;
    }


    public static <X extends GameObject> X[] newInstances(Class<X> objectClass, TerrainPosition[] positions) {
        if (positions == null)
            return (X[]) Array.newInstance(objectClass, 0);

        X[] gameObjects = (X[]) Array.newInstance(objectClass, positions.length);
        for (int i = 0; i < positions.length; i++) {
            if (positions[i] == null)
                continue;
            X gameObject = getObjectFromClass(objectClass);
            gameObject.addComponent(new PositionComponent(positions[i]));

            gameObjects[i] = gameObject;
        }
        return gameObjects;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        GameObject that = (GameObject) o;
        return this.id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    public boolean isIgnoreAddCallback() {
        return this.ignoreAddCallback;
    }

    public void setIgnoreAddCallback(boolean ignoreAddCallback) {
        this.ignoreAddCallback = ignoreAddCallback;
    }

    public static Class<? extends GameObject> getClassFromName(String gameObjectName) {
        return UNIQUE_GAMEOBJECTS.stream().filter(clazz -> {
            String simpleName = clazz.getSimpleName();
            return simpleName.equalsIgnoreCase(gameObjectName);
        }).findFirst().orElse(null);
    }


    public static Entity createEntityFromGameObject(GameObject gameObject, boolean displayBoundingBoxes) {
        if (gameObject == null)
            return null;

        if (displayBoundingBoxes && !gameObject.hasComponent(BoundingBoxComponent.class))
            return null;
        int id = gameObject.getId();

        Vector3f pos = null;
        PreviewComponent previewComponent = gameObject.getComponent(PreviewComponent.class);
        PositionComponent positionComponent = gameObject.getComponent(PositionComponent.class);
        boolean preview = false;
        if (previewComponent != null && previewComponent.getPreviewPosition() != null) {
            TerrainPosition previewPosition = previewComponent.getPreviewPosition(); // = null if no preview
            if (previewPosition != null) {
                preview = true;
                pos = previewPosition.toVector3f();
            }
        } else if (positionComponent != null)
            pos = positionComponent.getPosition();

        if (pos == null)
            return null;
//        pos = pos.add(new Vector3f(0, 0.005f, 0));
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
            ModelEntity modelEntity;
            if (preview)
                modelEntity = previewComponent.getTexture();
            else if (displayBoundingBoxes)
                modelEntity = gameObject.getComponent(BoundingBoxComponent.class).getBoundingBox().toModelEntity();
            else {
                if (gameObject.hasComponent(SingleModelComponent.class))
                    modelEntity = gameObject.getComponent(SingleModelComponent.class).getModel();
                else
                    modelEntity = gameObject.getComponent(AnimatedModelComponent.class).getModel();
            }

            entity = new Entity(new ModelEntity(pos, direction, scale, modelEntity.getModel(), id));
        } else if (gameObject.hasComponent(MultipleModelsComponent.class)) {
            if (preview)
                entity = new Entity(
                        new ModelEntity(pos, direction, scale, previewComponent.getTexture().getModel(), id));
            else {
                Vector3f finalPos = pos;
                MultipleModelsComponent multipleModelsComponent = gameObject
                        .getComponent(MultipleModelsComponent.class);
                Map<String, Entry<AbstractModel, Offset>> concurrentModels = multipleModelsComponent.getConcurrentModels();
                List<ModelEntity> modelEntities = concurrentModels
                        .values().stream().map(entry -> {
                            ModelEntity modelEntity = new ModelEntity(entry.getKey().toModelEntity());
                            Vector3f offsetPosition = entry.getValue().getOffsetPosition();
                            Vector3f offsetRotation = entry.getValue().getOffsetRotation();
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
                                modelEntity.setRotation(
                                        Vector3f.add(offsetRotation, new Vector3f(0, direction.getDegree(), 0), null));
                            }
                            return modelEntity;
                        }).collect(Collectors.toList());
                entity = new Entity(modelEntities);
            }
        } else {
            return null;
        }

        entity.setPreview(preview);
        return entity;
    }
}