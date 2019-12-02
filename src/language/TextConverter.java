package language;

import guis.exceptions.UnknownLanguageException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TextConverter {

    public final static Language ENGLISH = Language.ENGLISH;
    public final static Language FRENCH  = Language.FRENCH;

    public final static Language DEFAULT_LANGUAGE = ENGLISH;

    public static Language language;

    private static List<String> defaultLanguage;


    public static void loadLanguage(Language language) {
        if (defaultLanguage == null)
            loadDefaultLanguage();

        try (BufferedReader bufferedReader = new BufferedReader(
                new FileReader(new File("assets/" + language.getLang() + ".csv")))) {

            String line;
            List<String> words = new ArrayList<>();

            while ((line = bufferedReader.readLine()) != null) {
                words.add(line);
            }

            if (words.size() != defaultLanguage.size())
                throw new UnknownLanguageException("Word count different from default language");


            TextConverter.language = language;

            addWords(words);
        } catch (IOException e) {
            throw new UnknownLanguageException();
        }
    }

    private static void addWords(List<String> words) {
        if (language != DEFAULT_LANGUAGE) {
            int i = 0;

            while (i < words.size()) {
                LanguageAssets.getInstance().addWord(defaultLanguage.get(i), words.get(i));
                i++;
            }
        }
    }

    public static void loadDefaultLanguage() {
        if (defaultLanguage != null)
            return;

        defaultLanguage = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(
                new FileReader(new File("assets/" + DEFAULT_LANGUAGE.getLang() + ".csv")))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                defaultLanguage.add(line);

                LanguageAssets.getInstance().addWord(line, line);
            }
        } catch (IOException e) {
            throw new UnknownLanguageException();
        }
    }
}