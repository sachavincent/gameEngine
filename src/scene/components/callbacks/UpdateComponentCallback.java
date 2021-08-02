package scene.components.callbacks;

import scene.gameObjects.GameObject;

@FunctionalInterface
public interface UpdateComponentCallback {

    void onUpdateComponent(GameObject gameObject);
}
