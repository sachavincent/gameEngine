package scene.components;

import engineTester.Rome;
import java.util.HashSet;
import scene.components.callbacks.FrameRendereredCallback;
import scene.components.callbacks.ObjectPlacedCallback;
import scene.components.callbacks.TickElapsedCallback;
import scene.components.callbacks.UpdateComponentCallback;
import scene.gameObjects.GameObject;

public abstract class Component {

    protected int                     idGameObject;
    private   ObjectPlacedCallback    onObjectPlacedCallback;
    private   UpdateComponentCallback onUpdateComponentCallback;

    private TickElapsedCallback onTickElapsedCallback;
    private int nbTicksTickElapsed;

    private FrameRendereredCallback onFrameRenderedCallback;
    private int nbFramesFrameRendered;

    protected boolean updated;

    public final int getIdGameObject() {
        return this.idGameObject;
    }

    public Component(ObjectPlacedCallback onObjectPlacedCallback) {
        this();
        this.updated = false;
        this.onObjectPlacedCallback = onObjectPlacedCallback;
    }

    public Component() {
        if (!Rome.getGame().getScene().getIdGameObjectsForComponents().containsKey(getClass()))
            Rome.getGame().getScene().getIdGameObjectsForComponents().put(getClass(), new HashSet<>());

        this.onUpdateComponentCallback = gameObject -> {

        };
    }

    public void setId(int id) {
        this.idGameObject = id;
        Rome.getGame().getScene().getIdGameObjectsForComponents().get(getClass()).add(id);
    }

    public final ObjectPlacedCallback getOnObjectPlacedCallback() {
        return this.onObjectPlacedCallback;
    }

    public void setOnUpdateComponentCallback(UpdateComponentCallback onUpdateComponentCallback) {
        this.onUpdateComponentCallback = onUpdateComponentCallback;
    }

    public void setOnObjectPlacedCallback(ObjectPlacedCallback onObjectPlacedCallback) {
        this.onObjectPlacedCallback = onObjectPlacedCallback;
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
            GameObject gameObjectFromId = Rome.getGame().getScene().getGameObjectFromId(this.idGameObject);
            if (gameObjectFromId != null) {
                this.onUpdateComponentCallback.onUpdateComponentDefault(gameObjectFromId);
                this.onUpdateComponentCallback.onUpdateComponent(gameObjectFromId);
            }
        }
    }

    public final void tick() {
        if (this.onTickElapsedCallback != null) {
            boolean ticked = this.onTickElapsedCallback
                    .onTickElapsed(Rome.getGame().getScene().getGameObjectFromId(this.idGameObject),
                            this.nbTicksTickElapsed++);
            if (ticked)
                this.nbTicksTickElapsed = 0;
        }
    }

    public final void render() {
        if (this.onFrameRenderedCallback != null) {
            boolean rendered = this.onFrameRenderedCallback
                    .onFrameRendered(Rome.getGame().getScene().getGameObjectFromId(this.idGameObject),
                            this.nbFramesFrameRendered++);
            if (rendered)
                this.nbFramesFrameRendered = 0;
        }
    }
}