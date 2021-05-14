package fontMeshCreator;

import java.awt.Color;
import java.util.Locale;
import java.util.Objects;
import language.TextConverter;
import language.Words;
import renderEngine.fontRendering.TextMaster;
import util.math.Vector2f;

/**
 * Represents a piece of text in the game.
 *
 * @author Karl
 */
public class Text {

    private String textString;
    private float  fontSize;

    private int   textMeshVao;
    private int   vertexCount;
    private Color color = Color.BLACK;

    private Vector2f position;
    private float    lineMaxSize;
    private int      numberOfLines;

    private final FontType font;

    private boolean centerText;
    private boolean stringChanged;

    private float charWidth     = 0.5f;
    private float edgeCharWidth = 0.1f;

    private boolean upperCase;

    /**
     * Creates a new text, loads the text's quads into a VAO, and adds the text
     * to the screen.
     *
     * @param word - the word.
     * @param fontSize - the font size of the text, where a font size of 1 is the
     * default size.
     * @param font - the font that this text should use.
     * @param position - the position on the screen where the top left corner of the
     * text should be rendered. The top left corner of the screen is
     * (0, 0) and the bottom right is (1, 1).
     * @param maxLineLength - basically the width of the virtual page in terms of screen
     * width (1 is full screen width, 0.5 is half the width of the
     * screen, etc.) Text cannot go off the edge of the page, so if
     * the text is longer than this length it will go onto the next
     * line. When text is centered it is centered into the middle of
     * the line, based on this line length value.
     * @param centered - whether the text should be centered or not.
     */
    public Text(Words word, float fontSize, FontType font, Vector2f position, float maxLineLength, boolean centered) {
        this.textString = word.toString();
        this.fontSize = fontSize;
        this.font = font;
        this.position = position;
        this.lineMaxSize = maxLineLength;
        this.centerText = centered;
        this.stringChanged = false;
    }

    public Text(Words word, float fontSize, FontType font, Color color) {
        this.textString = word.toString();
        this.fontSize = fontSize;
        this.font = font;
        this.stringChanged = false;

        this.color = color;
        this.centerText = true;
//        this.charWidth = fontSize / 2.5f;
        this.charWidth = 0.48f;
        this.edgeCharWidth = Math.max(0, 0.1f + (2f - fontSize) * 0.0625f);
    }

    public Text(String word, float fontSize, FontType font, Vector2f position, float maxLineLength, boolean centered) {
        this.textString = TextConverter.getWordInCurrentLanguage(word);
        this.fontSize = fontSize;
        this.font = font;
        this.position = position;
        this.lineMaxSize = maxLineLength;
        this.centerText = centered;
        this.stringChanged = false;
    }

    public Text(String word, float fontSize, FontType font, Color color) {
        this.textString = TextConverter.getWordInCurrentLanguage(word);
        this.fontSize = fontSize;
        this.font = font;
        this.stringChanged = false;
        this.centerText = true;

        this.color = color;

//        this.charWidth = fontSize / 2.5f;
        this.charWidth = 0.48f;
        this.edgeCharWidth = Math.max(0, 0.1f + (2f - fontSize) * 0.0625f);
    }

    /**
     * Get width of given character
     *
     * @param c character
     * @return width
     */
    public double getCharacterWidth(int c) {
        MetaFile metaData = this.font.getLoader().getMetaData();

        if (c == ' ')
            return metaData.getSpaceWidth() * 2 * this.fontSize;

        Character character = metaData.getCharacter(c);
        if (character == null)
            return 0; //TODO: Accents ne fonctionnent pas

        return character.getxAdvance() * 2 * this.fontSize;
    }

    /**
     * Remove the text from the screen.
     */
    public void remove() {
        TextMaster.getInstance().removeText(this);
    }

    /**
     * @return The font used by this text.
     */
    public FontType getFont() {
        return this.font;
    }

    /**
     * Set the color of the text.
     *
     * @param r - red value, between 0 and 1.
     * @param g - green value, between 0 and 1.
     * @param b - blue value, between 0 and 1.
     */
    public void setColor(float r, float g, float b) {
        this.color = new Color(r, g, b);
    }

