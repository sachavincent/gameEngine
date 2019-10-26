package fontRendering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import fontMeshCreator.FontType;
import fontMeshCreator.Text;
import fontMeshCreator.TextMeshData;
import renderEngine.Loader;

public class TextMaster {

    private static Loader                    loader;
    private static Map<FontType, List<Text>> texts = new HashMap<>();
    private static FontRenderer              renderer;

    public static void init(Loader theLoader) {
        renderer = new FontRenderer();
        loader = theLoader;
    }

    public static void render() {
        renderer.render(texts);
    }

    public static void loadText(Text text) {
        FontType font = text.getFont();
        TextMeshData data = font.loadText(text);
        int vao = loader.loadToVAO(data.getVertexPositions(), data.getTextureCoords());
        text.setMeshInfo(vao, data.getVertexCount());
        List<Text> textBatch = texts.get(font);
        if (textBatch == null) {
            textBatch = new ArrayList<>();
            texts.put(font, textBatch);
        }
        textBatch.add(text);
    }

    public static void removeText(Text text) {
        List<Text> textBatch = texts.get(text.getFont());
        textBatch.remove(text);
        if (textBatch.isEmpty())
            texts.remove(text.getFont());
    }

    public static void removeText() {
            texts = new HashMap<>();
    }

    public static void cleanUp() {
        renderer.cleanUp();
    }

}
