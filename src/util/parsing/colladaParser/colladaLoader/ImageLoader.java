package util.parsing.colladaParser.colladaLoader;

import util.parsing.colladaParser.xmlParser.XmlNode;

import java.io.File;

public class ImageLoader {

    private final XmlNode libImageNode;
    private final File parentFile;

    public ImageLoader(XmlNode libImageNode, File parentFile) {
        this.libImageNode = libImageNode;
        this.parentFile = parentFile;
    }

    public File extractFile(String fileName) {
        XmlNode imageNode = this.libImageNode.getChildWithAttribute("image", "id", fileName);
        if (imageNode == null)
            return null;
        XmlNode image = imageNode.getChild("init_from");
        return new File(this.parentFile, image.getData());
    }
}
