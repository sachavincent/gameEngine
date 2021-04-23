package scene.gameObjects;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import scene.Scene;
import scene.components.Component;
import scene.components.PositionComponent;
import scene.components.RendererComponent;
import scene.components.RoadComponent;
import terrains.TerrainPosition;

public abstract class GameObject {

    private static int ID;

    private final int id;

    protected final Map<String, Component> components;

    private final Scene scene;

    public GameObject() {
        this.id = ++ID;

        this.components = new HashMap<>();
        this.scene = Scene.getInstance();
    }

    public int getId() {
        return this.id;
    }

    public final void addComponent(Component component) {
        removeComponent(component.getClass());

        this.components.put(component.getClass().getName(), component);

        if (component instanceof PositionComponent) {
            Scene.getInstance().addGameObject(this);
            if (this.components.containsKey(RoadComponent.class.getName()))
                Scene.getInstance().getRoadGraph()
                        .addRoad(((PositionComponent) component).getPosition().toTerrainPosition());
            Scene.getInstance().updateRequirements();
        }
    }

    public final <T extends Component> T getComponent(Class<T> componentClass) {
        Object object = this.components.get(componentClass.getName());
        return object == null ? null : (T) object;
    }

    public final <T extends Component> void removeComponent(Class<T> componentClass) {
        Component removedComponent = this.components.remove(componentClass.getName());

        if (removedComponent instanceof PositionComponent) {
            Scene.getInstance().removeGameObject(this, ((PositionComponent) removedComponent).getPosition());
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


    public static GameObject getObjectFromClass(Class<? extends GameObject> objectClass) {
        try {
            return objectClass.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            System.out.println(objectClass.getName());
        }
        return null;
    }

    public final void destroy() {
        this.components.forEach((s, component) -> component.removeObject(this));
    }

    public static GameObject newInstance(Class<? extends GameObject> objectClass, TerrainPosition position) {
        GameObject gameObject = getObjectFromClass(objectClass);
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
}