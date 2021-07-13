package fontMeshCreator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import language.TextConverter;
import language.Words;
import renderEngine.fontRendering.TextMaster;
import util.math.Vector2f;

public class Text {

    private String textString;
    private float  fontSize;

    private int textMeshVao;
    private int vertexCount;

    private Vector2f position;
    private float    maxLineLength;

    private final FontType font;

    private boolean centerTextHorizontal;
    private boolean centerTextVertical;
    private boolean stringChanged;

    private float charWidth;
    private float edgeCharWidth;

    private boolean upperCase;

    private List<Line>  lines;
    private int         maxLines = Integer.MAX_VALUE;
    private List<Color> colors;

    private Vector2f topLeftCorner;
    private Vector2f bottomRightCorner;

    private int yOffset; // Used for scrolling, in amount of lines offset

    public Text(String textString, float fontSize, FontType font, Vector2f position, float maxLineLength,
            boolean centerTextHorizontal, boolean centerTextVertical, float charWidth, float edgeCharWidth,
            List<Color> colors) {
        this.textString = TextConverter.getWordInCurrentLanguage(textString);
        this.fontSize = fontSize;
        this.position = position;
        this.maxLineLength = maxLineLength;
        this.font = font;
        this.centerTextHorizontal = centerTextHorizontal;
        this.centerTextVertical = centerTextVertical;
        this.charWidth = charWidth;
        this.edgeCharWidth = edgeCharWidth;
        this.stringChanged = false;
        this.colors = colors;
        this.lines = new ArrayList<>();

        if (!this.textString.isEmpty())
            this.lines = font.getLoader().getLines(this);
    }

    public Text(Words word, float fontSize, FontType font, Vector2f position, float maxLineLength,
            boolean centerTextHorizontal) {
        this(word, fontSize, font, position, maxLineLength, centerTextHorizontal, Color.BLACK);
    }

    public Text(Words word, float fontSize, FontType font, Vector2f position, float maxLineLength,
            boolean centerTextHorizontal, Color color) {
        this(word.toString(), fontSize, font, position, maxLineLength, centerTextHorizontal, color);
    }

    public Text(String word, float fontSize, FontType font, Vector2f position, float maxLineLength,
            boolean centerTextHorizontal, List<Color> colors) {
        this(word, fontSize, font, position, maxLineLength, centerTextHorizontal, true, 0.48f,
                Math.max(0, 0.1f + (2f - fontSize) * 0.0625f), colors);
    }

    public Text(String word, float fontSize, FontType font, Vector2f position, float maxLineLength,
            boolean centerTextHorizontal, Color color) {
        this(word, fontSize, font, position, maxLineLength, centerTextHorizontal,
                new ArrayList<>(Collections.singletonList(color)));
    }

    public Text(String word, float fontSize, FontType font, Vector2f position, float maxLineLength,
            boolean centerTextHorizontal) {
        this(word, fontSize, font, position, maxLineLength, centerTextHorizontal, Color.BLACK);
    }

    public Text(String word, float fontSize, FontType font, List<Color> colors) {
        this(word, fontSize, font, null, 1.0f, true, colors);
    }

    public Text(String word, float fontSize, FontType font, Color color) {
        this(word, fontSize, font, null, 1.0f, true, color);
    }

    public Text(Words word, float fontSize, FontType font, Color color) {
        this(word.toString(), fontSize, font, null, 1.0f, true, color);
    }

