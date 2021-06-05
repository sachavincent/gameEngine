package scene.components.requirements;

import java.util.Map;
import java.util.Objects;

public class Requirement<Key, Value> implements Map.Entry<Key, Value> {

    protected final Key                     key;
    protected       Value                   value;
    protected       SetValueCallback<Value> onSetValueCallback;

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
}