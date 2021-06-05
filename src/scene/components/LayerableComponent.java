package scene.components;

import java.util.Set;
import scene.gameObjects.GameObject;

public class LayerableComponent extends Component {

    protected Set<Class<? extends GameObject>> layerableGameObjectsClasses;

    /**
     * Meaning if a GameObject has this component, the GameObject can be placed even if the position it is placed in is
     * occupied
     */
    public LayerableComponent(Set<Class<? extends GameObject>> layerableGameObjectsClasses) {
        this.layerableGameObjectsClasses = layerableGameObjectsClasses;
    }

    public Set<Class<? extends GameObject>> getLayerableGameObjectsClasses() {
        return this.layerableGameObjectsClasses;
    }
}