package util;

import inputs.KeyBindings;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
                if (key == null || value.length() != 1)
                    continue;

                key.setValue(value.charAt(0));
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


            bufferedWriter.write("layout=" + KeyboardLayout.getCurrentKeyboardLayout().name());
            bufferedWriter.newLine();

            for (Key option : Key.KEYS) {
                bufferedWriter.write(option.keyBinding.getName() + "=" + (char) option.keyBinding.getKey());
                bufferedWriter.newLine();
            }
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

            bufferedWriter.write("layout=" + KeyboardLayout.getDefaultKeyboardLayout().name());
            bufferedWriter.newLine();

            for (Key option : Key.KEYS) {
                option.reset();

                bufferedWriter.write(option.keyBinding.getName() + "=" + (char) option.defaultValue);
                bufferedWriter.newLine();
            }
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

    static class Key {

        final static List<Key> KEYS = new ArrayList<>();

        final static Key DISPLAY_BOUNDING_BOXES = new Key(KeyBindings.DISPLAY_BOUNDING_BOXES, 'K');
        final static Key FORWARD                = new Key(KeyBindings.FORWARD, 'W');
        final static Key LEFT                   = new Key(KeyBindings.LEFT, 'A');
        final static Key BACKWARD               = new Key(KeyBindings.BACKWARD, 'S');
        final static Key RIGHT                  = new Key(KeyBindings.RIGHT, 'D');

        final KeyBindings keyBinding;
        final int         defaultValue; // Default value in the QWERTY layout

        private Key(KeyBindings keyBinding, int defaultValue) {
            this.keyBinding = keyBinding;
            this.defaultValue = defaultValue;

            KEYS.add(this);
        }

        void setValue(char value) {
//            char key;
//            KeyboardLayout currentKeyboardLayout = KeyboardLayout.getCurrentKeyboardLayout();
//
//            switch (value) {
//                case 'A':
//                    switch (currentKeyboardLayout) {
//                        case AZERTY:
//                            key = 'Q';
//                            break;
//                        default:
//                            key = value;
//                    }
//                    break;
//                case 'Q':
//                    switch (currentKeyboardLayout) {
//                        case AZERTY:
//                            key = 'A';
//                            break;
//                        default:
//                            key = value;
//                    }
//                    break;
//                case 'Z':
//                    switch (currentKeyboardLayout) {
//                        case AZERTY:
//                            key = 'W';
//                            break;
//                        case QWERTZ:
//                            key = 'Y';
//                            break;
//                        default:
//                            key = value;
//                    }
//                    break;
//                case 'Y':
//                    switch (currentKeyboardLayout) {
//                        case QWERTZ:
//                            key = 'Z';
//                            break;
//                        default:
//                            key = value;
//                    }
//                    break;
//                case 'W':
//                    switch (currentKeyboardLayout) {
//                        case AZERTY:
//                            key = 'Z';
//                            break;
//                        default:
//                            key = value;
//                    }
//                    break;
//                default:
//                    key = value;
//            }
            this.keyBinding.setKey(value);
        }

        static Key getKey(String name) {
            return KEYS.stream().filter(key -> key.keyBinding.getName().equalsIgnoreCase(name)).findFirst()
                    .orElse(null);
        }

        void reset() {
            this.keyBinding.setKey(this.defaultValue);
        }
    }

    public enum KeyboardLayout {
        QWERTY,
        AZERTY,
        QWERTZ;

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
    }
}
