package inputs;

import java.util.Map;
import java.util.Objects;
import util.KeybindingsManager.KeyboardLayout;

public class KeyInput implements Map.Entry<Character, KeyModifiers> {

    private final int  scancode; // Can be useful to differentiate numbers from numpad and keyboard
    private final char key;

    private KeyModifiers keyModifiers;

    public KeyInput(char key, KeyModifiers keyModifiers) {
        this.key = key;
        this.keyModifiers = keyModifiers;
        this.scancode = 0;
    }

    public KeyInput(char key, KeyModifiers keyModifiers, int scancode) {
        this.key = key;
        this.keyModifiers = keyModifiers;
        this.scancode = scancode;
    }

    public KeyInput(char key) {
        this.key = key;
        this.keyModifiers = KeyModifiers.NONE;
        this.scancode = 0;
    }

    private KeyInput(KeyInput keyInput) {
        this.key = keyInput.key;
        this.keyModifiers = keyInput.keyModifiers;
        this.scancode = 0;
    }

    @Override
    public Character getKey() {
        return this.key;
    }

    @Override
    public KeyModifiers getValue() {
        return this.keyModifiers;
    }

    public int getScancode() {
        return this.scancode;
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

        return keyModifierName.isEmpty() ? getKeyName() : keyModifierName + "+" + this.key;
    }

    public String getKeyName() {
        int keyValue = this.key;

        if (keyValue >= 3 && keyValue < 10)
            return "MOUSEBUTTON" + keyValue;

        return String.valueOf(this.key);
    }

    public KeyInput toLocalKeyboardLayout() {
        KeyInput keyInput = new KeyInput(this);
        keyInput = KeyboardLayout.getKeyToCurrentLayout(keyInput);

        return keyInput;
    }

    public KeyInput toDefaultKeyboardLayout() {
        KeyInput keyInput = new KeyInput(this);
        keyInput = KeyboardLayout.getKeyToDefaultLayout(keyInput);

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
