package scene.components.callbacks;

import scene.gameObjects.GameObject;

@FunctionalInterface
public interface TickElapsedCallback {

    /**
     * @param nbTicks since the last time this method returned true
     * @return true resets nbTicks back to 0
     */
    boolean onTickElapsed(GameObject gameObject, int nbTicks);
}
