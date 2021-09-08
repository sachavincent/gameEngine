package scene.components.callbacks;

import scene.components.RendererComponent;
import scene.gameObjects.GameObject;

@FunctionalInterface
public interface UpdateComponentCallback {

    void onUpdateComponent(GameObject gameObject);

    default void onUpdateComponentDefault(GameObject gameObject) {
        if (gameObject.hasComponent(RendererComponent.class)) {
            gameObject.getComponent(RendererComponent.class).getRenderer().removeGameObject(gameObject);
        }
    }
}