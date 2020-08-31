package fontRendering;

import fontMeshCreator.FontType;
import fontMeshCreator.Text;
import fontMeshCreator.TextMeshData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import renderEngine.Loader;

public class TextMaster {

    private Map<FontType, List<Text>> texts    = new HashMap<>();
    private FontRenderer              renderer = new FontRenderer();

    private static TextMaster instance;

    public static TextMaster getInstance() {
        return instance == null ? (instance = new TextMaster()) : instance;
    }

    private TextMaster() {
    }

    public void render() {
        renderer.render(texts);
    }

    public void loadText(Text text) {
        if (text == null)
            throw new IllegalArgumentException("Invalid text.");

        FontType font = text.getFont();
        TextMeshData data = font.loadText(text);
        int vao = Loader.getInstance().loadToVAO(data.getVertexPositions(), data.getTextureCoords());
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
