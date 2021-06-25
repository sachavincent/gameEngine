package scene.gameObjects;

import entities.Camera.Direction;
import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import scene.Scene;
import scene.components.Component;
import scene.components.DirectionComponent;
import scene.components.PositionComponent;
import scene.components.RendererComponent;
import scene.components.callbacks.AddComponentCallback;
import terrains.TerrainPosition;
import util.math.Vector3f;

public abstract class GameObject {

    private final static Set<Class<? extends GameObject>> UNIQUE_GAMEOBJECTS = new HashSet<>();

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
        component.setId(this.id);
        removeComponent(component.getClass());

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

    public void prepareRender() {
        RendererComponent renderer = getComponent(RendererComponent.class);
        if (renderer != null)
            renderer.getRenderer().prepareRender(this);
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

        if (hasComponent(RendererComponent.class))
            Scene.getInstance().removeRenderableGameObject(getComponent(RendererComponent.class).getRenderer(), this);
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
        gameObject.addComponent(new DirectionComponent(direction));
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
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
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
}