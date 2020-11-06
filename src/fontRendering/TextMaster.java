package fontRendering;

import fontMeshCreator.FontType;
import fontMeshCreator.Text;
import fontMeshCreator.TextMeshData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import renderEngine.Loader;

public class TextMaster {

    private       Map<FontType, List<Text>> texts    = new HashMap<>();
    private final FontRenderer              renderer = new FontRenderer();

    private static TextMaster instance;

    public static TextMaster getInstance() {
        return instance == null ? (instance = new TextMaster()) : instance;
    }

    private TextMaster() {
    }

    public void render() {
        // reload texts if changed
        Map<FontType, List<Text>> newTexts = new HashMap<>();
        texts.forEach((key, value) -> {
            List<Text> toKeep = value.stream().filter(text -> !text.isStringChanged()).collect(Collectors.toList());
            List<Text> toChange = value.stream().filter(Text::isStringChanged).collect(Collectors.toList());
            if (!toChange.isEmpty()) {
                toChange.forEach(this::loadText);
                toKeep.addAll(toChange);
            }

            newTexts.put(key, toKeep);
        });

        if (!newTexts.equals(texts))
            texts = newTexts;

        renderer.render(texts);
    }

    public void loadText(Text text) {
        if (text == null)
            throw new IllegalArgumentException("Invalid text.");

        if (text.isStringChanged()) {
            System.out.println("string changed:" + text.getTextString());
            text.setStringChanged(false);
        }

        FontType font = text.getFont();
        TextMeshData data = font.loadText(text);
        final int vao = Loader.getInstance().loadToVAO(data.getVertexPositions(), data.getTextureCoords());
        text.setMeshInfo(vao, data.getVertexCount());
        List<Text> textBatch = texts.computeIfAbsent(font, k -> new ArrayList<>());
        textBatch.add(text);
    }

    public void removeText(Text text) {
        if (text == null)
            return;

        List<Text> textBatch = texts.get(text.getFont());
        if (textBatch == null)
            return;

        textBatch.remove(text);
        if (textBatch.isEmpty())
            texts.remove(text.getFont());
    }

    public void removeText() {
        texts = new HashMap<>();
    }

    public void cleanUp() {
        renderer.cleanUp();
    }

}
