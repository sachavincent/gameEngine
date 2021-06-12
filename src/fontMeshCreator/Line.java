package fontMeshCreator;

import java.util.ArrayList;
import java.util.List;

public class Line {

    private final double maxLength;
    private final double spaceSize;

    private final List<Word> words = new ArrayList<>();

    private double currentLineLength = 0;

    /**
     * Creates an empty line.
     *
     * @param spaceWidth - the screen-space width of a space character.
     * @param fontSize - the size of font being used.
     * @param maxLength - the screen-space maximum length of a line.
     */
    protected Line(double spaceWidth, double fontSize, double maxLength) {
        this.spaceSize = spaceWidth * fontSize;
        this.maxLength = maxLength;
    }

    /**
     * Attempt to add a word to the line. If the line can fit the word in
     * without reaching the maximum line length then the word is added and the
     * line length increased.
     *
     * @param word - the word to try to add.
     * @return {@code true} if the word has successfully been added to the line.
     */
    protected boolean attemptToAddWord(Word word) {
        double additionalLength = word.getWordWidth();
        additionalLength += !words.isEmpty() ? spaceSize : 0;
        if (currentLineLength + additionalLength <= maxLength) {
            words.add(word);
            currentLineLength += additionalLength;
            return true;
        } else {
            return false;
        }
    }

    protected void addTailingSpaces(int nbSpaces) {
        this.currentLineLength += nbSpaces * this.spaceSize;
    }

    /**
     * @return The max length of the line.
     */
    protected double getMaxLength() {
        return this.maxLength;
    }

    /**
     * @return The current screen-space length of the line.
     */
    public double getLineLength() {
        return this.currentLineLength;
    }

    public double getSpaceSize() {
        return this.spaceSize;
    }

    /**
     * @return The list of words in the line.
     */
    protected List<Word> getWords() {
        return this.words;
    }

    public int getNbWords() {
        return this.words.size();
    }

    @Override
    public String toString() {
        return "Line{" +
                "maxLength=" + this.maxLength +
                ", spaceSize=" + this.spaceSize +
                ", words=" + this.words +
                ", currentLineLength=" + this.currentLineLength +
                '}';
    }
}
