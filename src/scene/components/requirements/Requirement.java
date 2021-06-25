package scene.components.requirements;

import java.util.Map;
import java.util.Objects;

public abstract class Requirement<Key, Value> implements Map.Entry<Key, Value> {

    protected final Key   key;
    protected       Value value;

    private final SetValueCallback<Value>  onSetValueCallback;
    private       MeetRequirementCallback  onMeetRequirementCallback;
    private       ClearRequirementCallback onClearRequirementCallback;

    public Requirement(Key key, Value value, SetValueCallback<Value> onSetValueCallback) {
        this.key = key;
        this.value = value;
        this.onSetValueCallback = onSetValueCallback;
    }

    @Override
    public Key getKey() {
        return this.key;
    }

    @Override
    public Value getValue() {
        return this.value;
    }

    @Override
    public Value setValue(Value value) {
        Value oldValue = this.value;
        this.onSetValueCallback.onSetValue(value);
        this.value = value;

        return oldValue;
    }

    public void setOnRequirementMetCallback(MeetRequirementCallback onMeetRequirementCallback) {
        this.onMeetRequirementCallback = onMeetRequirementCallback;
    }

    public abstract <X> boolean isRequirementMet(X... object);

    /**
     * Used when the requirement is met
     */
    protected void meet() {
        if (this.onMeetRequirementCallback != null)
            this.onMeetRequirementCallback.onRequirementMet(this);
    }

    /**
     * Used when the requirement is no longer met
     */
    protected void clear() {
        if (this.onClearRequirementCallback != null)
            this.onClearRequirementCallback.onRequirementCleared(this);
    }

    public void setOnClearRequirementCallback(ClearRequirementCallback onClearRequirementCallback) {
        this.onClearRequirementCallback = onClearRequirementCallback;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Requirement<?, ?> that = (Requirement<?, ?>) o;
        return Objects.equals(this.key, that.key) && Objects.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.key, this.value);
    }


    @FunctionalInterface
    public interface SetValueCallback<Value> {

        void onSetValue(Value value);
    }

    @FunctionalInterface
    public interface MeetRequirementCallback {

        void onRequirementMet(Requirement<?, ?> requirement);
    }

    @FunctionalInterface
    public interface ClearRequirementCallback {

        void onRequirementCleared(Requirement<?, ?> requirement);
    }
}