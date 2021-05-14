package util;

import inputs.KeyBindings;
import inputs.KeyInput;
import inputs.KeyModifiers;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import renderEngine.DisplayManager;

public class KeybindingsManager {

    public final static String SETTINGS_FILE = "assets/keybindings.conf";

    public static void loadKeyBindings() {
        FileReader fileReader;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(SETTINGS_FILE);
            bufferedReader = new BufferedReader(fileReader);

            String line = bufferedReader.readLine();

            do {
                String[] parameters = line.split("=");
                String name = parameters[0];
                String value = parameters[1];

                if (name.equals("layout")) { // Layout
                    for (KeyboardLayout layout : KeyboardLayout.values())
                        if (layout.name().equals(value)) {
                            KeyboardLayout.setCurrentKeyboardLayout(KeyboardLayout.valueOf(value));
                            break;
                        }

                    if (KeyboardLayout.getCurrentKeyboardLayout() == null)
                        throw new IllegalArgumentException("Unknown layout: " + value);
                    continue;
                }

                Key key = Key.getKey(name);
                if (key == null)
                    continue;

                key.setValue(value);
            } while ((line = bufferedReader.readLine()) != null);
        } catch (IllegalArgumentException | IOException | IndexOutOfBoundsException e) {
            e.printStackTrace();
            resetKeyBindings();
        } finally {
            try {
                if (bufferedReader != null)
                    bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            DisplayManager.showWindow();
        }
    }

