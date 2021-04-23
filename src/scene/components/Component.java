package scene.components;

import scene.gameObjects.GameObject;

public interface Component {

    default void removeObject(GameObject gameObject) {

    }
}