package scene.components.callbacks;

import scene.gameObjects.GameObject;
import util.math.Vector3f;

@FunctionalInterface
public interface AddComponentCallback {

    void onAddComponent(GameObject gameObject, Vector3f position);
}
