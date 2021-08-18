package scene.components;

import scene.Scene;
import scene.components.callbacks.AddComponentCallback;
import scene.components.callbacks.FrameRendereredCallback;
import scene.components.callbacks.TickElapsedCallback;
import scene.components.callbacks.UpdateComponentCallback;
import scene.gameObjects.GameObject;

import java.util.HashSet;

public abstract class Component {

    protected int idGameObject;
    private AddComponentCallback onAddComponentCallback;
    private UpdateComponentCallback onUpdateComponentCallback;

    private TickElapsedCallback onTickElapsedCallback;
    private int nbTicksTickElapsed;

    private FrameRendereredCallback onFrameRenderedCallback;
    private int nbFramesFrameRendered;

    protected boolean updated;

    public final int getIdGameObject() {
        return this.idGameObject;
    }

    public Component(AddComponentCallback onAddComponentCallback) {
        this();
        this.updated = false;
        this.onAddComponentCallback = onAddComponentCallback;
    }

    public Component() {
        if (!Scene.getInstance().getIdGameObjectsForComponents().containsKey(getClass()))
            Scene.getInstance().getIdGameObjectsForComponents().put(getClass(), new HashSet<>());

        this.onUpdateComponentCallback = gameObject -> {

        };
    }

    public void setId(int id) {
        this.idGameObject = id;
        Scene.getInstance().getIdGameObjectsForComponents().get(getClass()).add(id);
    }

    public final AddComponentCallback getOnAddComponentCallback() {
        return this.onAddComponentCallback;
    }

    public void setOnUpdateComponentCallback(UpdateComponentCallback onUpdateComponentCallback) {
        this.onUpdateComponentCallback = onUpdateComponentCallback;
    }

    public void setOnAddComponentCallback(AddComponentCallback onAddComponentCallback) {
        this.onAddComponentCallback = onAddComponentCallback;
    }

    public void setOnTickElapsedCallback(TickElapsedCallback onTickElapsedCallback) {
        this.onTickElapsedCallback = onTickElapsedCallback;
    }

    public void setOnFrameRenderedCallback(FrameRendereredCallback onFrameRenderedCallback) {
        this.onFrameRenderedCallback = onFrameRenderedCallback;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public boolean isUpdated() {
        return this.updated;
    }

    public final void update() {
        if (this.onUpdateComponentCallback != null) {
            GameObject gameObjectFromId = Scene.getInstance().getGameObjectFromId(this.idGameObject);
            if (gameObjectFromId != null) {
                this.onUpdateComponentCallback.onUpdateComponentDefault(gameObjectFromId);
                this.onUpdateComponentCallback.onUpdateComponent(gameObjectFromId);
            }
        }
    }

    public final void tick() {
        if (this.onTickElapsedCallback != null) {
            boolean ticked = this.onTickElapsedCallback
                    .onTickElapsed(Scene.getInstance().getGameObjectFromId(this.idGameObject),
                            this.nbTicksTickElapsed++);
            if (ticked)
                this.nbTicksTickElapsed = 0;
        }
    }

    public final void render() {
        if (this.onFrameRenderedCallback != null) {
            boolean rendered = this.onFrameRenderedCallback
                    .onFrameRendered(Scene.getInstance().getGameObjectFromId(this.idGameObject),
                            this.nbFramesFrameRendered++);
            if (rendered)
                this.nbFramesFrameRendered = 0;
        }
    }
}