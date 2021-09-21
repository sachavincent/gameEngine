package scene.components.callbacks;

import scene.gameObjects.GameObject;

@FunctionalInterface
public interface ObjectPlacedCallback {

    void onObjPlaced(GameObject gameObject);

    default boolean isForEach() {
        return true;
    }
}
