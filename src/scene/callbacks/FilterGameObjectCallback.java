package scene.callbacks;

import scene.gameObjects.GameObject;

@FunctionalInterface
public interface FilterGameObjectCallback {

    boolean onFilter(GameObject gameObject);
}
