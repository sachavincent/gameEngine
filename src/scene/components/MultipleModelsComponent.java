package scene.components;

import entities.ModelEntity;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import util.math.Vector3f;

/**
 * Used if a GameObject has several models at the same time
 * = A combination of models
 */
public class MultipleModelsComponent extends Component {

    private final Map<String, ModelEntity> concurrentModels;

    public MultipleModelsComponent() {
        this.concurrentModels = new LinkedHashMap<>();
    }

    public Map<String, ModelEntity> getConcurrentModels() {
        return Collections.unmodifiableMap(this.concurrentModels);
    }

    public boolean hasConcurrentModel(String name) {
        return this.concurrentModels.containsKey(name);
    }

    public void addConcurrentModel(String name, ModelEntity modelEntity) {
        if (name == null || modelEntity == null)
            return;

        name = name.toUpperCase(Locale.ROOT);
        this.concurrentModels.put(name, modelEntity);
    }

    public boolean replaceConcurrentModels(String name, ModelEntity modelEntity) {
        if (name == null || modelEntity == null)
            return false;

        name = name.toUpperCase(Locale.ROOT);
        if (this.concurrentModels.containsKey(name))
            this.concurrentModels.put(name, modelEntity);

        return true;
    }


    public void removeConcurrentModel(String name) {
        this.concurrentModels.remove(name);
    }

    public ModelEntity getModelFromName(String name) {
        if (name == null)
            return null;

        name = name.toUpperCase(Locale.ROOT);
        if (this.concurrentModels.containsKey(name))
            return this.concurrentModels.get(name);

        return null;
    }

    public Vector3f getOffsetRotation(String name) {
        ModelEntity modelEntity = getModelFromName(name);
        if (modelEntity != null)
            return modelEntity.getRotation();

        return new Vector3f();
    }

    public Vector3f getOffsetPosition(String name) {
        ModelEntity modelEntity = getModelFromName(name);
        if (modelEntity != null)
            return modelEntity.getPosition();

        return new Vector3f();
    }

    public void setOffsetsRotation(String name, Vector3f offsetRotation) {
        ModelEntity modelEntity = getModelFromName(name);
        modelEntity.setRotation(offsetRotation);
    }

    public void setOffsetsPosition(String name, Vector3f offsetPosition) {
        ModelEntity modelEntity = getModelFromName(name);
        modelEntity.setPosition(offsetPosition);
    }
}
