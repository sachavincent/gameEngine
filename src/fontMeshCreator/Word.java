package fontMeshCreator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Word {

    private final List<Character> characters = new ArrayList<>();
    private final double          fontSize;
    private final Color           color;

    private double  width;
    private boolean startsWithTab;

    /**
     * Create a new empty word.
     *
     * @param fontSize - the font size of the text which this word is in.
     * @param color - the color in which the text is displayed
     */
    protected Word(double fontSize, Color color) {
        this.fontSize = fontSize;
        this.color = color;
    }

    /**
     * Adds a character to the end of the current word and increases the screen-space width of the word.
     *
     * @param character - the character to be added.
     */
    protected void addCharacter(Character character) {
        if (character != null) {
            this.characters.add(character);
            this.width += character.getxAdvance() * this.fontSize;
        }
    }

    protected void addTabulation(double width) {
        this.width += width * this.fontSize;
        this.startsWithTab = true;
    }

    /**
     * if true, word is starts with a tabulation (8 * space width)
     * else, it starts with 1 space
     *
     * @return this.startsWithTab
     */
    public boolean doesStartWithTab() {
        return this.startsWithTab;
    }

    /**
     * @return The list of characters in the word.
     */
    protected List<Character> getCharacters() {
        return this.characters;
    }

    /**
     * @return The width of the word in terms of screen size.
     */
    protected double getWordWidth() {
        return this.width;
    }

    /**
     * @return the color in which the test in displayed
     */
    public Color getColor() {
        return this.color;
    }

    @Override
    public String toString() {
        return "Word{" +
                "characters=" + this.characters +
                ", width=" + this.width +
                ", fontSize=" + this.fontSize +
                ", color=" + this.color +
                '}';
    }
}
