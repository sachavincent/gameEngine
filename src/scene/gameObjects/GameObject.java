package scene.gameObjects;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import scene.Scene;
import scene.components.Component;
import scene.components.PositionComponent;
import scene.components.RendererComponent;
import terrains.TerrainPosition;

public abstract class GameObject {

    private final static Set<Class<? extends GameObject>> UNIQUE_GAMEOBJECTS = new HashSet<>();

    private static int ID;

    private final int id;

    protected final Map<String, Component> components;

    private final Scene scene;

    public GameObject() {
        this.id = ++ID;

        this.components = new HashMap<>();
        this.scene = Scene.getInstance();

        UNIQUE_GAMEOBJECTS.add(this.getClass());
    }

    public int getId() {
        return this.id;
    }

    public final void addComponent(Component component) {
        component.setId(this.id);
        removeComponent(component.getClass());

        this.components.put(component.getClass().getName(), component);

        if (component instanceof PositionComponent) {
            Scene.getInstance().addGameObject(this);
            this.components.values().stream().map(Component::getAddComponentCallback).filter(Objects::nonNull)
                    .forEach(callback -> callback.onAddComponent(this, ((PositionComponent) component).getPosition()));
        }
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
            Set<Integer> integers = idGameObjectsForComponents.get(component.getClass());
            integers.remove(this.id);
        }
        Scene.getInstance().removeGameObject(this.id);

        if (hasComponent(RendererComponent.class))
            Scene.getInstance().removeRenderableGameObject(getComponent(RendererComponent.class).getRenderer(), this);
    }

    public static <X extends GameObject> X newInstance(Class<X> objectClass, TerrainPosition position) {
        X gameObject = getObjectFromClass(objectClass);
        gameObject.addComponent(new PositionComponent(position));

        return gameObject;
    }

    public static GameObject[] newInstances(Class<? extends GameObject> objectClass, TerrainPosition[] positions) {
        if (positions == null)
            return new GameObject[0];

        GameObject[] gameObjects = new GameObject[positions.length];
        for (int i = 0; i < positions.length; i++) {
            GameObject gameObject = getObjectFromClass(objectClass);
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

    public static Class<? extends GameObject> getClassFromName(String gameObjectName) {
        return UNIQUE_GAMEOBJECTS.stream().filter(clazz -> {
            String simpleName = clazz.getSimpleName();
            return simpleName.equalsIgnoreCase(gameObjectName);
        }).findFirst().orElse(null);
    }
}