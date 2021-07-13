package language;

import util.Utils;

public enum Language {
    ENGLISH("EN", Words.ENGLISH),
    FRENCH("FR", Words.FRENCH);

    private final String lang;
    private       Words  name;

    Language(String lang, Words name) {
        this.lang = lang;
        this.name = name;
    }

    public String getLang() {
        return this.lang;
    }

    public Words getName() {
        return this.name;
    }

    public void setName(Words name) {
        this.name = name;
    }

    public String getEnglishName() {
        return Utils.formatText(name());
    }

    public static int getNbSupportedLanguages() {
        return values().length;
    }

    public static Language getLanguage(String value) {
        for (Language language : Language.values()) {
            if (language.getLang().equals(value))
                return language;
        }

        return null;
    }
}