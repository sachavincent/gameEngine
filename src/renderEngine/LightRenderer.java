package renderEngine;

import scene.gameObjects.GameObject;

public class LightRenderer extends Renderer {

    private static LightRenderer instance;

    public static LightRenderer getInstance() {
        return instance == null ? (instance = new LightRenderer()) : instance;
    }

    private LightRenderer() {
    }

    @Override
    public void render() {
    }

    public void prepareRender(GameObject gameObject) {
        this.gameObjects.add(gameObject);
    }
}