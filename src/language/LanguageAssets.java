package language;

import guis.exceptions.UnknownLanguageException;
import java.util.HashMap;
import java.util.Map;

public class LanguageAssets {

    private static Map<String, String> currentLanguage;

    private static LanguageAssets instance;

    public static LanguageAssets getInstance() {
        return instance == null ? (instance = new LanguageAssets()) : instance;
    }

    private LanguageAssets() {
        currentLanguage = new HashMap<>();
    }

    public static Map<String, String> getCurrentLanguage() {
        if (currentLanguage == null)
            throw new UnknownLanguageException();

        return currentLanguage;
    }

    public void addWord(String defaultWord, String word) {
        if (currentLanguage == null)
            throw new UnknownLanguageException();

        currentLanguage.put(defaultWord, word);
    }

    public String getWord(String key) {
        if (currentLanguage == null || !currentLanguage.containsKey(key)) {
            System.out.println("Couldn't find word: \"" + key + "\"");

            return key;
        }

        return currentLanguage.get(key);
    }
}
