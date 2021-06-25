package scene.components;

import java.util.HashSet;
import scene.Scene;
import scene.components.callbacks.AddComponentCallback;
import scene.components.callbacks.TickElapsedCallback;
import scene.components.callbacks.UpdateComponentCallback;
import scene.gameObjects.GameObject;

public abstract class Component {

    protected int                     idGameObject;
    private   AddComponentCallback    onAddComponentCallback;
    private   UpdateComponentCallback onUpdateComponentCallback;
    private   TickElapsedCallback     onTickElapsedCallback;
    private   int                     nbTicksTickElapsedCallback;

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

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public boolean isUpdated() {
        return this.updated;
    }

    final public void update() {
        if (this.onUpdateComponentCallback != null) {
            GameObject gameObjectFromId = Scene.getInstance().getGameObjectFromId(this.idGameObject);
            if (gameObjectFromId != null)
                this.onUpdateComponentCallback.onUpdateComponent(gameObjectFromId);
        }
    }

    final public void tick() {
        if (this.onTickElapsedCallback != null) {
            boolean ticked = this.onTickElapsedCallback
                    .onTickElapsed(Scene.getInstance().getGameObjectFromId(this.idGameObject),
                            this.nbTicksTickElapsedCallback++);
            if (ticked)
                this.nbTicksTickElapsedCallback = 0;
        }
    }
}