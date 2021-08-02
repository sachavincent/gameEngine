package scene.components.callbacks;

import scene.gameObjects.GameObject;

@FunctionalInterface
public interface FrameRendereredCallback {

    /**
     * @param nbFrames since the last time this method returned true
     * @return true resets nbFrames back to 0
     */
    boolean onFrameRendered(GameObject gameObject, int nbFrames);
}
