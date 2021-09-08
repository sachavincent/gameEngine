package util;

import static util.Utils.ASSETS_PATH;

import inputs.Key;
import inputs.KeyInput;
import inputs.KeyModifiers;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class KeybindingsManager {

    public static final String SETTINGS_FILE = "keybindings.conf";

    public static void loadKeyBindings() {
        try (BufferedReader reader = new BufferedReader(new FileReader(ASSETS_PATH + "/" + SETTINGS_FILE))) {
            String line;

            while ((line = reader.readLine()) != null) {
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

                Key key = Key.getKeyFromName(name);
                if (key == null)
                    continue;

                key.setValue(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
            resetKeyBindings();
        }
    }

    public static void saveKeyBindings() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ASSETS_PATH + "/" + SETTINGS_FILE, false))) {
            for (Key option : Key.KEYS) {
                writer.write(option.getName() + "=" + option.getKeyInput().formatKeyInput());
                writer.newLine();
            }

            writer.write("layout=" + KeyboardLayout.getCurrentKeyboardLayout().name());
        } catch (IOException e) {
            e.printStackTrace();
            resetKeyBindings();
        }
    }

    public static void resetKeyBindings() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ASSETS_PATH + "/" + SETTINGS_FILE, false))) {
            for (Key key : Key.KEYS) {
                key.reset();

                writer.write(key.getName() + "=" + key.getDefaultKeyInput().formatKeyInput());
                writer.newLine();
            }

            writer.write("layout=" + KeyboardLayout.getDefaultKeyboardLayout().name());
        } catch (IOException e) {
            e.printStackTrace();
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
                if (key.matches("^MOUSEBUTTON[3-9]$")) {
                    keyInput = new KeyInput((char) ((int) (key.charAt(key.length() - 1)) - '0'));
                } else {
                    String[] split = key.split("\\+");
                    if (split.length == 2) { // Only one modifier
                        keyInput = new KeyInput(split[1].charAt(0),
                                KeyModifiers.getKeyModifierFromName(split[0]));
                    } else { // 2 modifiers
                        keyInput = new KeyInput(split[2].charAt(0),
                                KeyModifiers.getKeyModifierFromName(split[0] + "_" + split[1]));
                    }
                }
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return keyInput;
    }

    public enum KeyboardLayout {
        QWERTY,
        AZERTY,
        QWERTZ;

        // Key = in QWERTY, Value = in AZERTY
        private static final Map<KeyInput, KeyInput>                      azertyMappings = new HashMap<>();
        // Key = in QWERTY, Value = in QWERTZ
        private static final Map<KeyInput, KeyInput>                      qwertzMappings = new HashMap<>();
        private static final Map<KeyboardLayout, Map<KeyInput, KeyInput>> keyMappings    = new HashMap<>();

        static {
            azertyMappings.put(new KeyInput('Q'), new KeyInput('A'));
            azertyMappings.put(new KeyInput('A'), new KeyInput('Q'));
            azertyMappings.put(new KeyInput('Z'), new KeyInput('W'));
            azertyMappings.put(new KeyInput('W'), new KeyInput('Z'));
            azertyMappings.put(new KeyInput('M'), new KeyInput(','));
            azertyMappings.put(new KeyInput(';'), new KeyInput('M'));
            azertyMappings.put(new KeyInput('`'), new KeyInput('²'));
            azertyMappings.put(new KeyInput('/'), new KeyInput('!'));
            azertyMappings.put(new KeyInput('1', KeyModifiers.LSHIFT, 2), new KeyInput('1'));
            azertyMappings.put(new KeyInput('1', KeyModifiers.RSHIFT, 2), new KeyInput('1'));
            azertyMappings.put(new KeyInput('.', KeyModifiers.LSHIFT), new KeyInput('/'));
            azertyMappings.put(new KeyInput('.', KeyModifiers.RSHIFT), new KeyInput('/'));
            keyMappings.put(AZERTY, azertyMappings);

            qwertzMappings.put(new KeyInput('Z'), new KeyInput('Y'));
            qwertzMappings.put(new KeyInput('Y'), new KeyInput('Z'));
            keyMappings.put(QWERTZ, qwertzMappings);
        }

        public static KeyboardLayout currentKeyboardLayout;

        public static KeyboardLayout getDefaultKeyboardLayout() {
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
         * given keyInput is transformed to current layout
         *
         * @param keyInput key in QWERTY
         * @return keyInput in currentLayout
         */
        public static KeyInput getKeyToCurrentLayout(KeyInput keyInput) {
            KeyboardLayout currentKeyboardLayout = KeyboardLayout.getCurrentKeyboardLayout();
            if (currentKeyboardLayout == QWERTY)
                return keyInput;

            Map<KeyInput, KeyInput> mappings = keyMappings.get(currentKeyboardLayout);
            if (!mappings.containsKey(keyInput)) // Same key, no mapping
                return keyInput;

            return mappings.get(keyInput);
        }


        /**
         * given keyInput is transformed to default layout*
         *
         * @param keyInput must be in currentLayout
         * @return keyInput in QWERTY
         */
        public static KeyInput getKeyToDefaultLayout(KeyInput keyInput) {
            KeyboardLayout currentKeyboardLayout = KeyboardLayout.getCurrentKeyboardLayout();
            if (currentKeyboardLayout == QWERTY)
                return keyInput;

            Entry<KeyInput, KeyInput> resEntry = keyMappings.get(currentKeyboardLayout).entrySet()
                    .stream().filter(entry -> entry.getValue().equals(keyInput)).findFirst().orElse(null);

            if (resEntry != null)
                return resEntry.getKey();

            return keyInput;
        }
    }
}
