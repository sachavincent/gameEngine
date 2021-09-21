package scene.components;

import static java.util.Map.Entry;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import models.AbstractModel;
import util.Offset;
import util.math.Vector3f;

/**
 * Used if a GameObject has several models at the same time
 * = A combination of models
 */
public class MultipleModelsComponent extends Component {

    private final Map<String, Entry<AbstractModel, Offset>> concurrentModels;

    public MultipleModelsComponent() {
        this.concurrentModels = new LinkedHashMap<>();
    }

    public Map<String, Entry<AbstractModel, Offset>> getConcurrentModels() {
        return Collections.unmodifiableMap(this.concurrentModels);
    }

    public boolean hasConcurrentModel(String name) {
        return this.concurrentModels.containsKey(name);
    }

    public void addConcurrentModel(String name, AbstractModel model, Offset offset) {
        if (name == null || model == null)
            return;

        name = name.toUpperCase(Locale.ROOT);
        this.concurrentModels.put(name, new AbstractMap.SimpleEntry<>(model, offset));
    }

    public void addConcurrentModel(String name, AbstractModel model) {
        addConcurrentModel(name, model, new Offset());
    }

    public boolean replaceConcurrentModel(String name, AbstractModel newModel) {
        if (name == null || newModel == null)
            return false;

        name = name.toUpperCase(Locale.ROOT);
        if (this.concurrentModels.containsKey(name)) {
            Entry<AbstractModel, Offset> entry = this.concurrentModels.get(name);
            this.concurrentModels.put(name, new AbstractMap.SimpleEntry<>(newModel, entry.getValue()));
        }

        return true;
    }

    public void removeConcurrentModel(String name) {
        this.concurrentModels.remove(name);
    }

    public Entry<AbstractModel, Offset> getModelFromName(String name) {
        if (name == null)
            return null;

        name = name.toUpperCase(Locale.ROOT);
        if (this.concurrentModels.containsKey(name))
            return this.concurrentModels.get(name);

        return null;
    }

    public int getOffsetRotation(String name) {
        Entry<AbstractModel, Offset> modelEntityEntry = getModelFromName(name);
        if (modelEntityEntry != null)
            return modelEntityEntry.getValue().getOffsetRotation();

        return 0;
    }

    public Vector3f getOffsetPosition(String name) {
        Entry<AbstractModel, Offset> modelEntityEntry = getModelFromName(name);
        if (modelEntityEntry != null)
            return modelEntityEntry.getValue().getOffsetPosition();

        return new Vector3f();
    }

    public float getOffsetScale(String name) {
        Entry<AbstractModel, Offset> modelEntityEntry = getModelFromName(name);
        if (modelEntityEntry != null)
            return modelEntityEntry.getValue().getOffsetScale();

        return 0;
    }

    public void setOffsetsRotation(String name, int offsetRotation) {
        Entry<AbstractModel, Offset> modelEntityEntry = getModelFromName(name);
        modelEntityEntry.getValue().setOffsetRotation(offsetRotation);
    }

    public void setOffsetsPosition(String name, Vector3f offsetPosition) {
        Entry<AbstractModel, Offset> modelEntityEntry = getModelFromName(name);
        modelEntityEntry.getValue().setOffsetPosition(offsetPosition);
    }

    public void setOffsetsScale(String name, float offsetScale) {
        Entry<AbstractModel, Offset> modelEntityEntry = getModelFromName(name);
        modelEntityEntry.getValue().setOffsetScale(offsetScale);
    }
}