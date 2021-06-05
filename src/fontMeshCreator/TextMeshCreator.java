package fontMeshCreator;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TextMeshCreator {

    public static final    double LINE_HEIGHT = 0.03f;
    public static final    int    SPACE_ASCII = 32;
    protected static final int    TAB_ASCII   = 9;

    private final MetaFile metaData;

    protected TextMeshCreator(File metaFile) {
        metaData = new MetaFile(metaFile);
    }

    protected TextMeshData createTextMesh(Text text) {
        List<Line> lines = createStructure(text);
        return createQuadVertices(text, lines);
    }

    public List<Line> getLines(Text text) {
        return createStructure(text);
    }

    private List<Line> createStructure(Text text) {
        List<Line> lines = new ArrayList<>();
        if (text == null)
            return lines;

        char[] chars = text.getTextString().toCharArray();
        if (chars.length == 0)
            return lines;

        Line currentLine = new Line(metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineLength());
        Word currentWord = new Word(text.getFontSize(), text.getColor(1));
        int nbWords = 1;
        for (char c : chars) {
            Color color = text.getColor(nbWords);
            if (c == '\n') { // \r\n or \n
                currentLine.attemptToAddWord(currentWord);
                lines.add(currentLine);
                currentLine = new Line(metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineLength());
                currentWord = new Word(text.getFontSize(), color);
                continue;
            }
            if ((int) c == SPACE_ASCII || (int) c == TAB_ASCII) {
                if ((int) c == TAB_ASCII)
                    currentWord.addTabulation(this.metaData.getTabWidth());
                if (currentWord.getCharacters().isEmpty()) {
                    if (currentLine.getNbWords() == 0)
                        currentLine.setNbSpacesBeforeLine(currentLine.getNbSpacesBeforeLine() + 1);
                    continue;
                }
                boolean added = currentLine.attemptToAddWord(currentWord);
                if (!added) {
                    lines.add(currentLine);
                    currentLine = new Line(metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineLength());
                    currentLine.attemptToAddWord(currentWord);
                }
                currentWord = new Word(text.getFontSize(), color);
                continue;
            }
            if (String.valueOf(c).matches(".")) {
                if (currentWord.getCharacters().isEmpty())
                    nbWords++;
                Character character = this.metaData.getCharacter(c);
                currentWord.addCharacter(character);
            }
        }
        completeStructure(lines, currentLine, currentWord, text);
        return lines;
    }


    private void completeStructure(List<Line> lines, Line currentLine, Word currentWord, Text text) {
        if (currentLine.getWords().isEmpty() && currentWord.getCharacters().isEmpty())
            return;

        boolean added = currentLine.attemptToAddWord(currentWord);
        if (!added) {
            lines.add(currentLine);
            currentLine = new Line(metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineLength());
            currentLine.attemptToAddWord(currentWord);
        }
        lines.add(currentLine);
    }

    private TextMeshData createQuadVertices(Text text, List<Line> lines) {
        double xCursor;
        double yCursor = -text.getyOffset() * text.getLineTextHeight() / 2;
        List<Float> vertices = new ArrayList<>();
        List<Float> textureCoords = new ArrayList<>();
        List<Float> colors = new ArrayList<>();
        for (Line line : lines) {
            if (line.doesStartWithSpaces())
                xCursor = line.getNbSpacesBeforeLine() * line.getSpaceSize();
            else
                xCursor = 0;

            if (text.isCenteredHorizontally())
                xCursor += (line.getMaxLength() - line.getLineLength()) / 2;

            for (Word word : line.getWords()) {
                if (word.doesStartWithTab())
                    xCursor += this.metaData.getTabWidth() * text.getFontSize();
                for (Character letter : word.getCharacters()) {
                    addVerticesForCharacter(xCursor, yCursor, letter, text.getFontSize(), vertices);
                    addTexCoords(textureCoords, letter.getxTextureCoord(), letter.getyTextureCoord(),
                            letter.getXMaxTextureCoord(), letter.getYMaxTextureCoord());
                    addColors(word.getColor(), colors);
                    xCursor += letter.getxAdvance() * text.getFontSize();
                }
                xCursor += this.metaData.getSpaceWidth() * text.getFontSize();
            }
            yCursor += text.getLineTextHeight() / 2;
        }
        return new TextMeshData(listToArray(vertices), listToArray(textureCoords), listToArray(colors));
    }

    private void addColors(Color value, List<Float> colors) {
        colors.add(value.getRed() / 255f);
        colors.add(value.getGreen() / 255f);
        colors.add(value.getBlue() / 255f);
        colors.add(value.getRed() / 255f);
        colors.add(value.getGreen() / 255f);
        colors.add(value.getBlue() / 255f);
        colors.add(value.getRed() / 255f);
        colors.add(value.getGreen() / 255f);
        colors.add(value.getBlue() / 255f);
        colors.add(value.getRed() / 255f);
        colors.add(value.getGreen() / 255f);
        colors.add(value.getBlue() / 255f);
        colors.add(value.getRed() / 255f);
        colors.add(value.getGreen() / 255f);
        colors.add(value.getBlue() / 255f);
        colors.add(value.getRed() / 255f);
        colors.add(value.getGreen() / 255f);
        colors.add(value.getBlue() / 255f);
    }

    private void addVerticesForCharacter(double curserX, double curserY, Character character, double fontSize,
            List<Float> vertices) {
        double x = curserX + (character.getxOffset() * fontSize);
        double y = curserY + (character.getyOffset() * fontSize);
        double maxX = x + (character.getSizeX() * fontSize);
        double maxY = y + (character.getSizeY() * fontSize);
        double properX = (2 * x) - 1;
        double properY = (-2 * y) + 1;
        double properMaxX = (2 * maxX) - 1;
        double properMaxY = (-2 * maxY) + 1;
        addVertices(vertices, properX, properY, properMaxX, properMaxY);
    }

    private static void addVertices(List<Float> vertices, double x, double y, double maxX, double maxY) {
        vertices.add((float) x);
        vertices.add((float) y);
        vertices.add((float) x);
        vertices.add((float) maxY);
        vertices.add((float) maxX);
        vertices.add((float) maxY);
        vertices.add((float) maxX);
        vertices.add((float) maxY);
        vertices.add((float) maxX);
        vertices.add((float) y);
        vertices.add((float) x);
        vertices.add((float) y);
    }

    private static void addTexCoords(List<Float> texCoords, double x, double y, double maxX, double maxY) {
        texCoords.add((float) x);
        texCoords.add((float) y);
        texCoords.add((float) x);
        texCoords.add((float) maxY);
        texCoords.add((float) maxX);
        texCoords.add((float) maxY);
        texCoords.add((float) maxX);
        texCoords.add((float) maxY);
        texCoords.add((float) maxX);
        texCoords.add((float) y);
        texCoords.add((float) x);
        texCoords.add((float) y);
    }


    private static float[] listToArray(List<Float> listOfFloats) {
        float[] array = new float[listOfFloats.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = listOfFloats.get(i);
        }
        return array;
    }

    public MetaFile getMetaData() {
        return this.metaData;
    }
}
