package scene.components;

import java.util.HashSet;
import scene.Scene;
import scene.components.callbacks.AddComponentCallback;
import scene.components.callbacks.UpdateComponentCallback;

public abstract class Component {

    protected int                     idGameObject;
    protected AddComponentCallback    addComponentCallback;
    protected UpdateComponentCallback updateComponentCallback;

    public int getIdGameObject() {
        return this.idGameObject;
    }

    public Component(AddComponentCallback addComponentCallback) {
        this();
        this.addComponentCallback = addComponentCallback;
    }

    public Component() {
        if (!Scene.getInstance().getIdGameObjectsForComponents().containsKey(getClass()))
            Scene.getInstance().getIdGameObjectsForComponents().put(getClass(), new HashSet<>());
    }

    public void setId(int id) {
        this.idGameObject = id;
        Scene.getInstance().getIdGameObjectsForComponents().get(getClass()).add(id);
    }

    public AddComponentCallback getAddComponentCallback() {
        return this.addComponentCallback;
    }

    public UpdateComponentCallback getUpdateComponentCallback() {
        return this.updateComponentCallback;
    }

    protected void update() {
        if (this.updateComponentCallback != null)
            this.updateComponentCallback.onUpdateComponent(Scene.getInstance().getGameObjectFromId(this.idGameObject));
    }
}