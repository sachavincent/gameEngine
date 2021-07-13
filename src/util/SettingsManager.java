package util;

import static renderEngine.DisplayManager.FRAMERATE_INFINITE;
import static util.Utils.ASSETS_PATH;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import language.Language;
import language.TextConverter;
import renderEngine.DisplayManager;
import renderEngine.DisplayManager.DisplayMode;
import renderEngine.DisplayManager.Resolution;

public class SettingsManager {

    public final static String SETTINGS_FILE = "settings.conf";

    public static void loadSettings() {
        try (BufferedReader reader = new BufferedReader(new FileReader(ASSETS_PATH + "/" + SETTINGS_FILE))) {
            String line = reader.readLine();
            do {
                String[] parameters = line.split("=");
                String name = parameters[0];
                String value = parameters[1];

                Option<?> option = Option.getOption(name);
                if (option == null)
                    continue;

                option.setValue(value);
            } while ((line = reader.readLine()) != null);
        } catch (IOException | IndexOutOfBoundsException e) {
            e.printStackTrace();
            resetSettings();
        } finally {
            DisplayManager.showWindow();
        }
    }

    public static void saveSettings() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ASSETS_PATH + "/" + SETTINGS_FILE, false))) {
            for (Option<?> option : Option.OPTIONS) {
                if (!option.equals(Option.OPTIONS.get(0)))
                    writer.newLine();
                writer.write(option.name + "=" + option.getCallback.onOptionGet().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            resetSettings();
        }
    }

    public static void resetSettings() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ASSETS_PATH + "/" + SETTINGS_FILE, false))) {
            for (Option<?> option : Option.OPTIONS) {
                option.reset();

                if (!option.equals(Option.OPTIONS.get(0)))
                    writer.newLine();
                writer.write(option.name + "=" + option.defaultValue);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class Option<T> {

        final static List<Option<?>> OPTIONS = new ArrayList<>();

        final static Option<Integer> DISPLAY = new Option<>("display", DisplayMode.defaultMode().ordinal(),
                DisplayManager::setScreen, () -> DisplayManager.indexCurrentScreen);

        final static Option<String> FPS_CAP = new Option<>("fpscap",
                "60", DisplayManager::setFPS, () -> {
            if (DisplayManager.FRAMERATE_LIMIT == Integer.MAX_VALUE)
                return FRAMERATE_INFINITE;

            return String.valueOf(DisplayManager.FRAMERATE_LIMIT);
        });

        final static Option<Integer> FULLSCREEN = new Option<>("display_mode", DisplayMode.FULLSCREEN.ordinal(),
                value -> DisplayManager.setDisplayMode(DisplayMode.values()[value]),
                () -> DisplayManager.displayMode.ordinal());

        final static Option<Boolean> VSYNC = new Option<>("vsync", true, DisplayManager::setVsync,
                () -> DisplayManager.VSYNC_ENABLED);

        final static Option<String> RESOLUTION = new Option<>("resolution", "1920x1080", value -> {
            String[] dim = value.split("x");
            DisplayManager.setWindowSize(new Resolution(Integer.parseInt(dim[0]), Integer.parseInt(dim[1])));
        }, () -> DisplayManager.currentScreen.resolution.toString().replaceAll("\\s", ""));

        final static Option<String> LANGUAGE = new Option<>("language", Language.ENGLISH.getLang(),
                value -> TextConverter.loadLanguage(Language.getLanguage(value)),
                () -> TextConverter.getNewLanguage() == null ? TextConverter.getLanguage().getLang()
                        : TextConverter.getNewLanguage().getLang());

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
