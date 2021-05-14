package inputs;

import java.util.Map;
import java.util.Objects;
import util.KeybindingsManager.KeyboardLayout;

public class KeyInput implements Map.Entry<Character, KeyModifiers> {

    private char         key;
    private KeyModifiers keyModifiers;

    public KeyInput(char key, KeyModifiers keyModifiers) {
        this.key = key;
        this.keyModifiers = keyModifiers;
    }

    public KeyInput(char key) {
        this.key = key;
        this.keyModifiers = KeyModifiers.NONE;
    }

    private KeyInput(KeyInput keyInput) {
        this.key = keyInput.key;
        this.keyModifiers = keyInput.keyModifiers;
    }

    @Override
    public Character getKey() {
        return this.key;
    }

    @Override
    public KeyModifiers getValue() {
        return this.keyModifiers;
    }

    public void setKey(char key) {
        this.key = key;
    }

    @Override
    public KeyModifiers setValue(KeyModifiers value) {
        KeyModifiers oldValue = this.keyModifiers;
        this.keyModifiers = value;

        return oldValue;
    }

    @Override
    public String toString() {
        return "KeyInput{" +
                "key=" + this.key +
                ", keyModifiers=" + this.keyModifiers +
                '}';
    }

    public String formatKeyInput() {
        String keyModifierName = this.keyModifiers.formatName();

        return keyModifierName.isEmpty() ? String.valueOf(this.key) : keyModifierName + "+" + this.key;
    }

    public KeyInput toLocalKeyboardLayout() {
        KeyInput keyInput = new KeyInput(this);
        keyInput.key = KeyboardLayout.getKeyToCurrentLayout(this.key);

        return keyInput;
    }

    public KeyInput toDefaultKeyboardLayout() {
        KeyInput keyInput = new KeyInput(this);
        keyInput.key = KeyboardLayout.getKeyToDefaultLayout(this.key);

        return keyInput;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        KeyInput keyInput = (KeyInput) o;
        return this.key == keyInput.key && this.keyModifiers == keyInput.keyModifiers;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.key, this.keyModifiers);
    }
}
