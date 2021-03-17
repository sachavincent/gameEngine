package engineTester;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import renderEngine.DisplayManager;
import renderEngine.DisplayManager.Resolution;
import renderEngine.DisplayManager.WindowType;

public class SettingsManager {

    public final static String SETTINGS_FILE = "assets/settings.conf";

    public static void loadSettings() {
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(SETTINGS_FILE);
            bufferedReader = new BufferedReader(fileReader);

            String line = bufferedReader.readLine();
            do {
                String[] parameters = line.split("=");
                String name = parameters[0];
                String value = parameters[1];

                Option<?> option = Option.getOption(name);
                if (option == null)
                    continue;

                option.setValue(value);
            } while ((line = bufferedReader.readLine()) != null);
        } catch (IOException | IndexOutOfBoundsException e) {
            e.printStackTrace();
            resetSettings();
        } finally {
            try {
                if (bufferedReader != null)
                    bufferedReader.close();
                if (fileReader != null)
                    fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
                resetSettings();
            }
            DisplayManager.showWindow();
        }
    }

    public static void saveSettings() {
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            fileWriter = new FileWriter(SETTINGS_FILE, false);
            bufferedWriter = new BufferedWriter(fileWriter);

            for (Option<?> option : Option.OPTIONS) {
                bufferedWriter.write(option.name + "=" + option.getCallback.onOptionGet().toString());
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            resetSettings();
        } finally {
            try {
                if (bufferedWriter != null)
                    bufferedWriter.close();
                if (fileWriter != null)
                    fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
                resetSettings();
            }
        }
    }

    public static void resetSettings() {
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            fileWriter = new FileWriter(SETTINGS_FILE, false);
            bufferedWriter = new BufferedWriter(fileWriter);

            for (Option<?> option : Option.OPTIONS) {
                option.reset();

                bufferedWriter.write(option.name + "=" + option.defaultValue);
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedWriter != null)
                    bufferedWriter.close();
                if (fileWriter != null)
                    fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static class Option<T> {

        final static List<Option<?>> OPTIONS = new ArrayList<>();

        final static Option<Integer> DISPLAY    = new Option<>("display", 0, DisplayManager::setScreen,
                () -> DisplayManager.indexCurrentScreen);
        final static Option<Integer>    FPS_CAP    = new Option<>("fpscap",
                300, DisplayManager::setFPS, () -> DisplayManager.MAX_FPS);
        final static Option<Integer> FULLSCREEN = new Option<>("window_type", WindowType.FULLSCREEN.ordinal(),
                value -> DisplayManager.setWindowType(WindowType.values()[value]), () -> DisplayManager.windowType.ordinal());
        final static Option<Boolean>    VSYNC      = new Option<>("vsync", true, DisplayManager::setVsync,
                () -> DisplayManager.vSync);
        final static Option<String>  RESOLUTION = new Option<>("resolution", "1920x1080", value -> {
            String[] dim = value.split("x");
            DisplayManager.setWindowSize(new Resolution(Integer.parseInt(dim[0]), Integer.parseInt(dim[1])));
        }, () -> DisplayManager.currentScreen.resolution.toString().replaceAll("\\s", ""));

        final T                    defaultValue;
        final String               name;
        final Class<T>             clazz;
        final SetOptionCallback<T> setCallback;
        final GetOptionCallback<T> getCallback;

        private Option(String name, T defaultValue, SetOptionCallback<T> setCallback,
                GetOptionCallback<T> getCallback) {
            this.name = name;
            this.defaultValue = defaultValue;
            this.setCallback = setCallback;
            this.getCallback = getCallback;
            this.clazz = (Class<T>) defaultValue.getClass();

            OPTIONS.add(this);
        }


        void setValue(String value) {
            T optionValue;
            if (this.clazz.isAssignableFrom(String.class)) {
                optionValue = (T) value;
            } else if (this.clazz.isAssignableFrom(Integer.class)) {
                optionValue = (T) Integer.valueOf(value);
            } else if (this.clazz.isAssignableFrom(Boolean.class)) {
                optionValue = (T) Boolean.valueOf(value);
            } else if (this.clazz.isAssignableFrom(Double.class)) {
                optionValue = (T) Double.valueOf(value);
            } else {
                throw new IllegalArgumentException("Bad type");
            }

            this.setCallback.onOptionSet(optionValue);
        }

        static Option<?> getOption(String name) {
            return OPTIONS.stream().filter(option -> option.name.equalsIgnoreCase(name)).findFirst().orElse(null);
        }

        void reset() {
            this.setCallback.onOptionSet(this.defaultValue);
        }
    }

    interface SetOptionCallback<T> {

        void onOptionSet(T value);
    }

    interface GetOptionCallback<T> {

        T onOptionGet();
    }

}
