package renderEngine.fontRendering;

import fontMeshCreator.FontType;
import fontMeshCreator.Text;
import fontMeshCreator.TextMeshData;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import renderEngine.Loader;

public class TextMaster {

    private       Map<FontType, Set<Text>> texts    = new HashMap<>();
    private final FontRenderer             renderer = new FontRenderer();

    private static TextMaster instance;

    public static TextMaster getInstance() {
        return instance == null ? (instance = new TextMaster()) : instance;
    }

    private TextMaster() {
    }

    public void render() {
        // reload texts if changed
        Map<FontType, Set<Text>> newTexts = new HashMap<>();
        texts.forEach((key, value) -> {
            Set<Text> toKeep = value.stream().filter(text -> !text.isStringChanged()).collect(Collectors.toSet());
            Set<Text> toChange = value.stream().filter(Text::isStringChanged).collect(Collectors.toSet());
            if (!toChange.isEmpty()) {
                toChange.forEach(this::loadText);
                toKeep.addAll(toChange);
            }

            newTexts.put(key, toKeep);
        });

        texts = newTexts;

//        GL13.glDisable(GL13.GL_MULTISAMPLE);
        renderer.render(texts);
//        GL13.glEnable(GL13.GL_MULTISAMPLE);
    }

    public void loadText(Text text) {
        if (text == null)
            throw new IllegalArgumentException("Invalid text.");

        FontType font = text.getFont();
        if (text.isStringChanged()) {
            text.setStringChanged(false);

            TextMeshData data = font.loadText(text);
            final int vao = Loader.getInstance()
                    .loadToVAO(data.getVertexPositions(), data.getTextureCoords(), data.getColors());
            final int vertexCount = data.getVertexCount();
            text.setMeshInfo(vao, vertexCount);
        }

        Set<Text> textBatch = texts.computeIfAbsent(font, k -> new HashSet<>());
        textBatch.add(text);
    }

    public void removeText(Text text) {
        if (text == null)
            return;

        Set<Text> textBatch = texts.get(text.getFont());
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