    public static void saveKeyBindings() {
        FileWriter fileWriter;
        BufferedWriter bufferedWriter = null;
        try {
            fileWriter = new FileWriter(SETTINGS_FILE, false);
            bufferedWriter = new BufferedWriter(fileWriter);

            for (Key option : Key.KEYS) {
                bufferedWriter
                        .write(option.keyBinding.getName() + "=" + option.keyBinding.getKeyInput().formatKeyInput());
                bufferedWriter.newLine();
            }

            bufferedWriter.write("layout=" + KeyboardLayout.getCurrentKeyboardLayout().name());
        } catch (IOException e) {
            e.printStackTrace();
            resetKeyBindings();
        } finally {
            try {
                if (bufferedWriter != null)
                    bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void resetKeyBindings() {
        FileWriter fileWriter;
        BufferedWriter bufferedWriter = null;
        try {
            fileWriter = new FileWriter(SETTINGS_FILE, false);
            bufferedWriter = new BufferedWriter(fileWriter);

            for (Key key : Key.KEYS) {
                key.reset();

                bufferedWriter.write(key.keyBinding.getName() + "=" + key.defaultKeyInput.formatKeyInput());
                bufferedWriter.newLine();
            }

            bufferedWriter.write("layout=" + KeyboardLayout.getDefaultKeyboardLayout().name());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedWriter != null)
                    bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Parses key
     *
     * @param key format: "LCTRL+ALT+M"
     */
    public static KeyInput parseKey(String key) {
        KeyInput keyInput = null;
        if (key.isEmpty())
            return null;

        try {
            if (key.length() == 1)
                keyInput = new KeyInput(key.charAt(0), KeyModifiers.NONE); // Only one character
            else {
                String[] split = key.split("\\+");
                if (split.length == 2) { // Only one modifier
                    keyInput = new KeyInput(split[1].charAt(0),
                            KeyModifiers.getKeyModifierFromName(split[0]));
                } else { // 2 modifiers
                    keyInput = new KeyInput(split[2].charAt(0),
                            KeyModifiers.getKeyModifierFromName(split[0] + "_" + split[1]));
                }
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return keyInput;
    }

    public static class Key {

        final static List<Key> KEYS = new ArrayList<>();

        final static Key DISPLAY_BOUNDING_BOXES = new Key(KeyBindings.DISPLAY_BOUNDING_BOXES, new KeyInput('K'));
        final static Key FORWARD                = new Key(KeyBindings.FORWARD, new KeyInput('W'));
        final static Key LEFT                   = new Key(KeyBindings.LEFT, new KeyInput('A'));
        final static Key BACKWARD               = new Key(KeyBindings.BACKWARD, new KeyInput('S'));
        final static Key RIGHT                  = new Key(KeyBindings.RIGHT, new KeyInput('D'));

        final KeyBindings keyBinding;
        final KeyInput    defaultKeyInput; // Default value in the QWERTY layout

        private Key(KeyBindings keyBinding, KeyInput defaultKeyInput) {
            this.keyBinding = keyBinding;
            this.defaultKeyInput = defaultKeyInput;

            KEYS.add(this);
        }

        public void setValue(String value) {
            KeyInput keyInput = parseKey(value);
            setValue(keyInput);
        }

        public void setValue(KeyInput keyInput) {
            this.keyBinding.setKeyInput(keyInput);
        }

        public static Key getKey(String name) {
            return KEYS.stream().filter(key -> key.keyBinding.getName().equalsIgnoreCase(name)).findFirst()
                    .orElse(null);
        }

        public KeyBindings getKeyBinding() {
            return this.keyBinding;
        }

        void reset() {
            this.keyBinding.setKeyInput(this.defaultKeyInput);
        }
    }

    public enum KeyboardLayout {
        QWERTY,
        AZERTY,
        QWERTZ;

        private static final Map<Character, Character>                      azertyMappings = new HashMap<>();
        private static final Map<Character, Character>                      qwertzMappings = new HashMap<>();
        private static final Map<KeyboardLayout, Map<Character, Character>> keyMappings    = new HashMap<>();

        static {
            azertyMappings.put('Q', 'A');
            azertyMappings.put('A', 'Q');
            azertyMappings.put('Z', 'W');
            azertyMappings.put('W', 'Z');
            azertyMappings.put('M', ',');
            azertyMappings.put(';', 'M');
            keyMappings.put(AZERTY, azertyMappings);

            qwertzMappings.put('Z', 'Y');
            qwertzMappings.put('Y', 'Z');
            keyMappings.put(QWERTZ, qwertzMappings);
        }

        public static KeyboardLayout currentKeyboardLayout;

        static KeyboardLayout getDefaultKeyboardLayout() {
            return QWERTY;
        }

        public static KeyboardLayout getCurrentKeyboardLayout() {
            return currentKeyboardLayout == null ? getDefaultKeyboardLayout() : currentKeyboardLayout;
        }

        public static void setCurrentKeyboardLayout(KeyboardLayout currentKeyboardLayout) {
            if (currentKeyboardLayout != null)
                KeyboardLayout.currentKeyboardLayout = currentKeyboardLayout;
        }

        public static KeyboardLayout getKeyboardLayoutFromName(String name) {
            for (KeyboardLayout layout : KeyboardLayout.values()) {
                if (layout.name().equals(name))
                    return layout;
            }

            return null;
        }

        /**
         * given key is transformed to current layout
         *
         * @param value key in QWERTY
         * @return key in currentLayout
         */
        public static char getKeyToCurrentLayout(char value) {
            KeyboardLayout currentKeyboardLayout = KeyboardLayout.getCurrentKeyboardLayout();
            if (currentKeyboardLayout == QWERTY)
                return value;

            Map<Character, Character> mappings = keyMappings.get(currentKeyboardLayout);
            if (!mappings.containsKey(value)) // Same key, no mapping
                return value;

            return mappings.get(value);
        }


        /**
         * @param value must be in currentLayout
         * @return key in QWERTY
         */
        public static char getKeyToDefaultLayout(char value) {
            KeyboardLayout currentKeyboardLayout = KeyboardLayout.getCurrentKeyboardLayout();
            if (currentKeyboardLayout == QWERTY)
                return value;

            Entry<Character, Character> resEntry = keyMappings.get(currentKeyboardLayout).entrySet()
                    .stream().filter(entry -> entry.getValue().equals(value)).findFirst().orElse(null);

            if (resEntry != null)
                return resEntry.getKey();

            return value;
        }
    }
}
