package util.parsing.colladaParser.colladaLoader;

import renderEngine.MeshData;
import util.parsing.colladaParser.dataStructures.*;
import util.parsing.colladaParser.xmlParser.XmlNode;
import util.parsing.colladaParser.xmlParser.XmlParser;

import java.io.File;
import java.util.List;

public class ColladaLoader {

    public static AnimatedModelData loadColladaModel(File colladaFile, int maxWeights) {
        XmlNode node = XmlParser.loadXmlFile(colladaFile);

        SkinLoader skinLoader = new SkinLoader(node.getChild("library_controllers"), maxWeights);
        SkinningData skinningData = skinLoader.extractSkinData();

        SkeletonLoader jointsLoader = new SkeletonLoader(node.getChild("library_visual_scenes"),
                skinningData.jointOrder);
        SkeletonData jointsData = jointsLoader.extractBoneData();

        MaterialLoader materialLoader = new MaterialLoader(colladaFile.getParentFile(), node.getChild("library_materials"), node.getChild("library_effects"), node.getChild("library_images"));
        List<MaterialData> materialsData = materialLoader.extractMaterialsData();
        GeometryLoader g = new GeometryLoader(node.getChild("library_geometries"), skinningData.verticesSkinData, materialsData);
        MeshData meshData = g.extractModelData();

        return new AnimatedModelData(meshData, jointsData);
    }

    public static AnimationData loadColladaAnimation(File colladaFile) {
        XmlNode node = XmlParser.loadXmlFile(colladaFile);
        if (node.hasChild("library_animations") && node.hasChild("library_visual_scenes")) {
            XmlNode animNode = node.getChild("library_animations");
            XmlNode jointsNode = node.getChild("library_visual_scenes");
            AnimationLoader loader = new AnimationLoader(animNode, jointsNode);
            return loader.extractAnimation();
        }
        return null;
    }

}
