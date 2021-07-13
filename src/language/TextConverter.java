package language;

import static util.Utils.ASSETS_PATH;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import util.Utils;

public class TextConverter {

    private static Language language;
    private static Language newLanguage;

    private static Map<Words, String> currentLanguage;

    public static void setNewLanguage(Language lang) {
        newLanguage = lang;
    }

    public static Language getLanguage() {
        return language;
    }

    public static Language getNewLanguage() {
        return newLanguage;
    }

    public static void loadLanguage(Language lang) {
        if (lang == Language.ENGLISH) {
            loadEnglishLanguage();
            return;
        }

        try (BufferedReader bufferedReader = new BufferedReader(
                new FileReader(ASSETS_PATH + "/" + lang.getLang() + ".conf"))) {

            String word;
            currentLanguage = new HashMap<>();
            language = lang;

            int i = 0;
            while ((word = bufferedReader.readLine()) != null) {
                if (!word.startsWith("#"))
                    currentLanguage.put(Words.values()[i++], word);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadEnglishLanguage() {
        language = Language.ENGLISH;

        currentLanguage = new HashMap<>();
        for (Words word : Words.values())
            currentLanguage.put(word, Utils.formatText(word.getString()));
    }

    public static String getWordInCurrentLanguage(String word) {
        Words wordFromString = Words.getWordFromString(Utils.formatText(word));
        if (!currentLanguage.containsKey(wordFromString))
            return word;

        return currentLanguage.get(wordFromString);
    }
}