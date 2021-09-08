package util.parsing.colladaParser.dataStructures;

import java.io.File;
import util.parsing.MaterialColor;

public class MaterialData {

    private final String id;
    private final String effectId;
    private final String name;

    private MaterialColor emission;
    private MaterialColor diffuse;
    private float specular;

    private File emissionTexture;
    private File specularTexture;
    private File diffuseTexture;

    public MaterialData(String id, String name, String effectId) {
        this.id = id;
        this.effectId = effectId;
        this.name = name;
    }

    public File getEmissionTexture() {
        return this.emissionTexture;
    }

    public void setEmissionTexture(File emissionTexture) {
        this.emissionTexture = emissionTexture;
    }

    public File getSpecularTexture() {
        return this.specularTexture;
    }

    public void setSpecularTexture(File specularTexture) {
        this.specularTexture = specularTexture;
    }

    public File getDiffuseTexture() {
        return this.diffuseTexture;
    }

    public void setDiffuseTexture(File diffuseTexture) {
        this.diffuseTexture = diffuseTexture;
    }

    public MaterialColor getEmission() {
        return this.emission;
    }

    public void setEmission(MaterialColor emission) {
        this.emission = emission;
    }

    public float getSpecular() {
        return this.specular;
    }

    public void setSpecular(float specular) {
        this.specular = specular;
    }

    public MaterialColor getDiffuse() {
        return this.diffuse;
    }

    public void setDiffuse(MaterialColor diffuse) {
        this.diffuse = diffuse;
    }

    public String getId() {
        return this.id;
    }

    public String getEffectId() {
        return this.effectId;
    }

    public String getName() {
        return this.name;
    }

}
