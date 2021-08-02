package scene.components;

import models.AbstractModel;
import util.math.Vector3f;

import java.util.*;

import static java.util.Map.Entry;

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

    public Vector3f getOffsetRotation(String name) {
        Entry<AbstractModel, Offset> modelEntityEntry = getModelFromName(name);
        if (modelEntityEntry != null)
            return modelEntityEntry.getValue().getOffsetRotation();

        return new Vector3f();
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

    public void setOffsetsRotation(String name, Vector3f offsetRotation) {
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

    public static class Offset {
        private Vector3f offsetPosition;
        private Vector3f offsetRotation;
        private float offsetScale;

        private final boolean fixedRotation;

        public Offset(boolean fixedRotation) {
            this(new Vector3f(), new Vector3f(), 0, fixedRotation);
        }

        public Offset() {
            this(false);
        }

        public Offset(Vector3f offsetPosition, Vector3f offsetRotation, float offsetScale) {
            this(offsetPosition, offsetRotation, offsetScale, false);
        }

        public Offset(Vector3f offsetPosition, Vector3f offsetRotation, float offsetScale, boolean fixedRotation) {
            this.offsetPosition = offsetPosition;
            this.offsetRotation = offsetRotation;
            this.offsetScale = offsetScale;
            this.fixedRotation = fixedRotation;
        }

        public Vector3f getOffsetPosition() {
            return this.offsetPosition;
        }

        public void setOffsetPosition(Vector3f offsetPosition) {
            this.offsetPosition = offsetPosition;
        }

        public Vector3f getOffsetRotation() {
            return this.offsetRotation;
        }

        public void setOffsetRotation(Vector3f offsetRotation) {
            this.offsetRotation = offsetRotation;
        }

        public float getOffsetScale() {
            return this.offsetScale;
        }

        public void setOffsetScale(float offsetScale) {
            this.offsetScale = offsetScale;
        }

        public boolean isFixedRotation() {
            return this.fixedRotation;
        }
    }
}
