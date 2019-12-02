package language;

public enum Language {
    ENGLISH("EN"),
    FRENCH("FR");

    private String lang;

    Language(String l) {
        this.lang = l;
    }

    public String getLang() {
        return this.lang;
    }
}