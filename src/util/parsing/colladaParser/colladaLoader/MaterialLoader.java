package util.parsing.colladaParser.colladaLoader;

import util.parsing.colladaParser.dataStructures.MaterialData;
import util.parsing.colladaParser.xmlParser.XmlNode;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MaterialLoader {

    private final EffectLoader effectLoader;

    private final XmlNode materialsDataNode;

    public MaterialLoader(File parentFile, XmlNode materialsNode, XmlNode effectsNode, XmlNode imagesNode) {
        this.materialsDataNode = materialsNode;
        this.effectLoader = new EffectLoader(effectsNode, new ImageLoader(imagesNode, parentFile));
    }

    public List<MaterialData> extractMaterialsData() {
        return this.materialsDataNode.getChildren("material").stream().map(materialNode -> {
            String materialID = materialNode.getAttribute("id");
            String materialName = materialNode.getAttribute("name");
            XmlNode instanceEffectNode = materialNode.getChild("instance_effect");
            if (instanceEffectNode == null)
                return null;

            String effectURL = instanceEffectNode.getAttribute("url").substring(1);
            MaterialData materialData = new MaterialData(materialID, materialName, effectURL);
            this.effectLoader.extractEffect(materialData, effectURL);
            return materialData;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }
}