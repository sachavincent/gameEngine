package scene.components;

import entities.Model;
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

    private final Map<String, Model> concurrentModels;

    public MultipleModelsComponent() {
        this.concurrentModels = new LinkedHashMap<>();
    }

    public Map<String, Model> getConcurrentModels() {
        return Collections.unmodifiableMap(this.concurrentModels);
    }

    public boolean hasConcurrentModel(String name) {
        return this.concurrentModels.containsKey(name);
    }

    public void addConcurrentModel(String name, Model model) {
        if (name == null || model == null)
            return;

        name = name.toUpperCase(Locale.ROOT);
        this.concurrentModels.put(name, model);
    }

    public boolean replaceConcurrentModels(String name, Model model) {
        if (name == null || model == null)
            return false;

        name = name.toUpperCase(Locale.ROOT);
        if (this.concurrentModels.containsKey(name))
            this.concurrentModels.put(name, model);

        return true;
    }


    public void removeConcurrentModel(String name) {
        this.concurrentModels.remove(name);
    }

    public Model getModelFromName(String name) {
        if (name == null)
            return null;

        name = name.toUpperCase(Locale.ROOT);
        if (this.concurrentModels.containsKey(name))
            return this.concurrentModels.get(name);

        return null;
    }

    public Vector3f getOffsetRotation(String name) {
        Model model = getModelFromName(name);
        if (model != null)
            return model.getRotation();

        return new Vector3f();
    }

    public Vector3f getOffsetPosition(String name) {
        Model model = getModelFromName(name);
        if (model != null)
            return model.getPosition();

        return new Vector3f();
    }

    public void setOffsetsRotation(String name, Vector3f offsetRotation) {
        Model model = getModelFromName(name);
        model.setRotation(offsetRotation);
    }

    public void setOffsetsPosition(String name, Vector3f offsetPosition) {
        Model model = getModelFromName(name);
        model.setPosition(offsetPosition);
    }
}