    private Text(Text text) {
        this(text.getTextString(), text.getFontSize(), text.getFont(), text.getPosition(), text.getMaxLineLength(),
                text.isCenteredHorizontally(), text.isCenteredVertically(), text.getCharWidth(),
                text.getEdgeCharWidth(),
                text.getColors());
        setStringChanged(text.isStringChanged());
        setUpperCase(text.isUpperCase());
        setMaxLines(text.getMaxLines());
        this.lines = text.lines;
        this.colors = text.getColors();
        this.yOffset = text.getyOffset();
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
        if (c == '\t')
            return metaData.getTabWidth() * 2 * this.fontSize;

        Character character = metaData.getCharacter(c);
        if (character == null)
            return 0;

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
     * Set whether the text should be centered horizontally or not
     *
     * @param centered - whether the text should be centered horizontally or not
     */
    public void setCenteredHorizontally(boolean centered) {
        this.centerTextHorizontal = centered;
        this.stringChanged = true;
    }

    /**
     * Sets the whole text in one single color
     *
     * @param color the color to paint the text with
     */
    public void setColor(Color color) {
        if (this.colors.isEmpty())
            this.colors.add(color);
        else
            this.colors = this.colors.stream().map(c -> color).collect(Collectors.toList());
    }

    public void setColors(List<Color> colors) {
        this.colors = colors;
    }

    /**
     * Set whether the text should be centered vertically or not
     *
     * @param centered - whether the text should be centered vertically or not
     */
    public void setCenteredVertically(boolean centered) {
        this.centerTextVertical = centered;
        this.stringChanged = true;
    }

    public void setUpperCase(boolean upperCase) {
        this.upperCase = upperCase;
    }

    public boolean isUpperCase() {
        return this.upperCase;
    }

    public List<Color> getColors() {
        return this.colors;
    }

    /**
     * Set font size
     *
     * @param fontSize - font size
     */
    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    public int getNumberOfLines() {
        return this.lines.size();
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
     * @param maxLineLength - line max size
     */
    public void setMaxLineLength(float maxLineLength) {
        this.maxLineLength = maxLineLength;
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
     * @return {@code true} if the text should be centered horizontally.
     */
    public boolean isCenteredHorizontally() {
        return this.centerTextHorizontal;
    }

    /**
     * @return {@code true} if the text should be centered vertically.
     */
    public boolean isCenteredVertically() {
        return this.centerTextVertical;
    }

    /**
     * @return The maximum length of a line of this text.
     */
    public float getMaxLineLength() {
        return this.maxLineLength;
    }

    /**
     * @return The string of text.
     */
    public String getTextString() {
        return this.upperCase ? this.textString.toUpperCase(Locale.ROOT) : this.textString;
    }

    public void setTextString(String textString) {
        if (textString != null) {
            this.textString = textString;
            this.lines = font.getLoader().getLines(this);

            this.stringChanged = true;
        }
    }

    public void addTextString(String textString, Color color) {
        if (textString == null || textString.isEmpty() || textString.matches("[\\n\\r]+"))
            return;

        this.textString = this.textString + textString;
        this.colors.add(color);
        this.lines = font.getLoader().getLines(this);
//        if (this.lines.size() == 36)
//            System.out.println();
        this.stringChanged = true;
    }

    public Color getColor(int nbWords) {
        if (this.colors.isEmpty())
            return Color.BLACK;

        return nbWords <= this.colors.size() ? this.colors.get(nbWords - 1) : this.colors.get(this.colors.size() - 1);
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

    /**
     * Total text height, accounting for all lines
     *
     * @return text height
     */
    public double getTotalTextHeight() {
        return getLineTextHeight() * this.lines.size();
    }

    /**
     * Text height for one line
     *
     * @return line height
     */
    public double getLineTextHeight() {
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

    public List<Line> getLines() {
        return this.lines;
    }

    public void clear() {
        this.textString = "";
        this.colors.clear();
        this.lines.clear();

        this.stringChanged = true;
    }

    public Vector2f getTopLeftCorner() {
        return this.topLeftCorner;
    }

    public void setTopLeftCorner(Vector2f topLeftCorner) {
        this.topLeftCorner = topLeftCorner;
    }

    public Vector2f getBottomRightCorner() {
        return this.bottomRightCorner;
    }

    public void setBottomRightCorner(Vector2f bottomRightCorner) {
        this.bottomRightCorner = bottomRightCorner;
    }

    public int getyOffset() {
        return this.yOffset;
    }

    public void setyOffset(int yOffset) {
        this.yOffset = yOffset;

        this.stringChanged = true;
    }

    public int getMaxLines() {
        return this.maxLines;
    }

    public void setMaxLines(int maxLines) {
        this.maxLines = maxLines;
    }

    public Text copy() {
        return new Text(this);
    }
}