    /**
     * Set the color of the text.
     *
     * @param color - color of the value
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Set whether the text should be centered or not
     *
     * @param centered - whether the text should be centered or not
     */
    public void setCentered(boolean centered) {
        this.centerText = centered;
    }

    /**
     * @return the color of the text.
     */
    public Color getColor() {
        return this.color;
    }

    public void setUpperCase(boolean upperCase) {
        this.upperCase = upperCase;
    }

    public boolean isUpperCase() {
        return this.upperCase;
    }

    /**
     * Set font size
     *
     * @param fontSize - font size
     */
    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    /**
     * @return The number of lines of text. This is determined when the text is
     * loaded, based on the length of the text and the max line length
     * that is set.
     */
    public int getNumberOfLines() {
        return this.numberOfLines;
    }

    /**
     * @return The position of the top-left corner of the text in screen-space.
     * (0, 0) is the top left corner of the screen, (1, 1) is the bottom
     * right.
     */
    public Vector2f getPosition() {
        return this.position;
    }

    /**
     * @return the ID of the text's VAO, which contains all the vertex data for
     * the quads on which the text will be rendered.
     */
    public int getMesh() {
        return this.textMeshVao;
    }

    /**
     * Set the VAO and vertex count for this text.
     *
     * @param vao - the VAO containing all the vertex data for the quads on
     * which the text will be rendered.
     * @param verticesCount - the total number of vertices in all of the quads.
     */
    public void setMeshInfo(int vao, int verticesCount) {
        this.textMeshVao = vao;
        this.vertexCount = verticesCount;
    }

    /**
     * Set the line max size
     *
     * @param lineMaxSize - line max size
     */
    public void setLineMaxSize(float lineMaxSize) {
        this.lineMaxSize = lineMaxSize;
    }

    /**
     * Set the position for this text
     *
     * @param position - the position
     */
    public void setPosition(Vector2f position) {
        this.position = new Vector2f((position.x / 2 + 0.5), (position.y / 2 + 0.5));

        this.stringChanged = true;
    }

    /**
     * @return The total number of vertices of all the text's quads.
     */
    public int getVertexCount() {
        return this.vertexCount;
    }

    /**
     * @return the font size of the text (a font size of 1 is normal).
     */
    public float getFontSize() {
        return this.fontSize;
    }

    /**
     * Sets the number of lines that this text covers (method used only in
     * loading).
     */
    protected void setNumberOfLines(int number) {
        this.numberOfLines = number;
    }

    /**
     * @return {@code true} if the text should be centered.
     */
    public boolean isCentered() {
        return this.centerText;
    }

    /**
     * @return The maximum length of a line of this text.
     */
    protected float getMaxLineSize() {
        return this.lineMaxSize;
    }

    /**
     * @return The string of text.
     */
    public String getTextString() {
        return this.upperCase ? this.textString.toUpperCase(Locale.ROOT) : this.textString;
    }

    public void setTextString(String textString) {
        this.textString = textString;

        this.stringChanged = true;
    }

    public void setTextString(char textString) {
        setTextString(String.valueOf(textString));
    }

    public boolean isStringChanged() {
        return this.stringChanged;
    }

    public void setStringChanged(boolean stringChanged) {
        this.stringChanged = stringChanged;
    }

    public double getTextHeight() {
        return TextMeshCreator.LINE_HEIGHT * this.fontSize * 2;
    }

    public float getCharWidth() {
        return this.charWidth;
    }

    public float getEdgeCharWidth() {
        return this.edgeCharWidth;
    }

    public void setCharWidth(float charWidth) {
        this.charWidth = charWidth;

        this.stringChanged = true;
    }

    public void setEdgeCharWidth(float edgeCharWidth) {
        this.edgeCharWidth = edgeCharWidth;

        this.stringChanged = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Text text = (Text) o;
        return this.textMeshVao == text.textMeshVao;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.textMeshVao);
    }
}
