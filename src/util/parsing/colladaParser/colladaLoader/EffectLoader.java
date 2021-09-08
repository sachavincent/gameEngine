package util.parsing.colladaParser.colladaLoader;

import java.io.File;
import util.Utils;
import util.math.Vector3f;
import util.math.Vector4f;
import util.parsing.SimpleMaterialColor;
import util.parsing.colladaParser.dataStructures.MaterialData;
import util.parsing.colladaParser.xmlParser.XmlNode;

public class EffectLoader {

    private final ImageLoader imageLoader;
    private final XmlNode effectsNode;

    public EffectLoader(XmlNode effectsNode, ImageLoader imageLoader) {
        this.effectsNode = effectsNode;
        this.imageLoader = imageLoader;
    }

    public void extractEffect(MaterialData materialData, String effectURL) {
        this.effectsNode.getChildren("effect").forEach(effectNode -> {
            String effectId = effectNode.getAttribute("id");
            if (effectId.equals(effectURL)) {
                extractMaterialsTexture(materialData, effectNode.getChild("profile_COMMON"));
            }
        });
    }

    private MaterialData extractMaterialsTexture(MaterialData materialData, XmlNode common) {
        if (common.hasChild("technique")) {
            XmlNode lambert = common.getChild("technique").getChild("lambert");
            XmlNode emissionNode = lambert.getChild("emission");
            loadEmission(materialData, emissionNode, common);
            XmlNode diffuseNode = lambert.getChild("diffuse");
            loadDiffuse(materialData, diffuseNode, common);
            XmlNode reflectivityNode = lambert.getChild("reflectivity");
            loadReflectivity(materialData, reflectivityNode, common);
        }
        return materialData;
    }

    private void loadEmission(MaterialData materialData, XmlNode emissionNode, XmlNode common) {
        if (emissionNode.hasChild("color")) { // Emission as Vector
            Vector4f emissionColor = null;
            try {
                emissionColor = Utils.parseVector(Vector4f.class, emissionNode.getChild("color").getData().split(" "));
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
            materialData.setEmission(new SimpleMaterialColor(new Vector3f(emissionColor)));
        } else if (emissionNode.hasChild("texture")) { // Emission as Map
            File emissionFile = extractFileFromTextureName(emissionNode, common);
            materialData.setEmissionTexture(emissionFile);
        }
    }

    private void loadDiffuse(MaterialData materialData, XmlNode diffuseNode, XmlNode common) {
        if (diffuseNode.hasChild("color")) { // Diffuse as Vector
            Vector4f diffuseColor = null;
            try {
                diffuseColor = Utils.parseVector(Vector4f.class, diffuseNode.getChild("color").getData().split(" "));
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
            materialData.setDiffuse(new SimpleMaterialColor(new Vector3f(diffuseColor)));
        } else if (diffuseNode.hasChild("texture")) { // Diffuse as Map
            File diffuseFile = extractFileFromTextureName(diffuseNode, common);
            materialData.setDiffuseTexture(diffuseFile);
        }
    }

    private void loadReflectivity(MaterialData materialData, XmlNode reflectivityNode, XmlNode common) {
        if (reflectivityNode == null)
            return;

        if (reflectivityNode.hasChild("float")) { // Reflectivity as Float
            float specular = Float.parseFloat(reflectivityNode.getChild("float").getData());
            materialData.setSpecular(specular);
        } else if (reflectivityNode.hasChild("texture")) { // Reflectivity as Texture
            File specularFile = extractFileFromTextureName(reflectivityNode, common);
            materialData.setSpecularTexture(specularFile);
        }
    }

    private File extractFileFromTextureName(XmlNode textureNode, XmlNode common) {
        String texture = textureNode.getChild("texture").getAttribute("texture");
        XmlNode newparam = common.getChildWithAttribute("newparam", "sid", texture);
        String sourceFile = newparam.getChild("sampler2D").getChild("source").getData();
        newparam = common.getChildWithAttribute("newparam", "sid", sourceFile);
        String sourceImage = newparam.getChild("surface").getChild("init_from").getData();
        return this.imageLoader.extractFile(sourceImage);
    }
}